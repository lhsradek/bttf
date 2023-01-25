package local.intranet.bttf.api.controller

import java.util.concurrent.atomic.AtomicInteger

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

import local.intranet.bttf.BttfApplication
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.LoggingEventInfo
import local.intranet.bttf.api.service.LoggingEventService
import local.intranet.bttf.api.service.UserService

import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.lang.Nullable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.HttpServerErrorException.InternalServerError

@Controller
public class IndexController {

    private val log = LoggerFactory.getLogger(IndexController::class.java)

    @Value("\${bttf.app.debug:false}") private lateinit var dbg: String // toBoolean
    @Value("\${bttf.app.headerSoftware:false}") private lateinit var headerSoftware: String
    @Value("\${bttf.app.logCnt:25}") private lateinit var logCnt: String // toInt

    @Autowired private lateinit var statusController: StatusController
    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var loggingEventService: LoggingEventService

    private val INDEX: String = "index"
    private val INDEX_API: String = "bttfApi"
    private val INDEX_LICENSE: String = "license"
    private val INDEX_LOG: String = "bttfLog"
    private val INDEX_LOGS_PAGE: String = "logsPage"
    private val INDEX_LOGS_TOTAL: String = "logsTotal"
    private val INDEX_LOGS_COUNTER: String = "logsTotalCounter"
    private val INDEX_LOGS_SORT: String = "logsSort"
    private val INDEX_LOGS_FILTER: String = "logsFilter"
    private val INDEX_LOGS_COUNT: String = "logsCnt"
    private val INDEX_LOGS_MAX: String = "logsMax"
    private val INDEX_BTTF_LOGS: String = "bttfLogs"
    private val INDEX_LOGIN: String = "login"
    private val INDEX_STATUS: String = "status"
    private val INDEX_ERROR: String = "error"
    private val INDEX_STATUS_SPRING_BOOT_VERSION: String = "springBootVersion"
    private val INDEX_STATUS_SPRING_VERSION: String = "springVersion"
    private val INDEX_HEADER_SOFTWARE: String = "headerSoftware"
    private val INDEX_STAGE: String = "stage"
    private val INDEX_SERVER_NAME: String = "serverName"
    private val INDEX_SERVER_SOFTWARE: String = "serverSoftware"
    private val INDEX_NEXT: String = "next"
    private val INDEX_PREV: String = "prev"
    private val INDEX_ACTIVE_PROFILES: String = "activeProfiles"
    private val INDEX_USERNAME: String = "username"
    private val INDEX_ROLE: String = "role"
    private val INDEX_USER_ROLES: String = "userRoles"

    /**
     *
     * HTML License info
     * <p>
     * The method getLicense for /license
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "license" for thymeleaf license.html {@link String}
     */
    @GetMapping(value = arrayOf("/license"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    fun getLicense(request: HttpServletRequest, model: Model): String {
        addModel(request, model)
        // model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        log.info("GetLicense '{}' session:{}", model.asMap().get(INDEX_USERNAME), request.requestedSessionId)
        return INDEX_LICENSE
    }

    /**
     *
     * HTML Index
     * <p>
     * The method getIndex for root /
     *
     * @param pg      {@link Int?}
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "index" for thymeleaf index.html {@link String}
     */
    @GetMapping(value = arrayOf("/", "/page/{page}"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    fun getIndex(
        @PathVariable(value = "page", required = false) pg: Int?,
        request: HttpServletRequest, model: Model
    ): String {
        addModel(request, model)
        val page: AtomicInteger = getPage(pg, 0)
        model.addAttribute(INDEX_STATUS_SPRING_BOOT_VERSION, SpringBootVersion.getVersion())
        model.addAttribute(INDEX_STATUS_SPRING_VERSION, SpringVersion.getVersion())
        model.addAttribute(INDEX_API, BttfApplication::class.java.name.split(".").last())
        model.addAttribute(BttfConst.IMPLEMENTATION_VERSION, statusController.getImplementationVersion())
        // model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        log.info(
            "GetIndex '{}' page:{} session:{}",
            model.asMap().get(INDEX_USERNAME),
            page.get(),
            request.requestedSessionId
        )
        return INDEX
    }

    /**
     *
     * HTML Log
     * <p>
     * The method getBttfLog for /bttfLog
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE} and
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     *
     * @param pg      {@link Int?}    Number from 0. Zero is the first page
     * @param srt     {@link String?} Sort by [idD, idU, mD, mU, a0D, a0U, a1D, a1U, a2D, a2U, a3D, a3U, cU, cD, lU, lD]
     * @param filter  {@link String?} Empty or filter by [DEBUG, ERROR, INFO, WARN]
     *                or their combinations. Example: 'ERROR+WARN'
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "indexLog" for thymeleaf indexLog.html {@link String}
     */
    @GetMapping(
        value = arrayOf(
            "/bttfLog",
            "/bttfLog/page/{page}",
            "/bttfLog/page/{page}/sort/{sort}",
            "/bttfLog/page/{page}/sort/{sort}/filter/{filter}"
        ),
        produces = arrayOf(MediaType.TEXT_HTML_VALUE)
    )
    @PreAuthorize("permitAll()")
    // @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    fun getBttfLog(
        @PathVariable(value = "page", required = false) pg: Int?,
        @PathVariable(value = "sort", required = false) srt: String?,
        @PathVariable(required = false) filter: String?,
        request: HttpServletRequest,
        model: Model
    ): String {
        var ctle: List<LevelCount> = loggingEventService.countTotalLoggingEvents()
        val levelString = mutableListOf<String>()
        var fil: String
        if (filter == null || filter.length == 0) {
            fil = "0"
            for (l: LevelCount in ctle)
                levelString.add(l.level)
        } else {
            fil = filter
            for (s: String in filter.split("\\+")) {
                if (arrayOf("DEBUG", "ERROR", "INFO", "WARN").contains(s))
                    levelString.add(s)
                else {
                    levelString.clear()
                    break
                }
            }
            if (levelString.size == 0) {
                fil = "0"
                for (l: LevelCount in ctle)
                    levelString.add(l.level)
            }
        }
        // Up, Down
        val sort: String
        if (srt == null || srt.length == 0 || !arrayOf(
                "idU", "idD", "mU", "mD", "a0U", "a0D", "a1U", "a1D", "a2U", "a2D", "a3U", "a3D", "cU", "cD", "lU", "lD"
            ).contains(srt)
        )
            sort = "idD"
        else
            sort = srt
        val page: AtomicInteger = getPage(pg, Integer.MAX_VALUE)
        addModel(request, model)
        val order: List<Order> = logSortByParam(sort)
        val pageable: Pageable = PageRequest.of(page.get(), logCnt.toInt(), Sort.by(order))
        var lg: Page<LoggingEventInfo> = loggingEventService.findPageByLevelString(pageable, levelString)
        val cnt: Long = lg.getTotalElements()
        val max: Int = if (lg.getTotalPages() > 0) (lg.getTotalPages() - 1) else 0
        val newPage: Int = getPage(page.get(), max).get()
        if (newPage != page.get()) {
            page.set(newPage)
            lg = loggingEventService.findPageByLevelString(pageable, levelString)
        }
        model.addAttribute(INDEX_ACTIVE_PROFILES, statusController.getActiveProfiles())
        model.addAttribute(INDEX_LOGS_TOTAL, ctle)
        model.addAttribute(INDEX_LOGS_COUNTER, AtomicInteger())
        model.addAttribute(INDEX_BTTF_LOGS, lg.getContent())
        model.addAttribute(INDEX_LOGS_COUNT, cnt)
        model.addAttribute(INDEX_LOGS_MAX, max)
        model.addAttribute(INDEX_LOGS_PAGE, page)
        model.addAttribute(INDEX_LOGS_SORT, sort)
        model.addAttribute(INDEX_LOGS_FILTER, fil)
        setPage(page, max, model)
        // model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        // if (dbg.toBoolean()) log.debug( "GetBttfLog '{}' page:{} session:{}", model.asMap().get(INDEX_USERNAME), page.get(), request.requestedSessionId )
        return INDEX_LOG
    }

    /**
     *
     * logSortByParam for {@link #getBttfLog}
     *
     * @param srt {@link String} Sort by [idD, idU, mD, mU, a0D, a0U, a1D, a1U, a2D, a2U, a3D, a3U, cU, cD, lU, lD]
     * @return {@link List}&lt;{@link Order}&gt;
     */
    protected fun logSortByParam(srt: String): List<Order> {
        val ret = mutableListOf<Order>()
        when (srt) {
            "idU" -> ret.add(Order.asc("id"))
            "idD" -> ret.add(Order.desc("id"))
            "mU" -> {
                ret.add(Order.asc("formattedMessage").ignoreCase()); ret.add(Order.asc("id"))
            }
            "mD" -> {
                ret.add(Order.desc("formattedMessage").ignoreCase()); ret.add(Order.desc("id"))
            }
            "a0U" -> {
                ret.add(Order.asc("arg0").ignoreCase()); ret.add(Order.asc("id"))
            }
            "a0D" -> {
                ret.add(Order.desc("arg0").ignoreCase()); ret.add(Order.desc("id"))
            }
            "a1U" -> {
                ret.add(Order.asc("arg1").ignoreCase()); ret.add(Order.asc("id"))
            }
            "a1D" -> {
                ret.add(Order.desc("arg1").ignoreCase()); ret.add(Order.desc("id"))
            }
            "a2U" -> {
                ret.add(Order.asc("arg2").ignoreCase()); ret.add(Order.asc("id"))
            }
            "a2D" -> {
                ret.add(Order.desc("arg2").ignoreCase()); ret.add(Order.desc("id"))
            }
            "a3U" -> {
                ret.add(Order.asc("arg3").ignoreCase()); ret.add(Order.asc("id"))
            }
            "a3D" -> {
                ret.add(Order.desc("arg3").ignoreCase()); ret.add(Order.desc("id"))
            }
            "cU" -> {
                ret.add(Order.asc("callerMethod").ignoreCase()); ret.add(Order.asc("id"))
            }
            "cD" -> {
                ret.add(Order.desc("callerMethod").ignoreCase()); ret.add(Order.desc("id"))
            }
            "lU" -> {
                ret.add(Order.asc("levelString").ignoreCase()); ret.add(Order.asc("id"))
            }
            "lD" -> {
                ret.add(Order.desc("levelString").ignoreCase()); ret.add(Order.desc("id"))
            }
            else -> ret.add(Order.desc("id"))
        }
        return ret
    }

    /**
     *
     * Get Page
     *
     * @param pg    {@link Int?}
     * @param max   {@link Int}
     * @return      {@link AtomicInteger}
     */
    protected fun getPage(@Nullable pg: Int?, max: Int): AtomicInteger {
        val page = AtomicInteger()
        pg?.let {
            page.set(pg)
            if (page.get() < 0 || page.get() > max)
                page.set(0)
        } ?: page.set(0)
        return page
    }

    /**
     *
     * Set page
     *
     * @param page  {@link AtomicInteger}
     * @param max   int
     * @param model {@link Model}
     */
    protected fun setPage(page: AtomicInteger, max: Int, model: Model) {
        if (page.get() > 0)
            model.addAttribute(INDEX_PREV, page.get() - 1)
        else
            model.addAttribute(INDEX_PREV, page.get())
        if (page.get() < max)
            model.addAttribute(INDEX_NEXT, page.get() + 1)
        else
            model.addAttribute(INDEX_NEXT, page.get())
    }

    /**
     *
     * Get Error with statusCode
     * <p>
     * and text HttpStatus.valueOf(statusCode).getReasonPhrase()
     * <p>
     * The method getError for /error
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "error" for thymeleaf error.html {@link String}
     */
    @RequestMapping(
        value = arrayOf("/error"),
        method = arrayOf(RequestMethod.GET, RequestMethod.POST),
        produces = arrayOf(MediaType.TEXT_HTML_VALUE)
    )
    @PreAuthorize("permitAll()")
    fun getError(request: HttpServletRequest, model: Model): String {
        try {
            var statusCode: Int = 200
            val status: Any? = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
            status?.let { statusCode = Integer.valueOf(status.toString()) }
            val statusText = HttpStatus.valueOf(statusCode).reasonPhrase
            model.addAttribute(INDEX_STATUS, statusCode)
            model.addAttribute(INDEX_ERROR, statusText)
            addModel(request, model)
            //  model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        } catch (e: Exception) {
            when (e) {
                is UsernameNotFoundException,
                is AccountExpiredException,
                is BadCredentialsException,
                is LockedException,
                is InternalServerError -> {
                    if (e is InternalServerError) {
                        log.error(e.message + " " + e.stackTrace, e)
                        throw e
                    } else
                        log.error(e.message, e)
                }
                else -> throw e
            }
        }
        return INDEX_ERROR
    }

    /**
     *
     * Add model for every index links
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     */
    protected fun addModel(request: HttpServletRequest, model: Model) {
        model.addAttribute(INDEX_HEADER_SOFTWARE, headerSoftware)
        model.addAttribute(INDEX_ACTIVE_PROFILES, statusController.getActiveProfiles())
        model.addAttribute(INDEX_STAGE, statusController.getStage())
        model.addAttribute(INDEX_SERVER_NAME, statusController.getServerName())
        model.addAttribute(INDEX_SERVER_SOFTWARE, statusController.getServerSoftware())
        model.addAttribute(INDEX_USERNAME, userService.getUsername())
        model.addAttribute(INDEX_USER_ROLES, userService.getUserRoles())
        model.addAttribute(INDEX_ROLE, userService.getAuthoritiesRoles().joinToString(separator = " "))
        // model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        if (dbg.toBoolean()) log.debug("AddModel model:'{}'", request.toString())
    }

}

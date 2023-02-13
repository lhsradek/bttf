package local.intranet.bttf.api.controller

import java.util.concurrent.atomic.AtomicInteger
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.AbstractMap.SimpleEntry
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import local.intranet.bttf.BttfApplication
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.LoggingEventInfo
import local.intranet.bttf.api.info.content.Provider
import local.intranet.bttf.api.security.AESUtil
import local.intranet.bttf.api.service.BttfService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.LoggingEventService
import local.intranet.bttf.api.service.UserService
import org.jetbrains.annotations.NotNull
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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.savedrequest.DefaultSavedRequest
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.client.HttpServerErrorException.InternalServerError

@Controller
public class IndexController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Value("\${bttf.app.headerSoftware:false}")
    private lateinit var headerSoftware: String // toBoolean

    @Value("\${bttf.app.logCnt:25}")
    private lateinit var logCnt: String // toInt

    @Value("\${bttf.sec.key}")
    private lateinit var key: String

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var loggingEventService: LoggingEventService

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var provider: Provider

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
    public fun getLicense(request: HttpServletRequest, model: Model): String {
        addModel(request, model)
        with(model) {
            // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
            request.requestedSessionId?.let {
                log.info(
                    "GetLicense username:'{}' ip:'{}' session:{}", asMap().get("username"),
                    statusController.clientIP(), request.requestedSessionId
                )
            } ?: log.info("GetLicense username:'{}' ip:'{}'", asMap().get("username"), statusController.clientIP())
        }
        return "license"
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
    @GetMapping(value = arrayOf("/"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    public fun getIndex(
        request: HttpServletRequest, model: Model
    ): String {
        addModel(request, model)
        with(model) {
            addAttribute("springBootVersion", SpringBootVersion.getVersion())
            addAttribute("springVersion", SpringVersion.getVersion())
            addAttribute("bttfApi", BttfApplication::class.java.name.split(".").last())
            addAttribute("implementationVersion", statusController.implementationVersion())
            // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
            request.requestedSessionId?.let {
                log.info(
                    "GetIndex username:'{}' ip:'{}' session:{}",
                    asMap().get("username"), statusController.clientIP(), request.requestedSessionId
                )
            } ?: log.info(
                "GetIndex username:'{}' ip:'{}'", asMap().get("username"), statusController.clientIP()
            )
        }
        return "index"
    }

    /**
     * 
     * HTML Properties
     * <p>
     * The method getProperties for /properties
     * <p>
     * Accessible to the
     * {@link local.intranet.tombola.api.domain.type.RoleType#ADMIN_ROLE}.
     * 
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "properties" for thymeleaf properties.html {@link String}
     */
    @GetMapping(value = arrayOf("/properties"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("hasRole('ROLE_adminRole')")
    public fun getProperties(request: HttpServletRequest, model: Model): String {
        addModel(request, model)
        with(model) {
            addAttribute("bttfBeans", statusController.propertiesAPIBean())
            addAttribute("hostName", statusController.hostName())
            addAttribute("serverPort", 8080)
            addAttribute("bttfProperties", statusController.bttfProperties())
            addAttribute("bttfHttpServletRequest", statusController.bttfHttpServletRequest())
            addAttribute("bttfServletContext", statusController.bttfServletContext())
            addAttribute("bttfEnvironment", statusController.bttfEnvironment())
            // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
            request.requestedSessionId?.let {
                log.info(
                    "GetProperties username:'{}' ip:'{}' session:{}",
                    asMap().get("username"), statusController.clientIP(), request.requestedSessionId
                )
            } ?: log.info(
                "GetProperties username:'{}' ip:'{}'", asMap().get("username"), statusController.clientIP()
            )
        }
        return "properties"
    }

    /**
     *
     * HTML Play
     * <p>
     * The method getIndex for play
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "play" for thymeleaf play.html {@link String}
     */
    @GetMapping(value = arrayOf("/play"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    public fun getPlay(request: HttpServletRequest, model: Model): String {
        try {
            with(request) {
                val time =
                    if (session != null && session.getAttribute(BttfConst.APPLICATION_YEAR) != null) {
                        val z = ZonedDateTime.now(ZoneId.systemDefault())
                        z.plusYears(session.getAttribute(BttfConst.APPLICATION_YEAR) as Long - z.year)
                    } else {
                        ZonedDateTime.now(ZoneId.systemDefault())
                    }
                addModel(request, model)
                val iv = AESUtil.generateIv()
                val salt = AESUtil.generateSalt()
                val secretKey = AESUtil.getKeyFromPassword(key, salt)
                with(model) {
                    addAttribute("bttfApi", BttfApplication::class.java.name.split(".").last())
                    addAttribute("implementationVersion", statusController.implementationVersion())
                    addAttribute("now", ZonedDateTime.now(ZoneId.systemDefault()))
                    addAttribute("time", time)
                    addAttribute("secretIv", iv)
                    addAttribute("secretKey", secretKey)
                    // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
                    requestedSessionId?.let {
                        session.setAttribute(BttfConst.APPLICATION_SALT, AESUtil.setHex(salt))
                        session.setAttribute(BttfConst.APPLICATION_SECRET_IV, iv.getIV())
                        log.info(
                            "GetPlay username:'{}' ip:'{}' time:{} session:{}",
                            asMap().get("username"), statusController.clientIP(),
                            time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")), requestedSessionId
                        )
                    } ?: log.info(
                        "GetPlay username:'{}' ip:'{}' time:{}",
                        asMap().get("username"), statusController.clientIP(),
                        time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))
                    )
                }
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchPaddingException,
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is InvalidKeyException,
                is InvalidKeySpecException,
                is BadPaddingException,
                is IllegalBlockSizeException -> {
                    log.warn("GetPlay error:'{}' message:'{}'", e::class.java.simpleName, e.message)
                }
                is InternalServerError -> {
                    log.error(e.message, e)
                }
            }
            throw e
        }
        return "play"
    }

    /**
     *
     * HTML Play
     *
     * @param cryptedYear {@link String?}
     * @param request {@link HttpServletRequest}
     * @return {@String}
     */
    @PostMapping(
        value = arrayOf("/play/year/{year}"),
        consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE),
        produces = arrayOf(MediaType.TEXT_HTML_VALUE)
    )
    @PreAuthorize("permitAll()")
    public fun postPlay(
        @PathVariable(value = "year", required = false) cryptedYear: String?, request: HttpServletRequest
    ): String {
        try {
            with(request) {
                val time = if (session != null && cryptedYear != null &&
                    session.getAttribute(BttfConst.APPLICATION_SALT) != null &&
                    session.getAttribute(BttfConst.APPLICATION_SECRET_IV) != null
                ) {
                    val salt = AESUtil.getHex(session.getAttribute(BttfConst.APPLICATION_SALT) as String)
                    val iv = IvParameterSpec(session.getAttribute(BttfConst.APPLICATION_SECRET_IV) as ByteArray)
                    val year =
                        AESUtil.decrypt(AESUtil.getHex(cryptedYear), AESUtil.getKeyFromPassword(key, salt), iv)
                            .toLong()
                    val z = ZonedDateTime.now(ZoneId.systemDefault())
                    z.plusYears(year - z.year)
                } else {
                    ZonedDateTime.now(ZoneId.systemDefault())
                }
                if (session != null) {
                    session.setAttribute(BttfConst.APPLICATION_YEAR, time.year.toLong())
                }
                val username = userService.username()
                // model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
                requestedSessionId?.let {
                    log.info(
                        "PostPlay username:'{}' ip:'{}' time:{} session:{}",
                        username, statusController.clientIP(), requestedSessionId,
                        time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))
                    )
                } ?: log.info(
                    "PostPlay username:'{}' ip:'{}' time:{}", username, statusController.clientIP(),
                    time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z"))
                )
            }
        } catch (e: Exception) {
            when (e) {
                is NoSuchPaddingException,
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is InvalidKeyException,
                is InvalidKeySpecException,
                is BadPaddingException,
                is IllegalBlockSizeException -> {
                    log.warn("PostPlay error:'{}' message:'{}'", e::class.java.simpleName, e.message)
                }
            }
            throw e
        }
        return "redirect: /bttf/play"
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
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    public fun getBttfLog(
        @PathVariable(value = "page", required = false) pg: Int?,
        @PathVariable(value = "sort", required = false) srt: String?,
        @PathVariable(required = false) filter: String?,
        request: HttpServletRequest,
        model: Model
    ): String {
        val ctle: List<LevelCount> = loggingEventService.countTotalLoggingEvents()
        val levelString = mutableListOf<String>()
        val fil: String
        if (filter == null || filter.length == 0) {
            fil = "0"
            ctle.forEach { levelString.add(it.level) }
        } else {
            for (s: String in filter.split("+")) {
                if (arrayOf("DEBUG", "ERROR", "INFO", "WARN").contains(s)) {
                    levelString.add(s)
                } else {
                    levelString.clear()
                    break
                }
            }
            if (levelString.size == 0) {
                fil = "0"
                ctle.forEach { levelString.add(it.level) }
            } else {
                fil = filter
            }
        }
        // Up, Down
        val sort: String
        if (srt == null || srt.length == 0 || !arrayOf(
                "idU", "idD", "mU", "mD", "a0U", "a0D", "a1U", "a1D", "a2U", "a2D", "a3U", "a3D", "cU", "cD", "lU", "lD"
            ).contains(srt)
        ) {
            sort = "idD"
        } else {
            sort = srt
        }
        val page: AtomicInteger = getPage(pg, Integer.MAX_VALUE)
        addModel(request, model)
        val order = logSortByParam(sort)
        val pageable = PageRequest.of(page.get(), logCnt.toInt(), Sort.by(order))
        var lg: Page<LoggingEventInfo> = loggingEventService.findPageByLevelString(pageable, levelString)
        val cnt= lg.totalElements
        val max = if (lg.totalPages > 0) (lg.totalPages - 1) else 0
        val newPage = getPage(page.get(), max).get()
        if (newPage != page.get()) {
            page.set(newPage)
            lg = loggingEventService.findPageByLevelString(pageable, levelString)
        }
        with(model) {
            addAttribute("logsTotal", ctle)
            addAttribute("logsTotalCounter", AtomicInteger())
            addAttribute("bttfLogs", lg.content)
            addAttribute("logsCnt", cnt)
            addAttribute("logsMax", max)
            addAttribute("logsPage", page)
            addAttribute("logsSort", sort)
            addAttribute("logsFilter", fil)
            setPage(page, max, model)
            // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
            // if (dbg.toBoolean()) log.debug( "GetBttfLog '{}' page:{} session:{}", asMap().get(INDEX_USERNAME), page.get(), request.requestedSessionId )
        }
        return "bttfLog"
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
        with(ret) {
            when (srt) {
                "idU" -> add(Order.asc("id"))
                "idD" -> add(Order.desc("id"))
                "mU" -> {
                    add(Order.asc("formattedMessage").ignoreCase())
                    add(Order.asc("id"))
                }
                "mD" -> {
                    add(Order.desc("formattedMessage").ignoreCase())
                    add(Order.desc("id"))
                }
                "a0U" -> {
                    add(Order.asc("arg0").ignoreCase())
                    add(Order.asc("id"))
                }
                "a0D" -> {
                    add(Order.desc("arg0").ignoreCase())
                    add(Order.desc("id"))
                }
                "a1U" -> {
                    ret.add(Order.asc("arg1").ignoreCase())
                    ret.add(Order.asc("id"))
                }
                "a1D" -> {
                    add(Order.desc("arg1").ignoreCase())
                    add(Order.desc("id"))
                }
                "a2U" -> {
                    ret.add(Order.asc("arg2").ignoreCase())
                    ret.add(Order.asc("id"))
                }
                "a2D" -> {
                    add(Order.desc("arg2").ignoreCase())
                    add(Order.desc("id"))
                }
                "a3U" -> {
                    ret.add(Order.asc("arg3").ignoreCase())
                    ret.add(Order.asc("id"))
                }
                "a3D" -> {
                    add(Order.desc("arg3").ignoreCase())
                    add(Order.desc("id"))
                }
                "cU" -> {
                    add(Order.asc("callerMethod").ignoreCase())
                    add(Order.asc("id"))
                }
                "cD" -> {
                    add(Order.desc("callerMethod").ignoreCase())
                    add(Order.desc("id"))
                }
                "lU" -> {
                    add(Order.asc("levelString").ignoreCase())
                    add(Order.asc("id"))
                }
                "lD" -> {
                    add(Order.desc("levelString").ignoreCase())
                    add(Order.desc("id"))
                }
                else -> add(Order.desc("id"))
            }
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
        with(page) {
            pg?.let {
                set(pg)
                if (get() < 0 || get() > max) set(0)
            } ?: set(0)
        }
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
    protected fun setPage(page: AtomicInteger, max: Int, model: Model) = with(model) {
        if (page.get() > 0) {
            addAttribute("prev", page.get() - 1)
        } else {
            addAttribute("prev", page.get())
        }
        if (page.get() < max) {
            addAttribute("next", page.get() + 1)
        } else {
            addAttribute("next", page.get())
        }
    }

    /**
     *
     * Log in
     * <p>
     * The method getLogin for /login
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "login" for thymeleaf login.html {@link String}
     */
    @GetMapping(value = arrayOf("/login"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    public fun getLogin(request: HttpServletRequest, model: Model): String {
        val err = getErrorMessage(request, model)
        with(request) {
            if (session != null && err.equals("OK")) {
                session.removeAttribute(BttfConst.LAST_EXCEPTION)
            } else if (session != null) {
                session.setAttribute(BttfConst.LAST_EXCEPTION, BttfException(err))
            }
        }
        addModel(request, model)
        with(model) {
            val isAuthenticated = getAttribute("isAuthenticated") as Boolean
            with(request) {
                if (getQueryString() != null && queryString.startsWith("error") && !isAuthenticated) {
                    log.warn("queryString:{} path:'{}'", getQueryString(), "")
                }
            }
            addAttribute("bttfApi", BttfApplication::class.java.name.split(".").last())
            addAttribute("invalidRole", "Invalid role for this page!")
            // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        }
        return "login"
    }

    /**
     *
     * Sign in from log in form
     * <p>
     * The method signin for /login/signin
     * <p>
     * For {@link #getLogin} after submit &lt;Log in&gt; or press
     * &lt;&crarr;Enter&gt;
     *
     * @param username {@link String} Username
     * @param password {@link String} Password
     * @param request  {@link HttpServletRequest}
     * @return redirect url {@link String}
     */
    @PostMapping(path = arrayOf("/login/signin"), consumes = arrayOf(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
    public fun signin(
        @RequestParam @NotNull username: String,
        @RequestParam @NotNull password: String,
        request: HttpServletRequest
    ): String {
        var redirect = "/bttf/login"
        request.session.getAttribute(BttfConst.SAVED_REQUEST)?.let {
            val savedRequest: DefaultSavedRequest? =
                request.session.getAttribute(BttfConst.SAVED_REQUEST) as DefaultSavedRequest
            savedRequest?.let {
                with (savedRequest) {
                    getRedirectUrl()?.let {
                        redirect = getRedirectUrl()
                    }
                }
            }
        }

        try {
            val user = userService.loadUserByUsername(username)
            val token = UsernamePasswordAuthenticationToken(user, password, user.authorities)
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(token))
            log.info(
                "Login username:'{}' redirect:'{}' sessionId:'{}'",
                username, redirect, request.requestedSessionId
            )
        } catch (e: Exception) {
            when (e) {
                is UsernameNotFoundException,
                is LockedException,
                is AccountExpiredException,
                is AuthenticationCredentialsNotFoundException,
                is BadCredentialsException -> {
                    val ret = "/bttf/login" + provider.queryProvider(listOf(
                        	SimpleEntry<String, String>("error", "true"),
                        	SimpleEntry<String, String>("exception", e::class.java.simpleName)
                    ))
                    val attempt = loginAttemptService.findById(statusController.clientIP())
                    log.warn("Signin username:'{}' redirect:'{}' attempt:{}", username, ret, attempt)
                    // log.error(e::class.java.simpleName, e)
                    log.error(e::class.java.simpleName)
                    request.session.setAttribute(BttfConst.LAST_EXCEPTION, e)
                    redirect = ret
                }
                else -> throw e
            }
        }
        return "redirect: " + redirect
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
    public fun getError(request: HttpServletRequest, model: Model): String {
        try {
            val status: Any? = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)
            val statusCode = status?.let {
                Integer.valueOf(status.toString())
            } ?: 200
            if (statusCode == 200) {
                request.session.removeAttribute(BttfConst.LAST_EXCEPTION)
            } else {
                val statusText = HttpStatus.valueOf(statusCode).reasonPhrase
                model.addAttribute("status", statusCode)
                if (statusCode == 500) {
                    model.addAttribute("error", BttfConst.ERROR_INTERNAL)
                } else {
                    model.addAttribute("error", statusText)
                }
                addModel(request, model)
            }
            //  model.asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        } catch (e: Exception) {
            when (e) {
                is UsernameNotFoundException,
                is AccountExpiredException,
                is BadCredentialsException,
                is AuthenticationCredentialsNotFoundException,
                is LockedException,
                is BttfException,
                is InternalServerError -> {
                    if (e is InternalServerError) {
                        log.error(e.message + " " + e.stackTrace, e)
                        // if (dbg.toBoolean()) throw e
                    } else {
                        log.error(e.message, e)
                    }
                }
                // else -> if (dbg.toBoolean()) throw e
            }
        }
        return "error"
    }

    /**
     *
     * Get error message from session
     * <p>
     * Used {@link BadCredentialsException} and {@link LockedException}
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return error message {@link String}
     */
    protected fun getErrorMessage(request: HttpServletRequest, model: Model): String {
        var ret = "OK"
        val ex: Any? = request.session.getAttribute(BttfConst.LAST_EXCEPTION)
        if (ex != null && ex is Exception) {
            when (ex) {
                is UsernameNotFoundException -> {
                    ret = BttfConst.ERROR_USERNAME_NOT_FOUND
                }
                is BadCredentialsException -> {
                    ret = BttfConst.ERROR_INVALID_USERNAME_AND_PASSWORD
                }
                is LockedException -> {
                    ret = BttfConst.ERROR_USERNAME_IS_LOCKED
                }
                is AccountExpiredException -> {
                    ret = BttfConst.ERROR_ACCOUNT_EXPIRED
                }
                is AuthenticationCredentialsNotFoundException -> {
                    ret = BttfConst.ERROR_AUTHENTICATION_CREDETIALS_NOT_FOUND
                }
                is BttfException -> {
                    ret = ex.message?.let { ex.message!! } ?: ex::class.java.simpleName
                }
                is NoSuchPaddingException,
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is InvalidKeyException,
                is InvalidKeySpecException,
                is BadPaddingException,
                is InternalServerError -> {
                    log.warn(ex.message + " " + ex.stackTrace, ex)
                    ret = BttfConst.ERROR_INTERNAL
                }
            }
        }
        if (!ret.equals("OK")) {
            model.addAttribute("getError", ret)
        }
        return ret
    }

    /**
     *
     * Add model for every index links
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     */
    protected fun addModel(request: HttpServletRequest, model: Model) = with(model) {
        addAttribute("headerSoftware", headerSoftware)
        addAttribute("activeProfiles", statusController.activeProfiles())
        addAttribute("stage", statusController.stage())
        addAttribute("serverName", statusController.serverName())
        addAttribute("serverSoftware", statusController.serverSoftware())
        addAttribute("isAuthenticated", userService.isAuthenticated())
        addAttribute("username", userService.username())
        addAttribute("userRoles", userService.userRoles())
        addAttribute("role", userService.authoritiesRoles().joinToString(separator = " "))
        val methodName = Thread.currentThread().stackTrace[2].methodName
        if (methodName.equals("getError")) {
            val err = getErrorMessage(request, model)
            if (!err.equals("OK")) {
                log.warn(
                    "AddModel error:'{}' message:'{}' code:{} path:'{}'", getAttribute("error"), err,
                    getAttribute("status"),
                    request.getAttribute(BttfConst.FORWARD_URI)
                )
            }
        }
        // asMap().forEach { log.debug("key:{} value:{}", it.key, it.value.toString()) }
        // if (dbg.toBoolean()) log.debug("AddModel model:'{}'", request.toString())
    }

}

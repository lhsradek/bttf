package local.intranet.bttf.api.controller

import local.intranet.bttf.BttfApplication
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootVersion
import org.springframework.core.SpringVersion
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.client.HttpServerErrorException.InternalServerError
import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest

@Controller
public class IndexController {

    private val logger = LoggerFactory.getLogger(IndexController::class.java)

    @Value("\${bttf.app.headerSoftware:false}")
    private lateinit var headerSoftware: String

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var userService: UserService

    private val INDEX: String = "index"
    private val INDEX_API: String = "bttfApi"
    private val INDEX_LICENSE: String = "license"
    private val INDEX_LOGIN: String = "login"
    private val INDEX_STATUS: String = "status"
    private val INDEX_ERROR: String = "error"
    private val INDEX_STATUS_SPRING_BOOT_VERSION: String = "springBootVersion"
    private val INDEX_STATUS_SPRING_VERSION: String = "springVersion"
    private val INDEX_HEADER_SOFTWARE: String = "headerSoftware"
    private val INDEX_STAGE: String = "stage"
    private val INDEX_SERVER_NAME: String = "serverName"
    private val INDEX_SERVER_SOFTWARE: String = "serverSoftware"
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
        // model.asMap().forEach({ logger.debug("key:{} value:{}", it.key, it.value.toString()) })
        return INDEX_LICENSE
    }

    /**
     *
     * HTML Index
     * <p>
     * The method getIndex for /
     * <p>
     * Accessible to the
     * {@link local.intranet.tombola.api.domain.type.RoleType#USER_ROLE}.
     *
     * @param request {@link HttpServletRequest}
     * @param model   {@link Model}
     * @return "index" for thymeleaf index.html {@link String}
     */
    @GetMapping(value = arrayOf("/"), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    fun getIndex(request: HttpServletRequest, model: Model): String {
        addModel(request, model)
        model.addAttribute(INDEX_STATUS_SPRING_BOOT_VERSION, SpringBootVersion.getVersion())
        model.addAttribute(INDEX_STATUS_SPRING_VERSION, SpringVersion.getVersion())
        model.addAttribute(INDEX_API, BttfApplication::class.java.name.split(".").last())
        model.addAttribute(BttfConst.IMPLEMENTATION_VERSION, statusController.getImplementationVersion())
        // logger.debug("GetIndex '{}' {}", model.asMap().get(INDEX_USERNAME), request.getRequestedSessionId())
        // model.asMap().forEach({ logger.debug("key:{} value:{}", it.key, it.value.toString()) })
        return INDEX
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
        method = arrayOf(RequestMethod.GET, RequestMethod.POST), produces = arrayOf(MediaType.TEXT_HTML_VALUE))
    @PreAuthorize("permitAll()")
    fun getError(request: HttpServletRequest, model: Model): String {
        try {
            var statusCode: Int = 200
            val status: Any? = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) 
            status?.let {
                statusCode = Integer.valueOf(status.toString())
            }
            val statusText = HttpStatus.valueOf(statusCode).getReasonPhrase()
            model.addAttribute(INDEX_STATUS, statusCode)
            model.addAttribute(INDEX_ERROR, statusText)
            addModel(request, model)
            // model.asMap().forEach({ logger.debug("key:{} value:{}", it.key, it.value.toString()) })
        } catch (e: Exception) {
            when (e) {
                is UsernameNotFoundException,
                is BadCredentialsException,
                is LockedException,
                is InternalServerError -> {
                    if (e is InternalServerError) {
                        logger.error(e.message + " " + e.getStackTrace(), e)
                    } else {
                        logger.error(e.message, e)
                    }
                }
                else -> throw e
            }
        }
        // logger.debug("AddModel model:'{}'", request)
        return INDEX_ERROR;
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
        val username: String = userService.getUsername()
        if (username.length > 0) {
            model.addAttribute(INDEX_USERNAME, username)
        } else {
            model.addAttribute(INDEX_USERNAME, "")
        }
        model.addAttribute(INDEX_USER_ROLES, userService.getUserRoles())
        model.addAttribute(INDEX_ROLE, userService.getAuthoritiesRoles().joinToString(separator = " "))
        model.asMap().forEach({ logger.debug("key:{} value:{}", it.key, it.value.toString()) })
        logger.debug("AddModel model:'{}'", request.toString())
    }

}

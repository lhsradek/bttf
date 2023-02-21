package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import java.util.StringJoiner
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.info.AttemptInfo
import local.intranet.bttf.api.info.BeanInfo
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.info.MessageEventInfo
import local.intranet.bttf.api.info.RoleInfo
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.service.BeanService
import local.intranet.bttf.api.service.CounterService
import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.LoggingEventService
import local.intranet.bttf.api.service.MessageService
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.MediaType
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 *
 * {@link InfoController} for {@link local.intranet.bttf.BttfApplication}
 * It's for REST methods
 * <p>
 * info from services in {@link local.intranet.bttf.api.service}
 *
 * @author Radek Kádner
 *
 */
@RestController
@RequestMapping("/api/v1/info")
@Tag(name = BttfConst.INFO_TAG)
public class InfoController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var counterService: CounterService

    @Autowired
    private lateinit var roleService: RoleService

    @Autowired(required = false)
    private lateinit var jobService: JobService

    @Autowired(required = false)
    private lateinit var messageService: MessageService

    @Autowired
    private lateinit var beanService: BeanService

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    @Autowired
    private lateinit var loggingEventService: LoggingEventService

    /**
     *
     * Bean informations
     * <p>
     * Used {@link local.intranet.bttf.api.service.BeanService#beanInfo}.
     * <p>
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/beanInfo" target=
     *      "_blank">swagger-ui/#/info-controller/beanInfo</a>
     * @return {@link BeanInfo}
     */
    @Operation(
        operationId = "beanInfo",
        summary = "Bean Info",
        description = "Get Bean Info\n\n" + "This method is calling BeanService.beanInfo\n\n",
        // "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#" +
        // "beanInfo()\" target=\"_blank\">InfoController.beanInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasRole('ROLE_userRole')")
    @GetMapping(value = arrayOf("/bean"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    public fun beanInfo(): BeanInfo = beanService.beanInfo()

    /**
     *
     * User informations
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.UserService#userInfo}.
     * <p>
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/userInfo" target=
     *      "_blank">swagger-ui/#/info-controller/userInfo</a>
     * @return {@link UserInfo}
     * @throws LockedException           if the user is locked.
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     * @throws BadCredentialsException   if the credentials are invalid
     * @throws AccountExpiredException   if an authentication request is rejected because the account has expired.
     *                                   Makes no assertion as to whether or not the credentials were valid.
     *
     * @return {@link UserDetails}
     */
    @GetMapping(value = arrayOf("/user"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "userInfo",
        summary = "User Info",
        description = "Get User Info\n\n"
                + "This method is calling UserService.userInfo\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "userInfo()\" target=\"_blank\">InfoController.userInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @Throws(UsernameNotFoundException::class, LockedException::class, BadCredentialsException::class)
    public fun userInfo(): UserDetails {
        try {
            return userService.userInfo()
        } catch (e: Exception) {
            when (e) {
                is UsernameNotFoundException,
                is LockedException,
                is AccountExpiredException,
                is BadCredentialsException -> {
                    log.error(e.message, e)
                    throw e
                }
                else -> throw e
            }
        }
    }

    /**
     *
     * Role informations
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.RoleService#roleInfo}.
     * <p>
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/roleInfo" target=
     *      "_blank">swagger-ui/#/info-controller/roleInfo</a>
     * @return {@link RoleInfo}
     *
     */
    @GetMapping(value = arrayOf("/role"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "roleInfo",
        summary = "Role Info",
        description = "Get Role Info\n\n"
                + "This method is calling RoleService.roleInfo\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "roleInfo()\" target=\"_blank\">InfoController.roleInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    public fun roleInfo(): RoleInfo = roleService.roleInfo()

    /**
     *
     * Job information
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.JobService#jobInfo}.
     * <p>
     *
     * @return {@link CounterInfo}
     */
    @GetMapping(value = arrayOf("/job"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "jobInfo",
        summary = "Job Info",
        description = "Get Job Info\n\n"
                + "This method is calling JobService.jobInfo\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "jobInfo()\" target=\"_blank\">InfoController.jobInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @ConditionalOnExpression("\${scheduler.enabled}")
    public fun jobInfo(): CounterInfo = jobService.jobInfo()

    /**
     *
     * Message information
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.MessageService#messageInfo}.
     * <p>
     *
     * @param page {@link Int}
     * @param size {@link Int}
     * @return {@link Page}&lt;{@link MessageEvent}&gt;
     */
    @GetMapping(
        value = arrayOf("/message/page/{page}/size/{size}"),
        produces = arrayOf(MediaType.APPLICATION_JSON_VALUE)
    )
    @Operation(
        operationId = "MessageInfo",
        summary = "Message Info",
        description = "Get Message Info\n\n"
                + "This method is calling MessageService.messageInfo\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "messageInfo()\" target=\"_blank\">InfoController.messageInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @ConditionalOnExpression("\${scheduler.enabled}")
    public fun messageInfo(
        @PathVariable @Parameter(
            allowEmptyValue = true, example = "0", description = "Zero-based page index (0..N)"
        ) page: Int,
        @PathVariable @Parameter(
            example = "20", description = "The size of the page to be returned"
        ) size: Int
    ):
            Page<MessageEventInfo> = messageService.messageInfo(page, size)

    /**
     *
     * Get attempts
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * <p>
     * Used {@link local.intranet.bttf.api.service.LoginAttemptService#loginAttempts}.
     *
     * @param printBlocked {@link Boolean?} as filter if not null
     * @return {@link List}&lt;{@AttemptInfo}&gt;
     */
    @GetMapping(value = arrayOf("/loginAttempts"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "loginAttempts",
        summary = "Login Attempts",
        description = "Get Login Attempts\n\n"
                + "This method get loggin attempts\n\n"
                + "printBlocked as filter if not empty value\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "loginAttempts()\" target=\"_blank\">InfoController.loginAttempts</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    public fun loginAttempts(@Parameter(allowEmptyValue = true, example = "") printBlocked: Boolean?):
            List<AttemptInfo> = loginAttemptService.loginAttempts(printBlocked)

    /**
     *
     * Count Total Logging Events
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.LoggingEventService#countTotalLoggingEvents}.
     * <p>
     *
     * @return {@link List}&lt;{@link LevelCount}&gt;
     */
    @GetMapping(value = arrayOf("/countTotalLoggingEvents"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "countTotalLoggingEvents",
        summary = "Total Logging Events",
        description = "Count Total Logging Events\n\n"
                + "This method is calling LoggingEventService.countTotalLoggingEvents\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "countTotalLoggingEvents()\" target=\"_blank\">InfoController.countTotalLoggingEvents</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    public fun countTotalLoggingEvents(): List<LevelCount> = loggingEventService.countTotalLoggingEvents()

    /**
     *
     * Count Total Message Events
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.MessageService#countTotalMessageEvents}.
     * <p>
     *
     * @return {@link List}&lt;{@link ServiceCount}&gt;
     */
    @GetMapping(value = arrayOf("/countTotalMessageEvents"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "countTotalMessageEvents",
        summary = "Total Message Events",
        description = "Count Total Message Events\n\n"
                + "This method is calling MessageService.countTotalMessageEvents\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "countTotalMessageEvents()\" target=\"_blank\">InfoController.countTotalMessageEvents</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @ConditionalOnExpression("\${scheduler.enabled}")
    public fun countTotalMessageEvents(): List<ServiceCount> = messageService.countTotalMessageEvents()

    /**
     *
     * Count Total Counter Name
     * <p>
     * Accessible to the
     * <br>
     * {@link local.intranet.bttf.api.domain.type.RoleType#MANAGER_ROLE}
     * {@link local.intranet.bttf.api.domain.type.RoleType#ADMIN_ROLE}
     * <p>
     * Used {@link local.intranet.bttf.api.service.counterService#countTotalCounterName}.
     * <p>
     *
     * @return {@link List}&lt;{@link ServiceCount}&gt;
     */
    @GetMapping(value = arrayOf("/countTotalCounterName"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "countTotalCounterName",
        summary = "Total Counter Name",
        description = "Count Total Counter Name\n\n"
                + "This method is calling CounterService.countTotalCounterName\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "countTotalCounterName()\" target=\"_blank\">InfoController.countTotalCounterName</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @ConditionalOnExpression("\${scheduler.enabled}")
    public fun countTotalCounterName(): List<ServiceCount> = counterService.countTotalCounterName()

}

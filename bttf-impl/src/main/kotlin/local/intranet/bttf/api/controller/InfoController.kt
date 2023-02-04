package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.RoleInfo
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.service.JobService
import local.intranet.bttf.api.service.LoginAttemptService
import local.intranet.bttf.api.service.LoggingEventService
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.http.MediaType
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
@RequestMapping(BttfConst.API + BttfConst.INFO_VERSION_PATH + BttfConst.INFO_BASE_INFO)
@Tag(name = BttfConst.INFO_TAG)
public class InfoController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var roleService: RoleService

    @Autowired(required = false)
    private lateinit var jobService: JobService

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    @Autowired
    private lateinit var loggingEventService: LoggingEventService


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
    public fun getUserInfo(): UserInfo {
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
    public fun roleInfo(): RoleInfo {
        return roleService.roleInfo()
    }

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
     * @return {@link List}&lt;{@link Triple}&lt;{@link String},{@link Int},{@link Long}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/job"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "jobInfo",
        summary = "Job Info",
        description = "Get Job Info\n\n"
                + "This method is calling JobService.getJobInfo\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "jobInfo()\" target=\"_blank\">InfoController.jobInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @ConditionalOnExpression("\${scheduler.enabled}")
    public fun jobInfo(): CounterInfo {
        return jobService.jobInfo()
    }

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
     * @param printBlocked {@link Boolean}
     * @return {@link List}&lt;{@link Triple}&lt;{@link String},{@link Int},{@link Long}&gt;&gt;
     */
    @GetMapping(value = arrayOf("/loginAttempts"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "loginAttempts",
        summary = "Login Attempts",
        description = "Get Login Attempts\n\n"
                + "This method get loggin attempts\n\n",
        // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
        // + "loginAttempts()\" target=\"_blank\">InfoController.loginAttempts</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    public fun loginAttempts(@Parameter(allowEmptyValue = false, example = "true") @NotNull printBlocked: Boolean):
            List<Triple<String, Int, ZonedDateTime>> {
        return loginAttemptService.loginAttempts(printBlocked)
    }

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
    public fun countTotalLoggingEvents(): List<LevelCount> {
        return loggingEventService.countTotalLoggingEvents()
    }

}

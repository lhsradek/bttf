package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation

import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.info.RoleInfo
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService

import org.slf4j.LoggerFactory

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
 * It's for charge of working with buffers and for REST methods
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

    private val log = LoggerFactory.getLogger(InfoController::class.java)

    @Autowired private lateinit var userService: UserService
    @Autowired private lateinit var roleService: RoleService


    /**
     *
     * User informations
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#USER_ROLE}.
     * <p>
     * Used {@link local.intranet.bttf.api.service.UserService#getUserInfo}.
     * <p>
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/getUserInfo" target=
     *      "_blank">swagger-ui/#/info-controller/getUserInfo</a>
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
        operationId = "getUserInfo",
        summary = "Get User Info",
        description = "Get User Info\n\n"
                + "This method is calling UserService.getUserInfo\n\n",
                // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
                // + "getUserInfo()\" target=\"_blank\">InfoController.getUserInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    @Throws(UsernameNotFoundException::class, LockedException::class, BadCredentialsException::class)
    fun getUserInfo(): UserInfo {
        try {
            return userService.getUserInfo()
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
     * {@link local.intranet.bttf.api.domain.type.RoleType#USER_ROLE}.
     * <p>
     * Used {@link local.intranet.bttf.api.service.RoleService#getRoleInfo}.
     * <p>
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/getRoleInfo" target=
     *      "_blank">swagger-ui/#/info-controller/getRoleInfo</a>
     * @return {@link RoleInfo}
     *
     */
    @GetMapping(value = arrayOf("/role"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "getRoleInfo",
        summary = "Get Role Info",
        description = "Get Role Info\n\n"
                + "This method is calling RoleService.getRoleInfo\n\n",
                // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
                // + "getRoleInfo()\" target=\"_blank\">InfoController.getRoleInfo</a>",
        tags = arrayOf(BttfConst.INFO_TAG)
    )
    @PreAuthorize("hasAnyRole('ROLE_managerRole', 'ROLE_adminRole')")
    fun getRoleInfo(): RoleInfo {
        return roleService.getRoleInfo()
    }

}

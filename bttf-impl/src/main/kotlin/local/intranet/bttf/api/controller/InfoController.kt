package local.intranet.bttf.api.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import local.intranet.bttf.api.domain.BttfController
import local.intranet.bttf.api.exception.BttfException
import local.intranet.bttf.api.info.RoleInfo
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.service.RoleService
import local.intranet.bttf.api.service.UserService

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
@RequestMapping(BttfController.API + BttfController.INFO_VERSION_PATH + BttfController.INFO_BASE_INFO)
@Tag(name = BttfController.INFO_TAG)
public class InfoController {

    val logger = LoggerFactory.getLogger(InfoController::class.java)

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var roleService: RoleService

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
     * @see {@link #getIndexInfo}
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/getUserInfo" target=
     *      "_blank">swagger-ui/#/info-controller/getUserInfo</a>
     * @return {@link UserInfo}
     */
    @GetMapping(value = arrayOf("/user"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "getUserInfo",
        summary = "Get User Info",
        description = "Get User Info\n\n"
                + "This method is calling UserService.getUserInfo\n\n"
                + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
                + "getUserInfo()\" target=\"_blank\">InfoController.getUserInfo</a>",
        tags = arrayOf(BttfController.INFO_TAG)
    )
    @PreAuthorize("hasRole('ROLE_userRole')")
    @Throws(BttfException::class)
    fun getUserInfo(): UserInfo {
        try {
            val ret = userService.getUserInfo()
            // logger.debug("{}", ret.toString())
            return ret
        } catch (e: BttfException) {
            logger.error(e.message, e)
            throw e
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
     * @see {@link #getIndexInfo}
     *
     * @see <a href="/bttf/swagger-ui/#/info-controller/getRoleInfo" target=
     *      "_blank">swagger-ui/#/info-controller/getRoleInfo</a>
     * @return {@link RoleInfo}
     */
    @GetMapping(value = arrayOf("/role"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "getRoleInfo",
        summary = "Get Role Info",
        description = "Get Role Info\n\n"
                + "This method is calling RoleService.getRoleInfo\n\n"
                + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/InfoController.html#"
                + "getRoleInfo()\" target=\"_blank\">InfoController.getRoleInfo</a>",
        tags = arrayOf(BttfController.INFO_TAG)
    )
    @PreAuthorize("hasRole('ROLE_userRole')")
    @Throws(BttfException::class)
    fun getRoleInfo(): RoleInfo {
        try {
            val ret = roleService.getRoleInfo()
            // logger.debug("{}", ret.toString())
            return ret
        } catch (e: BttfException) {
            logger.error(e.message, e)
            throw e
        }
    }

}

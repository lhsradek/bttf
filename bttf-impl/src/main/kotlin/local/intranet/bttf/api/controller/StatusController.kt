package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation

import java.util.Optional

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import local.intranet.bttf.api.domain.BttfController

/**
 *
 * {@link StatusController} for
 * {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@RestController
@RequestMapping(BttfController.API + BttfController.INFO_VERSION_PATH + BttfController.STATUS_BASE_INFO)
@Tag(name = BttfController.STATUS_TAG)
class StatusController {

	val logger = LoggerFactory.getLogger(StatusController::class.java)

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Value("\${bttf.app.stage}")
    private lateinit var stage: String
    
    @Value("\${spring.profiles.active}")
    private lateinit var profiles: String
    
    /**
     *
     * text/plain: "OK"
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#USER_ROLE}
     * 
     * @see <a href="/bttf/swagger-ui/#/status-controller/getPlainStatus" target=
     *      "_blank">bttf/swagger-ui/#/status-controller/getPlainStatus</a>
     * 
     * @return "OK" if BTTF API is running
     */
    @GetMapping(value = arrayOf("/status"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    @Operation(
        operationId = "getPlainStatus",
        summary = "Get Plain Status",
        description = "Get OK if BTTF API is running\n\n" +
                "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#getPlainStatus()\" " +
                "target=\"_blank\">StatusController.getPlainStatus</a>",
        tags = arrayOf(BttfController.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_userRole')")
    fun getPlainStatus(): String {
        return BttfController.STATUS_OK
    }
        
    /**
     *
     * Implementation version
     *
     * @return version from RequestContextUtils - HttpServletRequest -
     *         {@link Package#getImplementationVersion()}
     */
    // TODO Stub
    fun getImplementationVersion(): String {
        val ret = BttfController.STATUS_UNKNOWN
        // logger.debug("{}", ret)
        return ret
    }
    
    /**
     *
     * Get stage
     * <p>
     * Accessible to the
     * {@link local.intranet.bttf.api.domain.type.RoleType#USER_ROLE}
     * 
     * @see <a href="/bttf/swagger-ui/#/status-controller/getStage" target=
     *      "_blank">bttf/swagger-ui/#/status-controller/getStage</a>
     * 
     * @return ${bttf.app.stage}
     */
    @GetMapping(value = arrayOf("/stage"), produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @Operation(
        operationId = "getStage",
        summary = "Get Stage",
        description = "Get Stage from bttf.app.stage and spring.profiles.active\n\n" +
                "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#getStage()\" " +
                "target=\"_blank\">StatusController.getStage</a>",
        tags = arrayOf(BttfController.STATUS_TAG)
    )
    @PreAuthorize("hasRole('ROLE_userRole')")
    fun getStage(): Map<String, String> {
        val ret = mapOf(
            "stage" to stage,
            "spring.profiles.active" to profiles
        )
        // logger.debug("{}", ret)
        return ret
    }

}
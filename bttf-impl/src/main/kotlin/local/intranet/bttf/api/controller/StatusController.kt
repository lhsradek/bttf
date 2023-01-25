package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation

import java.util.Optional

import javax.servlet.ServletContext

import local.intranet.bttf.BttfApplication
import local.intranet.bttf.api.domain.BttfConst

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

/**
 *
 * {@link StatusController} for
 * {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@RestController
@RequestMapping(BttfConst.API + BttfConst.INFO_VERSION_PATH + BttfConst.STATUS_BASE_INFO)
@Tag(name = BttfConst.STATUS_TAG)
class StatusController {

    private val log = LoggerFactory.getLogger(StatusController::class.java)

    @Value("\${bttf.app.debug:false}") private lateinit var dbg: String // toBoolean
    @Value("\${bttf.app.stage}") private lateinit var stage: String

    @Autowired private lateinit var servletContext: ServletContext
    @Autowired private lateinit var applicationContext: ApplicationContext
    @Autowired private lateinit var environment: Environment

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
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    @PreAuthorize("permitAll()")
    fun getPlainStatus(): String {
        val ret: String = BttfConst.STATUS_OK
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Implementation version
     *
     * @return version BttfApplication
     */
    fun getImplementationVersion(): String {
        // val list: MutableMap<String, Any> = applicationContext.getBeansWithAnnotation(SpringBootApplication::class.java)
        // val keyFirstElement: String = list.keys.first()
        // val valueOfFirstElement: Any = list.getValue(keyFirstElement);
        // val ret: String = Optional.ofNullable(valueOfFirstElement::class.java.`package`.implementationVersion).orElse(BttfConst.STATUS_UNKNOWN)
        val ret: String = Optional.ofNullable(BttfApplication::class.java.`package`.implementationVersion)
            .orElse(BttfConst.STATUS_UNKNOWN)
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get stage
     *
     * @return ${bttf.app.stage}
     */
    fun getStage(): String {
        val ret: String = stage
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Active profiles
     *
     * @return environment.getActiveProfiles()
     */
    fun getActiveProfiles(): String {
        val ret: String = environment.getActiveProfiles().joinToString(separator = " ")
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get server name
     *
     * @return serverName (The second word) from {@link #getVirtualServerName()}
     */
    fun getServerName(): String {
        val ret: String = getVirtualServerName().split("/").last()
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get server software
     *
     * @return serverSoftware's name (The first word) without version from
     *         {@link #getServerInfo()}
     */
    fun getServerSoftware(): String {
        val ret = getServerInfo().split("/").first()
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get virtualServerName from ServletContext.getVirtualServerName()
     *
     * @return getVirtualServerName()
     */
    protected fun getVirtualServerName(): String {
        val ret: String = servletContext.getVirtualServerName()
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get server info from ServletContext.getServerInfo()
     *
     * @return servletContext.getServerInfo()
     */
    protected fun getServerInfo(): String {
        val ret: String = servletContext.getServerInfo()
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

}
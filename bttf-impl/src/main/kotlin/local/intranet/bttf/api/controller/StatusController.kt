package local.intranet.bttf.api.controller

import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Operation
import java.util.Optional
import javax.servlet.http.HttpServletRequest
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
public class StatusController {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Value("\${bttf.app.stage}")
    private lateinit var stage: String

    @Autowired
    private lateinit var servletContext: ServletContext

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @Autowired
    private lateinit var environment: Environment

    @Autowired
    private lateinit var httpServletRequest: HttpServletRequest

    /**
     *
     * text/plain: "OK" if BTTF API is running
     *
     * @see <a href="/bttf/swagger-ui/#/status-controller/getPlainStatus" target=
     *      "_blank">bttf/swagger-ui/#/status-controller/getPlainStatus</a>
     *
     * @return {@link String}
     */
    @GetMapping(value = arrayOf("/status"), produces = arrayOf(MediaType.TEXT_PLAIN_VALUE))
    @Operation(
        operationId = "plainStatus",
        summary = "Plain Status",
        description = "Get OK if BTTF API is running\n\n",
                // + "See <a href=\"/bttf-javadoc/local/intranet/bttf/api/controller/StatusController.html#getPlainStatus()\" "
                // + "target=\"_blank\">StatusController.getPlainStatus</a>",
        tags = arrayOf(BttfConst.STATUS_TAG)
    )
    public fun plainStatus(): String {
        return "OK"
    }

    /**
     *
     * Implementation version
     *
     * @return version {@link String}
     */
    public fun implementationVersion(): String {
        // val list: MutableMap<String, Any> = applicationContext.getBeansWithAnnotation(SpringBootApplication::class.java)
        // val keyFirstElement: String = list.keys.first()
        // val valueOfFirstElement: Any = list.getValue(keyFirstElement);
        // val ret: String = Optional.ofNullable(valueOfFirstElement::class.java.`package`.implementationVersion).orElse("unknown")
        val ret = Optional.ofNullable(BttfApplication::class.java.`package`.implementationVersion).orElse("unknown")
        // log.debug(ret)
        return ret
    }

    /**
     * 
     * Session id
     *
     * @return version {@link String}
     */
    public fun sessionId(): String {
        val session = httpServletRequest.getSession(false)
        val ret = session?.let {
            session.getId()?.let {
                session.getId()
            } ?: ""
        } ?: ""
        return ret;
    }

    /**
     *
     * Stage
     *
     * @return ${bttf.app.stage}
     */
    public fun stage(): String {
        return stage
    }

    /**
     *
     * Active profiles
     *
     * @return environment.getActiveProfiles()
     */
    public fun activeProfiles(): String {
        return environment.getActiveProfiles().joinToString(separator = " ")
    }

    /**
     *
     * Server name
     *
     * @return serverName (The second word) from {@link #getVirtualServerName()}
     */
    public fun serverName(): String {
        return virtualServerName().split("/").last()
    }

    /**
     *
     * Server software
     *
     * @return serverSoftware's name (The first word) without version from
     *         {@link #getServerInfo()}
     */
    public fun serverSoftware(): String {
        return serverInfo().split("/").first()
    }

    /**
     *
     * Get virtualServerName from ServletContext.getVirtualServerName()
     *
     * @return virtualServerName()
     */
    protected fun virtualServerName(): String {
        return servletContext.getVirtualServerName()
    }

    /**
     *
     * Server info from ServletContext.getServerInfo()
     *
     * @return servletContext.getServerInfo()
     */
    protected fun serverInfo(): String {
        return servletContext.getServerInfo()
    }
    
    /**
     * 
     * client IP
     * 
     * @return {@link String} as ip
     */
    public fun clientIP(): String {
        val xfHeader = httpServletRequest.getHeader("X-Forwarded-For")
        val ret = xfHeader?.let {
            xfHeader.split(",").first()
        }?: httpServletRequest.remoteAddr
        return ret
    }

}
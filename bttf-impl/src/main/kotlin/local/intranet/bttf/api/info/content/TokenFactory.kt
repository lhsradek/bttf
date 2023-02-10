package local.intranet.bttf.api.info.content

import local.intranet.bttf.api.info.content.Token
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import java.net.URI

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
@Component
public class TokenFactory {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.node.id}")
    private lateinit var nodeId: String

    @Value("\${bttf.app.node.name}")
    private lateinit var nodeName: String

    public fun tokenInstance(cl: Class<*>): Any {
        try {
            return cl.getDeclaredConstructor(URI::class.java, String::class.java)
                .newInstance(URI(nodeId), nodeName)
        } catch (e: Exception) {
            log.error("Token instance creation failed!", e)
            throw e
        }
    }

    public fun tokenInstance(tid: URI, cl: Class<*>): Any {
        try {
            return cl.getDeclaredConstructor(URI::class.java, String::class.java, URI::class.java)
                .newInstance(URI(nodeId), nodeName, tid)
        } catch (e: Exception) {
            log.error("Token instance creation failed!", e)
            throw e
        }
    }

}
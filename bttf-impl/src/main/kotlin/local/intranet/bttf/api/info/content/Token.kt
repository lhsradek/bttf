package local.intranet.bttf.api.info.content

import java.io.Serializable
import java.time.ZoneId
import java.time.ZonedDateTime
import java.net.URI
import java.net.URISyntaxException
import java.util.UUID
import java.text.MessageFormat
import org.slf4j.LoggerFactory

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public abstract class Token : Serializable {

    private val log = LoggerFactory.getLogger(javaClass)

    private val nid: URI
    private val name: String
    private val tid: URI
    private val ts: ZonedDateTime

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     */
    public constructor(nid: URI, name: String) {
        this.nid = nid
        this.name = name
        try {
            this.tid = URI("uuid", UUID.randomUUID().toString(), null)
        } catch (e: URISyntaxException) {
            log.error("Cannot created Transaction ID!", e)
            throw e
        }
        this.ts = ZonedDateTime.now(ZoneId.systemDefault())
    }

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     * @param tid  {@link URI}
     */
    public constructor(nid: URI, name: String, tid: URI) {
        this.nid = nid
        this.name = name
        this.tid = tid
        this.ts = ZonedDateTime.now(ZoneId.systemDefault())
    }

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     * @param tid  {@link URI}
     * @param ts   {@link ZonedDateTime}
     */
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) {
        this.nid = nid
        this.name = name
        this.tid = tid
        this.ts = ts
    }

    /**
     *
     * Get Nid
     *
     * @return {@URI}
     */
    public fun getNid(): URI = nid

    /**
     *
     * Get Name
     *
     * @return {@String}
     */
    public fun getName(): String = name

    /**
     *
     * Get Tid
     *
     * @return {@URI}
     */
    public fun getTid(): URI = tid

    /**
     *
     * Get Ts
     *
     * @return {@ZonedDateTime}
     */
    public fun getTs(): ZonedDateTime = ts

    /**
     *
     * Returns a string representation of the object
     *
     * @return {@String}
     */
    public override fun toString(): String =
        MessageFormat.format("Token(nid={0}, name={1}, tid={2}, ts={3,time,long})", nid, name, tid, ts)

}
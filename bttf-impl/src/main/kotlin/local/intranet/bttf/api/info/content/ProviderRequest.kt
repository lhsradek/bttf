package local.intranet.bttf.api.info.content

import java.time.ZoneId
import java.time.ZonedDateTime
import java.net.URI
import java.text.MessageFormat

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public abstract class ProviderRequest : Request {

    private var value: Long = 0

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     */
    public constructor(nid: URI, name: String) : super(nid, name)

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     * @param tid  {@link URI}
     */
    public constructor(nid: URI, name: String, tid: URI) : super(nid, name, tid)

    /**
     *
     * @constructor with parameters
     *
     * @param nid  {@link URI}
     * @param name {@link String}
     * @param tid  {@link URI}
     * @param ts   {@link ZonedDateTime}
     */
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) : super(nid, name, tid, ts)

    /**
     *
     * Get Value
     * @return {@Long}
     */
    public fun getValue(): Long = value

    /**
     *
     * Set Value
     * @param value {@Long}
     */
    public fun setValue(value: Long) {
        this.value = value
    }

    /**
     *
     * Get Audit Event
     * @return {@String}
     */
    public fun auditEvent(): String = "applicant ${ProviderRequest::class.simpleName}"

    /**
     *
     * Returns a string representation of the object
     * @return {@String}
     */
    public override fun toString(): String = MessageFormat.format(
        "${ProviderRequest::class.simpleName}(value={0}, {1})", value, super.toString()
    )
}
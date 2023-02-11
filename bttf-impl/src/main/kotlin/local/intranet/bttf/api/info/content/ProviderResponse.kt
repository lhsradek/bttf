package local.intranet.bttf.api.info.content

import java.time.ZoneId
import java.time.ZonedDateTime
import java.net.URI
import java.text.MessageFormat

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public abstract class ProviderResponse : Response {

    private var result: Long = 0

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
     * Get Result
     *
     * @return {@Long}
     */
    public fun getResult(): Long = result

    /**
     *
     * Set Result
     *
     * @param result {@Long}
     */
    public fun setResult(result: Long) {
        this.result = result
    }

    /**
     *
     * Get Audit Event
     *
     * @return {@String}
     */
    public fun auditEvent(): String = "applicant.${ProviderResponse::class.simpleName}"

    /**
     *
     * Returns a string representation of the object
     *
     * @return {@String}
     */
    public override fun toString(): String = MessageFormat.format(
        "${ProviderResponse::class.simpleName}(result={0}, {1})", result, super.toString()
    )

}
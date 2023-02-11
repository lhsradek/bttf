package local.intranet.bttf.api.info.content

import java.time.ZonedDateTime
import java.text.MessageFormat
import java.net.URI
import local.intranet.bttf.api.domain.type.ResponseCodeType

public abstract class Response : Token {

    private var code: ResponseCodeType = ResponseCodeType.OK

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
     * Get Code
     *
     * @return {@ResponseCodeType}
     */
    public fun getCode(): ResponseCodeType = code

    /**
     *
     * Set Code
     *
     * @param code {@ResponseCodeType}
     */
    public fun setCode(code: ResponseCodeType) {
        this.code = code
    }

    /**
     *
     * Returns a string representation of the object
     *
     * @return {@String}
     */
    public override fun toString(): String = MessageFormat.format("Response(code={0}, {1})", code, super.toString())

}
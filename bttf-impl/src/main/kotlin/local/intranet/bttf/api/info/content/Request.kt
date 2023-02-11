package local.intranet.bttf.api.info.content

import java.time.ZonedDateTime
import java.text.MessageFormat
import java.net.URI

public abstract class Request : Token {

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
     * Returns a string representation of the object
     *
     * @return {@String}
     */
    public override fun toString(): String = MessageFormat.format("Request({0})", super.toString())

}
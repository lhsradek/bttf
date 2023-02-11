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
    
    private var value: Long = 0L

    public constructor(nid: URI, name: String): super(nid, name)
        
    public constructor(nid: URI, name: String, tid: URI) : super(nid, name, tid)
    
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) : super(nid, name, tid, ts)
    
    public fun getValue() : Long = value
    
    public fun setValue(value: Long) { this.value = value }
    
    public fun auditEvent():  String = "applicant ${ProviderRequest::class.simpleName}"

    public override fun toString(): String = MessageFormat.format(
        "${ProviderRequest::class.simpleName}(value={0}, {1})", value, super.toString())
}
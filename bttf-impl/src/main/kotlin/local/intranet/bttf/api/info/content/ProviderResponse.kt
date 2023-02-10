package local.intranet.bttf.api.info.content

import java.time.ZoneId
import java.time.ZonedDateTime
import java.net.URI
import java.text.MessageFormat

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public class ProviderResponse : Response {
    
    public var result: Long = 0
    
    public constructor(nid: URI, name: String): super(nid, name)
        
    public constructor(nid: URI, name: String, tid: URI) : super(nid, name, tid)
    
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) : super(nid, name, tid, ts)
    
    public fun auditEvent(): String = "applicant.${ProviderResponse::class.simpleName}"
    
    public override fun toString(): String = MessageFormat.format(
        "${ProviderResponse::class.simpleName}(result={0}, {1})", result, super.toString())
    
}
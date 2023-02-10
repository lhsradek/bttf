package local.intranet.bttf.api.info.content

import java.time.ZonedDateTime
import java.text.MessageFormat
import java.net.URI

public abstract class Request : Token {
    
    public constructor(nid: URI, name: String): super(nid, name)
        
    public constructor(nid: URI, name: String, tid: URI) : super(nid, name, tid)
    
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) : super(nid, name, tid, ts)
    
    public override fun toString(): String = MessageFormat.format("Request({0})", super.toString())
    
}
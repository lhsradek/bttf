package local.intranet.bttf.api.info.content

import java.time.ZonedDateTime
import java.text.MessageFormat
import java.net.URI
import local.intranet.bttf.api.domain.type.ResponseCodeType

public abstract class Response : Token {
    
    public var code: ResponseCodeType
    
    public constructor(nid: URI, name: String): super(nid, name) {
        this.code = ResponseCodeType.OK
    }
        
    public constructor(nid: URI, name: String, tid: URI) : super(nid, name, tid) {
        this.code = ResponseCodeType.OK
    }
    
    public constructor(nid: URI, name: String, tid: URI, ts: ZonedDateTime) : super(nid, name, tid, ts) {
        this.code = ResponseCodeType.OK
    }
    
    public override fun toString(): String = MessageFormat.format("Response(code={0}, {1})", code, super.toString())
    
}
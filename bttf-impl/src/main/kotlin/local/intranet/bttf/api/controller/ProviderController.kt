package local.intranet.bttf.api.controller

import java.util.Random
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.type.ResponseCodeType
import local.intranet.bttf.api.info.content.ProviderResponse
import local.intranet.bttf.api.info.content.ProviderRequest
import local.intranet.bttf.api.info.content.Request
import local.intranet.bttf.api.info.content.ServiceProvider
import local.intranet.bttf.api.info.content.Token
import local.intranet.bttf.api.info.content.TokenFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation
import org.springframework.web.bind.annotation.RestController

@RestController
@Endpoint(id = "application")
public class ProviderController: ServiceProvider<ProviderRequest, ProviderResponse> {

    private var margin: Long = 10L
    private var divider: Long = 2
    
    // @Autowired
    // private lateinit var loginAttemptService: LoginAttemptService
    
    @Autowired
    private lateinit var tokenFactory: TokenFactory

    @ReadOperation
    public fun getData(): Map<String, Long> = mutableMapOf("margin" to margin, "divider" to divider)

    @WriteOperation
    public fun postData(name: String, value: Long) {
        if (name.equals("margin")) {
            margin = value
        } else if (name.equals("divider")) {
            divider = value
        }
    }

    public override fun getInstanceName(): String = "Provider ${javaClass.simpleName}"

    public override fun perform(request: ProviderRequest): ProviderResponse {
        val response = tokenFactory
            .tokenInstance(
                request.getTid(),
                ProviderResponse::class.java) as ProviderResponse
        response.setCode(ResponseCodeType.OK)
        val value: Long = Random().nextLong(
            Math.max(Math.min(request.getValue() / divider, margin), 1)) 
        response.setResult(value)
        return response;
    }
}

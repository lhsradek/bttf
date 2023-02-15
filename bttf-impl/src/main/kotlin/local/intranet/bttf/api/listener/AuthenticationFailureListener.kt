package local.intranet.bttf.api.listener

import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.service.LoginAttemptService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.stereotype.Component

/**
 *
 * {@link AuthenticationFailureListener} for
 * {@link local.intranet.bttf.BttfApplication}
 * <p>
 * https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 *
 * @author Radek Kádner
 *
 */
@Component
public class AuthenticationFailureListener : ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    /**
     *
     * Application event listener
     */
    public override fun onApplicationEvent(e: AuthenticationFailureBadCredentialsEvent) {
        val ip = statusController.clientIP()
        loginAttemptService.loginFailed(ip)
        val arr = mutableListOf<String>()
        e.authentication?.let {
            e.authentication.authorities.forEach {
                arr.add(it.authority.replace(BttfConst.ROLE_PREFIX, ""))
            }
            arr.sort()
        }
        val id: Int? = loginAttemptService.findById(ip)
        id?.let {
            log.warn("Failed username:'{}' authorities:'{}' attempt:{}",
                e.authentication.name, arr, ip, id)
        } ?: log.warn("Failed username:'{}' authorities:'{}'",
            e.authentication.name, arr, ip)
    }
}
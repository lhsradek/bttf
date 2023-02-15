package local.intranet.bttf.api.listener


import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.service.LoginAttemptService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

/**
 *
 * {@link AuthenticationSuccessEventListener} for
 * {@link local.intranet.tombola.TombolaApplication}
 * <p>
 * https://www.baeldung.com/spring-security-block-brute-force-authentication-attempts
 *
 * @author Radek Kádner
 *
 */
@Component
public class AuthenticationSuccessEventListener : ApplicationListener<AuthenticationSuccessEvent> {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService

    /**
     *
     * Application event listener
     */
    public override fun onApplicationEvent(e: AuthenticationSuccessEvent) {
        val ip = statusController.clientIP()
        val arr = mutableListOf<String>()
        e.authentication?.let {
            e.authentication.authorities.forEach {
                arr.add(it.authority.replace(BttfConst.ROLE_PREFIX, ""))
            }
            arr.sort()
        }
        val id: Int? = loginAttemptService.findById(ip)
        loginAttemptService.loginSucceeded(ip)
        id?.let {
            log.info(
                "Login username:'{}' authorities:'{}' '{}' attempt:{}",
                e.getAuthentication().getName(), arr, ip, id)
        } ?: log.info(
            "Login username:'{}' authorities:'{}' '{}'",
            e.getAuthentication().getName(), arr, ip
        )
    }
}
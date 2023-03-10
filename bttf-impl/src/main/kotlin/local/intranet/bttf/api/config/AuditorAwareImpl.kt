package local.intranet.bttf.api.config

import java.util.Optional

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder

/**
 *
 * {@link AuditorAwareImpl} for {@link local.intranet.bttf.BttfApplication}
 *
 */
@ConditionalOnExpression("\${bttf.envers.enabled}")
public class AuditorAwareImpl : AuditorAware<String> {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    /**
     *
     * Get current auditor
     * @return {@link Optional}&lt;{@link String}&gt;
     */
    public override fun getCurrentAuditor(): Optional<String> {
        val ret: Optional<String>
        val authentication = SecurityContextHolder.getContext().authentication
        if (SecurityContextHolder.getContext().authentication != null && authentication.isAuthenticated()) {
            ret = Optional.ofNullable(SecurityContextHolder.getContext().authentication.name)
        } else {
            ret = Optional.empty()
        }
        // if (dbg.toBoolean()) log.debug("{}", ret.get())
        return ret
    }
}
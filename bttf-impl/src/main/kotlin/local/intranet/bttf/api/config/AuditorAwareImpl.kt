package local.intranet.bttf.api.config

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Optional

/**
 *
 * {@link AuditorAwareImpl} for {@link local.intranet.bttf.BttfApplication}
 *
 */
@ConditionalOnExpression("\${bttf.envers.enabled}")
class AuditorAwareImpl : AuditorAware<String> {

    private val logger = LoggerFactory.getLogger(AuditorAwareImpl::class.java)

    /**
     *
     * Get current auditor
     * @return {@link Optional}&lt;{@link String}&gt;
     */
    override fun getCurrentAuditor(): Optional<String> {
        val ret: Optional<String>
        val authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication.isAuthenticated()) {
            ret = Optional.ofNullable(authentication.principal as String)
        } else {
            ret = Optional.empty()
        }
        logger.debug("{}", ret)
        return ret
    }
}
package local.intranet.bttf.api.config

import java.util.Optional

import org.springframework.data.domain.AuditorAware
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

/**
 * 
 * {@link AuditorAwareImpl} for {@link local.intranet.bttf.BttfApplication}
 *
 */
class AuditorAwareImpl: AuditorAware<String> {

	/**
	 * 
	 * Get current auditor
	 * @return {@link Optional}&lt;{@link String}&gt;
	 */
    override fun getCurrentAuditor(): Optional<String>  {
        val authentication = SecurityContextHolder.getContext().getAuthentication()
        if (authentication.isAuthenticated()) {
            return Optional.ofNullable(authentication.principal as String)
        } else {
            return Optional.empty()
        }
    }
}
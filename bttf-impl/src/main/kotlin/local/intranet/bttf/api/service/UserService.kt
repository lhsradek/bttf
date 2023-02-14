package local.intranet.bttf.api.service

import javax.servlet.http.HttpSession
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.type.RoleType
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.model.entity.User
import local.intranet.bttf.api.model.repository.UserRepository
import local.intranet.bttf.api.controller.StatusController
import local.intranet.bttf.api.listener.LogoutSuccess
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * {@link UserService} for
 * {@link local.intranet.bttf.api.controller.InfoController#userInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
public class UserService : UserDetailsService, Statusable {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var httpSession: HttpSession

    @Autowired
    private lateinit var statusController: StatusController

    @Autowired
    private lateinit var loginAttemptService: LoginAttemptService


   /**
     *
     * Get status
     *
     * @return {@link StatusType}
     */
    public override fun getStatus(): StatusType = StatusType.UP

    /**
     *
     * Bean for logout
     * {@link local.intranet.bttf.api.listener.LogoutSuccess#onLogoutSuccess}.
     * <p>
     * Login is in
     * {@link local.intranet.bttf.api.controller.IndexController#signin}
     *
     * @return {@link LogoutSuccess}
     */
    @Bean
    public fun logoutSuccess(): LogoutSuccessHandler = LogoutSuccess()

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#userInfo}
     *
     * @return {@link UserInfo}
     * @throws LockedException           if the user is locked.
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     * @throws BadCredentialsException   if the credentials are invalid
     * @throws AccountExpiredException   if an authentication request is rejected because the account has expired.
     *                                   Makes no assertion as to whether or not the credentials were valid.
     */
    @Transactional(readOnly = true)
    @Throws(
        UsernameNotFoundException::class, LockedException::class, BadCredentialsException::class,
        AccountExpiredException::class
    )
    public fun userInfo(): UserDetails = loadUserByUsername(username())

    /**
     *
     * Locates the user based on the username.
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws LockedException           if the user is locked.
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     * @throws BadCredentialsException   if the credentials are invalid
     * @throws AccountExpiredException   if an authentication request is rejected because the account has expired.
     *                                   Makes no assertion as to whether or not the credentials were valid.
     */
    @Transactional(readOnly = true)
    @Throws(
        UsernameNotFoundException::class, LockedException::class, BadCredentialsException::class,
        AccountExpiredException::class
    )
    public override fun loadUserByUsername(username: String): UserDetails {
        var ip: String
        try {
            ip = statusController.clientIP()
        } catch (e: Exception) {
            ip = ""
        }
        if (ip.length > 0 && loginAttemptService.isBlocked(ip)) {
            throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
        }
        val user: User? = userRepository.findByName(username)

        user?.let {
            with(user) {
                if (accountNonExpired && accountNonLocked && credentialsNonExpired && enabled) {
                    val authorities = mutableListOf<GrantedAuthority>()
                    role.forEach {
                        authorities.add(SimpleGrantedAuthority(BttfConst.ROLE_PREFIX + it.roleName))
                    }
                    return UserInfo.build(user, authorities)

                } else {
                    if (!credentialsNonExpired) {
                        throw BadCredentialsException(BttfConst.ERROR_BAD_CREDENTIALS)
                    } else if (!accountNonExpired) {
                        throw AccountExpiredException(BttfConst.ERROR_ACCOUNT_EXPIRED)
                    }
                    throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
                }
            }
        } ?: with(loginAttemptService) {
            if (ip.length > 0) {
            	loginFailed(ip)
            }
            throw UsernameNotFoundException(BttfConst.ERROR_USERNAME_NOT_FOUND)
        }
    }

    /**
     *
     * Get Username
     *
     * @return {@link String}
     */
    public fun username(): String {
        var ret = ""
        SecurityContextHolder.getContext().authentication?.let {
            httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)?.let {
                if (httpSession.maxInactiveInterval < 3600) {
                    httpSession.setMaxInactiveInterval(3600)
                }
                ret = SecurityContextHolder.getContext().authentication.name
            }
        }
        // if (dbg.toBoolean()) log.debug("'{}'", ret)
        return ret
    }

    /**
     *
     * Is authenticated
     *
     * @return {@link Boolean}
     */
    public fun isAuthenticated(): Boolean {
        val list = authoritiesRoles()
        val ret = if (list.size > 0 && !list.first().equals(RoleType.ANONYMOUS_ROLE.role)) true else false
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get authorities roles
     *
     * @return {@link List}&lt;{@link String}&gt;
     */
    public fun authoritiesRoles(): List<String> {
        val ret = mutableListOf<String>()
        httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)?.let {
            SecurityContextHolder.getContext().authentication?.let {
                for (g: GrantedAuthority in SecurityContextHolder.getContext().authentication.authorities) {
                    ret.add(g.authority.replace(BttfConst.ROLE_PREFIX, ""))
                }
            }
            ret.sort()
        }
        if (ret.size == 0) {
            ret.add(RoleType.ANONYMOUS_ROLE.role)
        }
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * All user's roles
     * <p>
     *
     * &#64;JsonIgnore
     *
     * @return get all roles with boolean for Heavy Check &#x2714; or Heavy Ballot
     *         &#x2718; if user haves role in
     *         {@link Map}&lt;{@link String},{@link Boolean}&gt; It's displayed in
     *         {@link local.intranet.bttf.api.controller.IndexController#getLogin}
     *         if user is logged.
     */
    public fun userRoles(): Map<String, Boolean> {
        val ret = mutableMapOf<String, Boolean>()
        val list = authoritiesRoles()
        for (r in RoleType.values()) {
            if (!r.equals(RoleType.ANONYMOUS_ROLE)) {
                ret.put(r.role.replace(BttfConst.ROLE_PREFIX, ""), list.contains(r.role))
            }
        }
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

}

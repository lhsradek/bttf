package local.intranet.bttf.api.service

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.type.RoleType
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.model.entity.User
import local.intranet.bttf.api.model.repository.UserRepository
import local.intranet.bttf.api.security.LogoutSuccess
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpSession

/**
 *
 * {@link UserService} for
 * {@link local.intranet.bttf.api.controller.InfoController#getUserInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
class UserService : UserDetailsService {

    private val log = LoggerFactory.getLogger(UserService::class.java)

    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

    val USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL: Int = 3600

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var httpSession: HttpSession

    /**
     *
     * Bean for logout
     * {@link local.intranet.bttf.api.security.LogoutSuccess#onLogoutSuccess}.
     * <p>
     * Login is in
     * {@link local.intranet.bttf.api.controller.IndexController#signin}
     *
     * @return {@link LogoutSuccess}
     */
    @Bean
    fun logoutSuccess(): LogoutSuccessHandler {
        val ret: LogoutSuccessHandler = LogoutSuccess()
        return ret
    }

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#getUserInfo}
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
    fun getUserInfo(): UserInfo {
        val ret = loadUserByUsername(getUsername())
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

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
    override fun loadUserByUsername(username: String): UserInfo {
        val ret: UserInfo
        // val ip: String = statusController.getClientIP()
        // if (loginAttemptService.isBlocked(ip)) {
        //     throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
        // }
        val user: User? = userRepository.findByName(username)

        // if (dbg.toBoolean()) logger.debug("{}", user)

        user?.let {
            if (user.accountNonExpired && user.accountNonLocked && user.credentialsNonExpired && user.enabled) {
                val authorities = mutableListOf<GrantedAuthority>()
                user.role.forEach {
                    authorities.add(SimpleGrantedAuthority(BttfConst.ROLE_PREFIX + it.roleName))
                }
                ret = UserInfo(user.userName, user.password, true, true, true, true, authorities)

                if (dbg.toBoolean()) log.debug("{}", ret)
                return ret

            } else {
                if (!user.credentialsNonExpired) {
                    throw BadCredentialsException(BttfConst.ERROR_BAD_CREDENTIALS)
                } else if (!user.accountNonExpired) {
                    throw AccountExpiredException(BttfConst.ERROR_ACCOUNT_EXPIRED)
                }
                throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
            }
        } ?: run {
        	if (dbg.toBoolean()) log.debug("username:'{}' {}", username, BttfConst.ERROR_USERNAME_NOT_FOUND)
            throw UsernameNotFoundException(BttfConst.ERROR_USERNAME_NOT_FOUND)
        }
    }

    /**
     *
     * Get Username
     *
     * @return {@link String}
     */
    fun getUsername(): String {
        var ret = ""
        val obj = httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
        obj?.let {
            val auth: Authentication = SecurityContextHolder.getContext().authentication
            if (httpSession.getMaxInactiveInterval() < USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL) {
                httpSession.setMaxInactiveInterval(USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL)
            }
            ret = auth.getName()
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
    fun isAuthenticated(): Boolean {
        val list = getAuthoritiesRoles()
        val ret: Boolean = if (list.size > 0 && !list.first().equals(BttfConst.USER_ANONYMOUS)) true else false
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * Get authorities roles
     *
     * @return {@link List}&lt;{@link String}&gt;
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    fun getAuthoritiesRoles(): List<String> {
        val ret = mutableListOf<String>()
        val obj = httpSession.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY)
        obj?.let {
            val auth: Authentication = SecurityContextHolder.getContext().authentication
            for (g: GrantedAuthority in auth.authorities) {
                ret.add(g.authority.replace(BttfConst.ROLE_PREFIX, ""))
            }
            ret.sort()
        }
        if (ret.size == 0) {
            ret.add(RoleType.ANONYMOUS_ROLE.toString())
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
    @JsonIgnore
    fun getUserRoles(): Map<String, Boolean> {
        val ret = mutableMapOf<String, Boolean>()
        val list = getAuthoritiesRoles()
        for (r: RoleType in RoleType.values()) {
            if (!r.equals(RoleType.ANONYMOUS_ROLE)) {
                ret.put(r.role.replace(BttfConst.ROLE_PREFIX, ""), list.contains(r.role))
            }
        }
        // if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

}

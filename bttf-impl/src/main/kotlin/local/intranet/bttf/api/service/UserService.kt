package local.intranet.bttf.api.service

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.type.RoleType
import local.intranet.bttf.api.info.UserInfo
import local.intranet.bttf.api.model.entity.User
import local.intranet.bttf.api.model.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
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

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    val USER_LOGIN_SESSION_MAX_INACTIVE_INTERVAL: Int = 3600

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var httpSession: HttpSession

    @Transactional(readOnly = true)
    fun getUserInfo(): UserInfo {
        val ret = loadUserByUsername(getUsername())
        logger.debug("{}", ret)
        return ret
    }

    @Transactional(readOnly = true)
    @Throws(UsernameNotFoundException::class, LockedException::class)
    override fun loadUserByUsername(username: String): UserInfo {
        val ret: UserInfo
        // val ip: String = statusController.getClientIP()
        // if (loginAttemptService.isBlocked(ip)) {
        //     throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
        // }
        val user: User? = userRepository.findByName(username)
        
        logger.debug("{}", user)

        user?.let {
            if (user.accountNonExpired && user.accountNonLocked && user.credentialsNonExpired
                && user.enabled
            ) {
                val authorities = mutableListOf<GrantedAuthority>()
                user.role.forEach {
                    authorities.add(SimpleGrantedAuthority(BttfConst.ROLE_PREFIX + it.roleName))
                }
                ret = UserInfo(user.userName, user.password, true, true, true, true, authorities)
                
                logger.debug("{}", ret)
                return ret

            } else {
                throw LockedException(BttfConst.ERROR_USERNAME_IS_LOCKED)
            }
        } ?: throw UsernameNotFoundException(BttfConst.ERROR_USERNAME_NOT_FOUND)
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
        logger.debug("{}", ret)
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
        logger.debug("{}", ret)
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
        logger.debug("{}", ret)
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
     *         {@link local.intranet.tombola.api.controller.IndexController#getLogin}
     *         if user is logged.
     */
    @JsonIgnore
    fun getUserRoles(): Map<String, Boolean> {
        val ret = mutableMapOf<String, Boolean>()
        val list = getAuthoritiesRoles()
        for (r: RoleType in RoleType.values()) {
            if (!r.equals(RoleType.ANONYMOUS_ROLE))
                ret.put(r.role.replace(BttfConst.ROLE_PREFIX, ""), list.contains(r.role))
        }
        logger.debug("{}", ret)
        return ret
    }

}

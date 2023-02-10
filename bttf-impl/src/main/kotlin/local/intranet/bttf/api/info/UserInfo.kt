package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.model.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.validation.constraints.Size


/**
 *
 * {@link UserInfo} for {@link local.intranet.bttf.api.service.UserService}
 * and {@link local.intranet.bttf.api.controller.IndexController#signin}
 *
 * @author Radek Kádner
 *
 * @constructor with parameters
 *
 * @param username                {@link String}
 * @param password                {@link String}
 * @param isEnabled               {@link Boolean}
 * @param isCredentialsNonExpired {@link Boolean}
 * @param isAccountNonExpired     {@link Boolean}
 * @param isAccountNonLocked      {@link Boolean}
 * @param authorities             {@link List&lt;{@link GrantedAuthority}&gt;}
 */
@JsonPropertyOrder(
    "username",
    "isEnabled",
    "isCredentialsNonExpired",
    "isAccountNonExpired",
    "isAccountNonLocked"
)
public data class UserInfo (
    private val username: String,
    private val password: String,
    private val isEnabled: Boolean,
    private val isCredentialsNonExpired: Boolean,
    private val isAccountNonExpired: Boolean,
    private val isAccountNonLocked: Boolean,
    private val authorities: List<GrantedAuthority>) : UserDetails {

    public companion object {

        /**
         *
         * Build for {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
         *
         * @param user        {@link User}
         * @param authorities {@link List}&lt;{@link GrantedAuthority}&gt;
         * @return {@link UserInfo}
         */
        @JvmStatic
        public fun build(user: User, authorities: List<GrantedAuthority>): UserInfo = UserInfo(
            user.userName, user.password, true, true, true, true, authorities)
    }

    /**
     *
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public override fun getUsername(): String = username

    /**
     *
     * Get password
     *
     * @return {@link String}
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonIgnore
    public override fun getPassword(): String = password

    /**
     *
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code>
     *         otherwise
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun isEnabled(): Boolean = isEnabled

    /**
     *
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired

    /**
     *
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun isAccountNonExpired(): Boolean = isAccountNonExpired

    /**
     *
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code>
     *         otherwise
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public override fun isAccountNonLocked(): Boolean = isAccountNonLocked

    /**
     *
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_ENUMERATION)
    @JsonIgnore
    public override fun getAuthorities(): List<GrantedAuthority> = authorities

    /**
     *
     * Returns a string representation of the object with protected password
     */
    public override fun toString(): String = "UserInfo [username=" + username + ", password=" +
    		BttfConst.STATUS_PROTECTED + ", enabled=" + isEnabled + ", authorities=" + authorities + "]"
    
}

package local.intranet.bttf.api.info

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
 * Constructor with parameters
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
    "password",
    "isEnabled",
    "isCredentialsNonExpired",
    "isAccountNonExpired",
    "isAccountNonLocked"
)
data class UserInfo(
    private val username: String,
    private val password: String,
    private val isEnabled: Boolean,
    private val isCredentialsNonExpired: Boolean,
    private val isAccountNonExpired: Boolean,
    private val isAccountNonLocked: Boolean,
    private val authorities: List<GrantedAuthority>) : UserDetails {

    /**
     *
     * Returns the username used to authenticate the user. Cannot return
     * <code>null</code>.
     *
     * @return the username (never <code>null</code>)
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    override fun getUsername(): String = username

    /**
     *
     * Get password
     *
     * @return {@link String}
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonIgnore
    override fun getPassword(): String = password

    /**
     *
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is enabled, <code>false</code>
     *         otherwise
     */
    override fun isEnabled(): Boolean = isEnabled

    /**
     *
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    override fun isCredentialsNonExpired(): Boolean = isCredentialsNonExpired

    /**
     *
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     *
     * @return <code>true</code> if the user's credentials are valid (ie
     *         non-expired), <code>false</code> if no longer valid (ie expired)
     */
    override fun isAccountNonExpired(): Boolean = isAccountNonExpired

    /**
     *
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     *
     * @return <code>true</code> if the user is not locked, <code>false</code>
     *         otherwise
     */
    override fun isAccountNonLocked(): Boolean = isAccountNonLocked

    /**
     *
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     *
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
    @JsonIgnore
    @Size(min = 0)
    override fun getAuthorities(): List<GrantedAuthority> = authorities

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "UserInfo [username=" + username + ", password=" + BttfConst.STATUS_PROTECTED +
                ", enabled=" + isEnabled + ", authorities=" + authorities + "]"
    }

}

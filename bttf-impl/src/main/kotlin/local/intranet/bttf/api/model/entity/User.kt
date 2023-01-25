package local.intranet.bttf.api.model.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.domain.DefaultFieldLengths

/**
 *
 * {@link User} is entity for CRUD with
 * {@link local.intranet.bttf.api.model.repository.UserRepository}
 *
 * @author Radek Kádner
 *
 */
@Entity
@Table(name = "bttf_user")
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @NotNull
    @Column(name = "user_name", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    val userName: String = ""

    @NotNull
    @Column(name = "password", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    val password: String = ""

    /**
     *
     * accountNonExpired
     *
     * If expired in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> AccountExpiredException
     *
     */
    @Column(name = "account_non_expired")
    val accountNonExpired: Boolean = true

    /**
     *
     * accountNonLocked
     *
     * If locked in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> LockedException
     *
     */
    @Column(name = "account_non_locked")
    val accountNonLocked: Boolean = true

    /**
     *
     * credentialsNonExpired
     *
     * iIf expired in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> BadCredentialsException
     *
     */
    @Column(name = "credentials_non_expired")
    val credentialsNonExpired: Boolean = true

    val enabled: Boolean = true

    @Column(nullable = true)
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(name = "bttf_user_role")
    var role = mutableListOf<Role>()

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "User [id=" + id + ", userName=" + userName + ", password=" + BttfConst.STATUS_PROTECTED +
                ", credentialsNonExpired=" + credentialsNonExpired + ", enabled=" + enabled + ", role=" + role + "]"
    }

}

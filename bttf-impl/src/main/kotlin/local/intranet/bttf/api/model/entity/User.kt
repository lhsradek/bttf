package local.intranet.bttf.api.model.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
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
public data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public val id: Long? = null,

    @NotNull
    @Column(name = "user_name", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    public val userName: String,

    @NotNull
    @Column(name = "password", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    public val password: String,

    /**
     *
     * accountNonExpired
     *
     * If expired in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> AccountExpiredException
     *
     */
    @NotNull
    @Column(name = "account_non_expired", nullable = false)
    public val accountNonExpired: Boolean = true,

    /**
     *
     * accountNonLocked
     *
     * If locked in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> LockedException
     *
     */
    @NotNull
    @Column(name = "account_non_locked", nullable = false)
    public val accountNonLocked: Boolean = true,

    /**
     *
     * credentialsNonExpired
     *
     * iIf expired in {@link local.intranet.bttf.api.service.UserService#loadUserByUsername}
     *  -> BadCredentialsException
     *
     */
    @Column(name = "credentials_non_expired")
    public val credentialsNonExpired: Boolean = true,

    @Column(nullable = false)
    public val enabled: Boolean = true
)
// constructor's end
{
// method    

    @Column(nullable = true)
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(name = "bttf_user_role")
    public val role = mutableListOf<Role>()

}
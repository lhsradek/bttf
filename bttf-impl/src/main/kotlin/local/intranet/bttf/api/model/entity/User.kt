package local.intranet.bttf.api.model.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinTable
import javax.persistence.OneToMany
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import local.intranet.bttf.api.domain.BttfController
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
    val id: Int? = null

    @NotNull
    @Column(name = "user_name", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    val userName: String = ""

    @NotNull
    @Column(name = "password", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    val password: String = ""

    @Column(name = "account_non_expired")
    val accountNonExpired: Boolean = true

    @Column(name = "account_non_locked")
    val accountNonLocked: Boolean = true

    @Column(name = "credentials_non_expired")
    val credentialsNonExpired: Boolean = true

    val enabled: Boolean = true

    @Column(nullable = true)
    @OneToMany(cascade = [CascadeType.ALL])
    @JoinTable(name = "bttf_user_role")
    val role: Set<Role>? = null

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "User [id=" + id + ", userName=" + userName + ", password=" + BttfController.STATUS_PROTECTED +
                ", credentialsNonExpired=" + credentialsNonExpired + ", enabled=" + enabled + ", role=" + role +"]"
    }

}

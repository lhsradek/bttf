package local.intranet.bttf.api.model.entity

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

import local.intranet.bttf.api.domain.DefaultFieldLengths

/**
 *
 * {@link Role} is entity for CRUD with
 * {@link local.intranet.bttf.api.model.repository.RoleRepository}
 *
 * @author Radek Kádner
 *
 */
@Entity
@Table(name = "bttf_role")
class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @NotNull
    @Column(name = "role_name", nullable = false)
    @Size(max = DefaultFieldLengths.DEFAULT_NAME)
    val roleName: String = ""

    val enabled: Boolean = true

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
     	return "Role [id=" + id + ", roleName=" + roleName + ", enabled=" + enabled + "]"
    }

}

package local.intranet.bttf.api.info

import javax.validation.constraints.Size

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import local.intranet.bttf.api.domain.DefaultFieldLengths
import local.intranet.bttf.api.service.RoleService

/**
 *
 * {@link RoleInfo}
 *
 * @author Radek Kádner
 *
 * Constructor with parameter
 *
 * @param role {@link List}&lt;{@link RolePlain}&gt;
*/
@JsonPropertyOrder("name", "roles")
data class RoleInfo constructor(

    @Size(min = 0)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val role: List<RolePlain>) {

    /**
     *
     * Returns the rolename
     *
     * @return the rolename
     */
    @Size(min = 1, max = DefaultFieldLengths.DEFAULT_NAME)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    fun name(): String {
        return RoleService::class.java.getSimpleName()
    }

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "RoleInfo [name=" + name() + ", role=" + role + "]"
    }

}

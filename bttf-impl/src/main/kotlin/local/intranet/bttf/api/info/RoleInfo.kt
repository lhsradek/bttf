package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 *
 * {@link RoleInfo} for
 * {@link local.intranet.bttf.api.service.RoleService#getRoleInfo} and
 * {@link local.intranet.bttf.api.controller.InfoController#getRoleInfo}
 *
 * @author Radek Kádner
 *
 * @param name             {@link String}
 * @param roleInfo         {@link RoleInfo}
 * @param isEnabled        {@link Boolean}
 */
@JsonPropertyOrder("name", "roles", "enabled")
data class RoleInfo(val name: String, val roleInfo: RoleInfo, val isEnabled: Boolean) {

    /**
     *
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        return "RoleInfo [name=" + name + ", roleInfo=" + roleInfo + ", enabled=" + isEnabled + "]"
    }

}

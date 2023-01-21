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
 */
@JsonPropertyOrder("name", "roles", "enabled")
class RoleInfo {

}

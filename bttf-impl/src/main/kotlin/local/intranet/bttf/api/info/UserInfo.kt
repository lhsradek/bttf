package local.intranet.bttf.api.info

import com.fasterxml.jackson.annotation.JsonPropertyOrder

/**
 * 
 * {@link UserInfo} for {@link local.intranet.bttf.api.service.UserService}
 * and {@link local.intranet.bttf.api.controller.IndexController#signin}
 * 
 * @author Radek Kádner
 *
 */
@JsonPropertyOrder("username", "password", "enabled")
class UserInfo {

    private var username = ""

    private var password = ""

    private var enabled = true

}

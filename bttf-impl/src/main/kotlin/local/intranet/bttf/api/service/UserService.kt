package local.intranet.bttf.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import local.intranet.bttf.api.info.UserInfo

/**
 *
 * {@link UserService} for
 * {@link local.intranet.bttf.api.controller.InfoController#getUserInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
class UserService {

    @Transactional(readOnly = true)
    fun getUserInfo(): UserInfo {
        val userInfo = UserInfo()
        return userInfo
    }

}

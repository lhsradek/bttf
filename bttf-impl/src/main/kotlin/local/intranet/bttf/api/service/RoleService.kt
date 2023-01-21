package local.intranet.bttf.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import local.intranet.bttf.api.info.RoleInfo

/**
 * 
 * {@link RoleService} for
 * {@link local.intranet.bttf.api.controller.InfoController#getRoleInfo}
 * 
 * @author Radek Kádner
 *
 */
@Service
class RoleService {
    
    @Transactional(readOnly = true)
    fun getRoleInfo(): RoleInfo {
        val roleInfo = RoleInfo()
        return roleInfo
    }

}

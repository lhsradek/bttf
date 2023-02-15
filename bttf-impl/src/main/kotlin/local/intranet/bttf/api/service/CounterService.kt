package local.intranet.bttf.api.service

import local.intranet.bttf.api.domain.type.RoleType
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.info.RolePlain
import local.intranet.bttf.api.model.repository.CounterRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 *
 * {@link CounterService} for
 * {@link local.intranet.bttf.api.controller.InfoController#counterInfo}
 *
 * @author Radek Kádner
 *
 */
@Service
public class CounterService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository

    /**
     *
     * Get userRole for {@link local.intranet.bttf.api.controller.InfoController#countTotalCounterName}
     *
     * @return {@link List}&lt;{@link ServiceCount}&gt;
     */
    @Synchronized
    @Transactional(readOnly = true)
    public fun countTotalCounterName(): List<ServiceCount> = counterRepository.countTotalCounterName()

}

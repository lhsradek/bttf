package local.intranet.bttf.api.service

import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.model.repository.MessageEventRepository
import local.intranet.bttf.api.info.content.BttfCounter
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.info.ServiceCount
import org.hibernate.envers.RevisionType
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
/**
 *
 * {@link JobService} for {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@Service
@ConditionalOnExpression("\${scheduler.enabled}")
public class JobService : Countable, Invocationable, Statusable, BttfCounter() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository

    @Autowired
    private lateinit var messageEventRepository: MessageEventRepository
    
    /*
    public override fun countValue(): Long = super.countValue()
    public override fun incrementCounter(): Long = super.incrementCounter()
    public override fun lastInvocation(): ZonedDateTime = super.lastInvocation()
    public override fun getStatus(): StatusType = super.getStatus()
 	*/
       
    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#jobInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun jobInfo(): CounterInfo {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            with(counter) {
                val (revisonNum, revisionType) = counterAudit(id!!)
                CounterInfo(
                    cnt,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    counterName, StatusType.valueOf(status), revisonNum, revisionType
                )
            }
        } ?: CounterInfo(
            0L,
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault()),
            javaClass.simpleName, StatusType.NONE, 0, RevisionType.DEL // If it's DEL, it wasn't in getCounterAudit 
        )
        return ret
    }

    
    @Transactional(readOnly = true)
    public fun countTotalMessageEvents(): List<ServiceCount> = messageEventRepository.countTotalMessageEvents()
 
    
}

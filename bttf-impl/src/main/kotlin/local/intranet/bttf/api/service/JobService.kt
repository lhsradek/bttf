package local.intranet.bttf.api.service

import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.repository.CounterRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
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
public class JobService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository


    /**
     *
     * Increment counter
     *
     * @param external boolean
     * @return {@link Counter}
     */
    @Transactional
    fun incrementCounter(): Counter {
        val counter = counterRepository.findByName(javaClass.simpleName)
        counter?.let {
            counter.cnt++
            counter.timestmp = System.currentTimeMillis()
            counterRepository.save(counter)
            return counter
        } ?: run {
            val ret = Counter(null, javaClass.simpleName, 1L, System.currentTimeMillis(), StatusType.UP.status)
            counterRepository.save(ret)
            return ret
        }
    }

    @Transactional(readOnly = true)
    fun countValue(): Long {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt
        } ?: 0L
        return ret
    }

    @Transactional(readOnly = true)
    fun lastInvocation(): ZonedDateTime {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(counter.timestmp), ZoneId.systemDefault())
        } ?: ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
        return ret
    }

    @Transactional(readOnly = true)
    fun getStatus(): StatusType {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            StatusType.valueOf(counter.status)
        } ?: StatusType.UP
        return ret
    }

}

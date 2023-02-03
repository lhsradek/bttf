package local.intranet.bttf.api.service

import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.info.CounterInfo
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
class JobService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository


    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#getCounterInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    fun getJobInfo(): CounterInfo {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            CounterInfo(
                counter.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(counter.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(counter.status),
                counter.counterName
            )
        } ?: CounterInfo(
            0L,
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault()),
            StatusType.NONE,
            javaClass.simpleName
        )
        return ret
    }

    /**
     *
     * Increment counter
     *
     * @param external boolean
     * @return {@link Counter}
     */
    @Transactional
    fun incrementCounter(): CounterInfo {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt++
            counter.timestmp = System.currentTimeMillis()
            val newCnt = counterRepository.save(counter)
            CounterInfo(
                newCnt.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(newCnt.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(newCnt.status),
                newCnt.counterName
            )

        } ?: run {
            val cnt = Counter(null, javaClass.simpleName, 1L, System.currentTimeMillis(), StatusType.UP.status)
            val newCnt = counterRepository.save(cnt)
            CounterInfo(
                newCnt.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(newCnt.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(newCnt.status),
                newCnt.counterName
            )
        }
        return ret
    }

    /**
     *
     * Number of invocations
     * <p>
     * &#64;JsonProperty("count")
     *
     * @return number of invocations from count
     */
    @Transactional(readOnly = true)
    fun countValue(): Long {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt
        } ?: 0L
        return ret
    }

    /**
     *
     * Time of last invocation
     * <p>
     * &#64;JsonInclude(JsonInclude.Include.NON_NULL)
     * <p>
     *
     * @return lastInvocation
     */
    @Transactional(readOnly = true)
    fun lastInvocation(): ZonedDateTime {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(counter.timestmp), ZoneId.systemDefault())
        } ?: ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
        return ret
    }

    /**
     *
     * Get status
     *
     * @return {@link StatusType}
     */
    @Transactional(readOnly = true)
    fun getStatus(): StatusType {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            StatusType.valueOf(counter.status)
        } ?: StatusType.NONE
        return ret
    }

}

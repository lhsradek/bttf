package local.intranet.bttf.api.service

import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.entity.MessageEvent
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.model.repository.MessageEventRepository
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.info.content.Provider
import org.jetbrains.annotations.NotNull
import org.hibernate.envers.AuditReader
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
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
public class JobService : Countable, Invocationable, Statusable {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository

    @Autowired
    private lateinit var messageEventRepository: MessageEventRepository
    
    @Autowired
    private lateinit var provider: Provider

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
            val (revisonNum, revisionType) = counterAudit(counter.id)
            CounterInfo(
                counter.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(counter.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(counter.status), counter.counterName, revisonNum, revisionType
            )
        } ?: CounterInfo(
            0L,
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault()),
            StatusType.NONE, javaClass.simpleName, 0, RevisionType.DEL // If it's DEL, it wasn't in getCounterAudit 
        )
        return ret
    }

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#messageInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun messageInfo(page: Int, size: Int): Page<MessageEvent> {
    	val ret = messageEventRepository.findByName(PageRequest.of(page, size), javaClass.simpleName)
		return ret
    }
    
    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#messageInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun messageByUuid(uuid: String): MessageEvent {
    	val ret = messageEventRepository.findByUuid(uuid)
		return ret
    }
    
    /**
     *
     * Send Message
     *
     * @return {@link StatusType}
     */
    @Transactional // write new message
    public fun sendMessage(message: String): MessageEvent {
        return messageEventRepository.save(MessageEvent(
            null, UUID.randomUUID().toString(), javaClass.simpleName, 0, System.currentTimeMillis(), message
        ))
    }

    /**
     *
     * Increment counter
     *
     * @param external boolean
     * @return {@link Counter}
     */
    @Transactional  // write
    public fun incrementCounter(): CounterInfo {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt++
            counter.timestmp = System.currentTimeMillis()
            val newCnt = counterRepository.save(counter)
            val (revisonNum, revisionType) = counterAudit(newCnt.id)
            CounterInfo(
                newCnt.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(newCnt.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(newCnt.status), newCnt.counterName, revisonNum, revisionType
            )
        } ?: run {
            val cnt = Counter(null, javaClass.simpleName, 1L, System.currentTimeMillis(), StatusType.UP.status)
            val newCnt = counterRepository.save(cnt)
            CounterInfo(
                newCnt.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(newCnt.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(newCnt.status), newCnt.counterName, 0, RevisionType.DEL // If it's DEL, it wasn't in getCounterAudit
            )
        }
        return ret
    }

    /**
     *
     * Number of invocations
     *
     * @return number of invocations from count
     */
    @Transactional(readOnly = true)
    public override fun countValue(): Long {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt
        } ?: 0L
        return ret
    }

    /**
     *
     * Time of last invocation
     *
     * @return lastInvocation
     */
    @Transactional(readOnly = true)
    public override fun lastInvocation(): ZonedDateTime {
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
    public override fun getStatus(): StatusType {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            StatusType.valueOf(counter.status)
        } ?: StatusType.NONE
        return ret
    }

    /**
     *
     * Counter audit
     *
     * @param counterId {@link Long}
     * @return {@link Pair}&lt;{@link Int}, {@link RevisionType}&gt;
     */
    @Transactional(readOnly = true)
    protected fun counterAudit(@NotNull counterId: Long?): Pair<Int, RevisionType> {
        var ret = Pair(0, RevisionType.DEL)
        val reader: AuditReader = provider.auditReader()
        val query: AuditQuery = reader.createQuery()
            .forRevisionsOfEntity(Counter::class.java, true, true)
            .addProjection(AuditEntity.revisionNumber())
            .addProjection(AuditEntity.selectEntity(false))
            .add(AuditEntity.id().eq(counterId))
            .addOrder(AuditEntity.revisionNumber().desc())
            .addOrder(AuditEntity.id().desc())
        for (row in query.getResultList()) {
            // row is Object[] in Java
            val arr = row as Array<*>
            val revisionNumber = arr[0] as Int
            val entity = arr[1] as Map<*, *>
            ret = entity["REVTYPE"]?.let {
                Pair(revisionNumber, entity["REVTYPE"] as RevisionType) // It should be a MOD
            } ?: Pair(revisionNumber, RevisionType.ADD) // I know it went this way
            break // the first is enough for openAPI info
        }
        // If RevisionType it's DEL, it wasn't in the for cycle
        log.debug("CounterAudit ret:{}",  ret)
        return ret
    }

}

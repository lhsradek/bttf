package local.intranet.bttf.api.service

import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.entity.MessageEvent
import local.intranet.bttf.api.model.repository.CounterRepository
import local.intranet.bttf.api.model.repository.MessageEventRepository
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.info.MessageCount
import local.intranet.bttf.api.info.MessageEventInfo
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
import org.springframework.data.domain.PageImpl
/**
 *
 * {@link JobService} for {@link local.intranet.bttf.BttfApplication}
 *
 * @author Radek Kádner
 *
 */
@Service
@ConditionalOnExpression("\${scheduler.enabled}")
public class MessageService : Countable {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var counterRepository: CounterRepository
    
    @Autowired
    private lateinit var messageEventRepository: MessageEventRepository
    
    @Autowired
    private lateinit var provider: Provider

    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#messageInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun messageInfo(page: Int, size: Int): Page<MessageEventInfo> {
        val pageRequest = PageRequest.of(page, size)
        val list = mutableListOf<MessageEventInfo>()
        val listPage = messageEventRepository.findByName(pageRequest, javaClass.simpleName)
        listPage.forEach {
            with (it) {
                val (revisonNum, revisionType) = messageAudit(id)
                list.add(MessageEventInfo(
                        cnt, uuid, serviceName,
                        ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                        0L, revisonNum, revisionType)
                )
            }
        }
        return PageImpl<MessageEventInfo>(list, pageRequest, listPage.totalElements)
        // return ret
    }
    
    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#messageInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun messageByUuid(uuid: String): MessageEvent {
		return messageEventRepository.findByUuid(uuid)
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
     * Number of invocations
     *
     * @return number of invocations from count
     */
    @Transactional(readOnly = true)
    public override fun countValue(): Long {
        return messageEventRepository.findByName(PageRequest.of(0, 1), javaClass.simpleName).totalElements
    }


    @Transactional(readOnly = true)
    public fun countTotalMessageEvents(): List<MessageCount> {
        return messageEventRepository.countTotalMessageEvents()
    }
    
    /**
     *
     * MessageEvent audit
     *
     * @param messageId {@link Long}
     * @return {@link Pair}&lt;{@link Int}, {@link RevisionType}&gt;
     */
    @Transactional(readOnly = true)
    protected fun messageAudit(@NotNull messageId: Long?): Pair<Int, RevisionType> {
        // val ret: Pair<Int, RevisionType>
        var ret = Pair(0, RevisionType.DEL)
        val reader: AuditReader = provider.auditReader()
        val query: AuditQuery = reader.createQuery()
            .forRevisionsOfEntity(MessageEvent::class.java, true, true)
            .addProjection(AuditEntity.revisionNumber())
            .addProjection(AuditEntity.selectEntity(false))
            .add(AuditEntity.id().eq(messageId))
            .addOrder(AuditEntity.revisionNumber().desc())
            .addOrder(AuditEntity.id().desc())
        for (row in query.getResultList()) {
            // row is Object[] in Java
            // Object[] arr = (Object[]) row; in Java
            val arr = row as Array<*>
            val revisionNumber = arr[0] as Int
            // Map<String, Object> entity = (Map<String, Object>) arr[1]; in Java
            val entity = arr[1] as Map<*, *>
            ret = entity["REVTYPE"]?.let {
                Pair(revisionNumber, entity["REVTYPE"] as RevisionType) // It should be a MOD
            } ?: Pair(revisionNumber, RevisionType.ADD) // I know it went this way
            break // the first is enough for openAPI info
        }
        
        // If RevisionType it's DEL, it wasn't in the for cycle
        log.debug("MessageAudit messageId:{} ret:{}", messageId, ret)
        return ret
    }

    /**
     *
     * getCounter
     * // Increment counter
     *
     * @param external boolean
     * @return {@link Counter}
     */
    @Transactional  // write
    public fun getCounter(): CounterInfo {
    // public fun incrementCounter(): CounterInfo {
        /*
        val ret = counter?.let {
            with(counter) {
                cnt++
                timestmp = System.currentTimeMillis()
            }
            val newCnt = counterRepository.save(counter)
            val (revisonNum, revisionType) = messageAudit(newCnt.id)
            CounterInfo(
                newCnt.cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(newCnt.timestmp), ZoneId.systemDefault()),
                StatusType.valueOf(newCnt.status), newCnt.counterName, revisonNum, revisionType
            )
        } ?: with(
                counterRepository.save(Counter(null, javaClass.simpleName, 1L, System.currentTimeMillis(),
                        StatusType.UP.status))) {
                CounterInfo(
                    cnt,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    StatusType.valueOf(status),
                    counterName,
                    0,
                    RevisionType.DEL // If it's DEL, it wasn't in getCounterAudit
                )
        }
        */
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            with (counter) {
                val (revisonNum, revisionType) = messageAudit(id)
                CounterInfo(
                    cnt,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    StatusType.valueOf(status), counterName, revisonNum, revisionType)
            }
        } ?: with (counterRepository.save(
            Counter(null, javaClass.simpleName, 0, System.currentTimeMillis(),StatusType.UP.status))) {
            val (revisonNum, revisionType) = messageAudit(id)
            CounterInfo(
                cnt,
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
        		StatusType.valueOf(status), counterName, revisonNum, revisionType)
        }
        // log.debug("GetCounter ret:{}",  ret)
        return ret
    }

}

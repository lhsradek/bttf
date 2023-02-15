package local.intranet.bttf.api.service

import java.util.UUID
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.entity.MessageEvent
import local.intranet.bttf.api.model.repository.MessageEventRepository
import local.intranet.bttf.api.info.content.BttfCounter
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.info.MessageEventInfo
import local.intranet.bttf.api.info.content.Provider
import org.jetbrains.annotations.NotNull
import org.hibernate.envers.AuditReader
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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
public class MessageService : Countable, Invocationable, Statusable, BttfCounter() {

    private val log = LoggerFactory.getLogger(javaClass)

    @Value("\${scheduler.enabled}")
    private lateinit var scheduler: String
    
    @Autowired
    private lateinit var messageEventRepository: MessageEventRepository
    
    @Autowired
    private lateinit var provider: Provider

   /**
     *
     * Number of invocations
     *
     * @return number of invocations from count
     */
    @Transactional(readOnly = true)
    public override fun countValue(): Long = messageEventRepository
        .findByName(PageRequest.of(0, 1), javaClass.simpleName).totalElements
    
    public override fun incrementCounter(): Long = countValue()
    
    /**
     *
     * Time of last invocation
     *
     * @return {@link ZonedDateTime}
     */
    @Transactional(readOnly = true)
    public override fun lastInvocation(): ZonedDateTime = super.lastInvocationFromAudit(MessageEvent::class.java)

   /**
     *
     * Get status
     *
     * @return {@link StatusType}
     */
    public override fun getStatus(): StatusType = if (scheduler.toBoolean()) StatusType.UP else StatusType.DOWN

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
            with(it) {
                val (revisonNum, revisionType) = counterAudit(MessageEvent::class.java, id!!)
                list.add(MessageEventInfo(
                    id, uuid, serviceName,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    cnt, message, revisonNum, revisionType)
                )
            }
        }
        return PageImpl<MessageEventInfo>(list, pageRequest, listPage.totalElements)
    }
    
    /**
     *
     * For {@link local.intranet.bttf.api.controller.InfoController#messageInfo}
     *
     * @return {@link CounterInfo}
     */
    @Transactional(readOnly = true)
    public fun messageByUuid(uuid: String): MessageEvent = messageEventRepository.findByUuid(uuid)
    
    /**
     *
     * Send Message
     *
     * @return {@link StatusType}
     */
    @Transactional // write new message
    public fun sendMessage(message: String): MessageEvent = messageEventRepository
        .save(MessageEvent(
            null, "${UUID.randomUUID()}", javaClass.simpleName, 0, System.currentTimeMillis(), message
        ))

    @Transactional(readOnly = true)
    public fun countTotalMessageEvents(): List<ServiceCount> = messageEventRepository.countTotalMessageEvents()
    
}

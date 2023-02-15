package local.intranet.bttf.api.info.content

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.AbstractMap.SimpleEntry
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.info.content.Provider
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.repository.CounterRepository
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.hibernate.envers.AuditReader
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 *
 * {@link BttfCounter} for
 * {@link local.intranet.bttf.api.controller.IndexController},
 * {@link local.intranet.bttf.api.service.JobService},
 * {@link local.intranet.bttf.api.service.MessageService} and
 *
 * @author Radek Kádner
 */
@Component
public abstract class BttfCounter() : Countable, Invocationable, Statusable {
    
    private val log = LoggerFactory.getLogger(javaClass)
    
    @Autowired
    private lateinit var counterRepository: CounterRepository
        
    @Autowired
    private lateinit var provider: Provider

   /**
     *
     * Number of invocations
     *
     * @return number of invocations from count
     */
    @Transactional(readOnly = true)
    public open override fun countValue(): Long {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            counter.cnt
        } ?: 0L
        // log.debug("CountValue ret:{}", ret)
        return ret
    }

   /**
     *
     * Increment counter
     *
     * @return {@link Long}
     */
    @Transactional  // write
    public open override fun incrementCounter(): Long {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            with(counter) {
                cnt++
                timestmp = System.currentTimeMillis()
            }
            with(counterRepository.save(counter)) {
                val (revisonNum, revisionType) = counterAudit(Counter::class.java, id!!)
                CounterInfo(
                    cnt,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    counterName, StatusType.valueOf(status), revisonNum, revisionType
                )
            }
        } ?: with(counterRepository.save(Counter(null, javaClass.simpleName, 1L, System.currentTimeMillis(),
                StatusType.UP.status))) {
                CounterInfo(cnt,
                    ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault()),
                    counterName, StatusType.valueOf(status), 0,
                    RevisionType.DEL // If it's DEL, it wasn't in getCounterAudit
                )
        }
        // log.debug("IncrementCounter ret:{}", ret)
        return ret.count
    }
    
    /**
     *
     * Get status
     *
     * @return {@link StatusType}
     */
    @Transactional(readOnly = true)
    public open override fun getStatus(): StatusType {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            StatusType.valueOf(counter.status)
        } ?: StatusType.NONE
        // log.debug("GetStatus ret:{}", ret)
        return ret
    }

    /**
     *
     * Time of last invocation
     *
     * @return {@link ZonedDateTime}
     */
    @Transactional(readOnly = true)
    public open override fun lastInvocation(): ZonedDateTime {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(counter.timestmp), ZoneId.systemDefault())
        } ?: ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
        // log.debug("LastInvocation ret:{}", ret)
        return ret
    }

    /**
     *
     * Time of last invocation from envers audit
     *
     * with Counter::class.java
     *
     * @return {@link ZonedDateTime}
     */
    @Transactional(readOnly = true)
    public open fun lastInvocationFromAudit(): ZonedDateTime = lastInvocationFromAudit(Counter::class.java)

    /**
     *
     * Time of last invocation from envers audit
     *
     * cl is Counter::class.java
     * or MessageEvent::class.java in {@link local.intranet.bttf.api.service.MessageService}
     *
     * @param cl {@link Class}&lt;{@link Any}&gt;
     * @return {@link ZonedDateTime}
     */
    @Transactional(readOnly = true)
    public open fun lastInvocationFromAudit(@NotNull cl: Class<*>): ZonedDateTime {
    	var ret = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
    			val reader: AuditReader = provider.auditReader()
    			val query: AuditQuery = reader.createQuery()
    			.forRevisionsOfEntity(cl, true, true)
    			.setFirstResult(0)
    			.setMaxResults(1)
    			.addProjection(AuditEntity.property("timestmp"))
    			.addProjection(AuditEntity.selectEntity(false))
    			.addOrder(AuditEntity.revisionNumber().desc())
    			.addOrder(AuditEntity.id().desc())
    			for (row in query.getResultList()) {
    				val arr = row as Array<*>
    				val timestmp = arr[0] as Long
    				ret = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestmp), ZoneId.systemDefault())
    				break // the first is last
    			}
    	// log.debug("LastInvocationFromAudit cl:{} ret:{}", cl, ret)
    	return ret
    }
    
    /**
     *
     * Counter audit
     *
     * with Counter::class.java
     *
     * @param counterId {@link Long}
     * @return {@link Map.Entry}&lt;{@link Int}, {@link RevisionType}&gt;
     */
    protected open fun counterAudit(@NotNull counterId: Long): Map.Entry<Int, RevisionType> {
        val ret = counterAudit(Counter::class.java, counterId)
        // log.debug("CounterAudit counterId:{} ret:{}", counterId, ret)
        return ret
    } 
    
    /**
     *
     * Counter audit
     *
     * cl is Counter::class.java
     * or MessageEvent::class.java in {@link local.intranet.bttf.api.service.MessageService}
     *
     * @param cl        {@link Class}&lt;*&gt;
     * @param counterId {@link Long}
     * @return {@link Map.Entry}&lt;{@link Int}, {@link RevisionType}&gt;
     */
    @Transactional(readOnly = true)
    protected open fun counterAudit(@NotNull cl: Class<*>, @NotNull counterId: Long): Map.Entry<Int, RevisionType> {
        var ret = SimpleEntry<Int, RevisionType>(0, RevisionType.DEL)
        val reader: AuditReader = provider.auditReader()
        val query: AuditQuery = reader.createQuery()
            .forRevisionsOfEntity(cl, true, true)
            .setFirstResult(0)
            .setMaxResults(1)
            .addProjection(AuditEntity.revisionNumber())
            .addProjection(AuditEntity.selectEntity(false))
            .add(AuditEntity.id().eq(counterId))
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
                SimpleEntry<Int, RevisionType>(revisionNumber, entity["REVTYPE"] as RevisionType) // It should be a MOD
            } ?: SimpleEntry<Int, RevisionType>(revisionNumber, RevisionType.ADD) // I know it went this way
            break // the first is enough for openAPI info
        }
        // If RevisionType it's DEL, it wasn't in the for cycle
        // log.debug("CounterAudit cl:{} counterId:{} ret:{}", cl, counterId, ret)
        return ret
    }

}
package local.intranet.bttf.api.info.content

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import local.intranet.bttf.api.domain.Invocationable
import local.intranet.bttf.api.domain.Statusable
import local.intranet.bttf.api.domain.Countable
import local.intranet.bttf.api.domain.type.StatusType
import local.intranet.bttf.api.info.content.Provider
import local.intranet.bttf.api.info.CounterInfo
import local.intranet.bttf.api.model.entity.Counter
import local.intranet.bttf.api.model.repository.CounterRepository
import org.jetbrains.annotations.NotNull
import org.hibernate.envers.AuditReader
import org.hibernate.envers.RevisionType
import org.hibernate.envers.query.AuditEntity
import org.hibernate.envers.query.AuditQuery
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import org.springframework.transaction.annotation.Transactional

@Component
public abstract class BttfCounter() :  Countable, Invocationable {
    
    @Autowired
    private lateinit var counterRepository: CounterRepository
        
    @Autowired
    private lateinit var provider: Provider

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
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(with (counter) { timestmp } ), ZoneId.systemDefault())
        } ?: ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault())
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
            with(counter) { 
                cnt
            }
        } ?: 0L
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
                Pair(revisionNumber, entity["REVTYPE"] as RevisionType) // It should be a MOD
            } ?: Pair(revisionNumber, RevisionType.ADD) // I know it went this way
            break // the first is enough for openAPI info
        }
        // If RevisionType it's DEL, it wasn't in the for cycle
        // log.debug("CounterAudit counterId:{} ret:{}", counterId, ret)
        return ret
    }

   /**
     *
     * Increment counter
     *
     * @param external boolean
     * @return {@link Counter}
     */
    @Transactional  // write
    public open fun incrementCounter(): CounterInfo {
        val counter = counterRepository.findByName(javaClass.simpleName)
        val ret = counter?.let {
            with(counter) {
                cnt++
                timestmp = System.currentTimeMillis()
            }
            with(counterRepository.save(counter)) {
                val (revisonNum, revisionType) = counterAudit(id)
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
        return ret
    }
    
}
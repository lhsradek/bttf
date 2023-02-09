package local.intranet.bttf.api.info.content

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.PersistenceContext
import org.hibernate.envers.AuditReader
import org.hibernate.envers.AuditReaderFactory
import org.hibernate.envers.internal.reader.AuditReaderImpl
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *
 * {@link Provider} for
 * {@link local.intranet.bttf.api.controller.IndexController} and
 * {@link local.intranet.bttf.api.service.JobService}
 *
 * @author Radek Kádner
 *
 */
@Component
public class Provider {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var entityManagerFactory: EntityManagerFactory

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     *
     * Get queryProvider for
     * {@link local.intranet.bttf.api.controller.IndexController#signin}
     *
     * @param params {@link List}&lt;{@link Pair}&lt;{@link String},
     *               {@link String}&gt;&gt;
     * @return {@link String}
     */
    public fun queryProvider(params: List<Pair<String, String>>): String {
        val query = StringBuffer()
        params.forEach {
            // val (a, b) = it
            if (query.length > 0) {
                query.append("&")
            } else {
                query.append("?")
            }
            // query.append("${a}=${b}")
            query.append("${it.first}=${it.second}")
        }
        return "${query}"
    }

    /**
     *
     * Get AuditReader for
     * {@link local.intranet.bttf.api.service.JobService#counterAudit} and
     * {@link local.intranet.bttf.api.service.MessageService#messageAudit}
     *
     * @return {@link AuditReader}
     */
    public fun auditReader(): AuditReader {
        val ret: AuditReader
        val r = AuditReaderFactory.get(entityManager) as AuditReaderImpl
        if (r.session.isOpen()) {
            ret = r
        } else {
            ret = AuditReaderFactory.get(entityManagerFactory.createEntityManager())
            log.warn("AuditReader create EntityManager!")
        }
        return ret
    }

}
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
 * {@link local.intranet.bttf.api.service.BttfService}
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
    fun queryProvider(params: List<Pair<String, String>>): String {
        val query = StringBuilder()
        var first = true
        params.forEach {
            val (a, b) = it
            if (first) {
                query.append("?")
                query.append(a)
                query.append("=")
                query.append(b)
                first = false
            } else {
                query.append("&")
                query.append(a)
                query.append("=")
                query.append(b)
            }
        }
        return query.toString()
    }

    /**
     *
     * Get AuditReader
     *
     * @return {@link AuditReader}
     */
    fun auditReader(): AuditReader {
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
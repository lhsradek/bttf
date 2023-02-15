package local.intranet.bttf.api.model.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.JpaRepository
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.model.entity.MessageEvent

/**
 *
 * {@link MessageEventRepository} is repository for JPA with
 * {@link local.intranet.bttf.api.model.entity.MessageEvent}
 *
 * @author Radek Kádner
 *
 */
public interface MessageEventRepository : JpaRepository<MessageEvent, Long> {

    /**
     *
     * Count total MessageEvents
     *
     * @return {@link List}&lt;{@link LevelCount}&gt;
     */
    @Query(
        value = "select new local.intranet.bttf.api.info.ServiceCount(u.serviceName, count(u.serviceName)) " +
                "from MessageEvent u group by u.serviceName order by u.serviceName asc"
    )
    public fun countTotalMessageEvents(): List<ServiceCount>

    /**
     *
     * Find by uuid
     *
     * @param uuid {@link String}
     * @return {@link MessageEvent}?
     */
    @Query(value = "select u from MessageEvent u where u.uuid = ?1")
    public fun findByUuid(uuid: String): MessageEvent

    /**
     *
     * Find by name
     *
     * @param uuid {@link String}
     * @return {@link MessageEvent}?
     */
    @Query(value = "select u from MessageEvent u where u.serviceName = ?1")
    public fun findByName(pageable: Pageable, serviceName: String): Page<MessageEvent>

}

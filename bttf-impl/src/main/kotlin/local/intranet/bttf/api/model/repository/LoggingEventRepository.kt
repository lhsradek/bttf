package local.intranet.bttf.api.model.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.model.entity.LoggingEvent

/**
 *
 * {@link LoggingEventRepository} is repository for JPA with
 * {@link local.intranet.bttf.api.model.entity.LoggingEvent}
 * <p>
 * An immutable repository is only used to read data written by logback-spring
 * DbAppender
 *
 * @author Radek Kádner
 *
 */
public interface LoggingEventRepository : JpaRepository<LoggingEvent, Long> {

    /**
     *
     * Count total loggingEvents
     *
     * @return {@link List}&lt;{@link LevelCount}&gt;
     */
    @Query(
        value = "select new local.intranet.bttf.api.info.LevelCount(u.levelString, count(u.levelString)) " +
                "from LoggingEvent u group by u.levelString order by u.levelString asc"
    )
    public fun countTotalLoggingEvents(): List<LevelCount>

    /**
     *
     * Find page by LevelString
     *
     * @param pageable    {@link Pageable}
     * @param levelString {@link List}&lt;{@link String}&gt;
     * @return {@link Page}&lt;{@link LoggingEvent}&gt;
     */
    @Query(value = "select u from LoggingEvent u where u.levelString in ?1")
    public fun findPageByLevelString(pageable: Pageable, levelString: List<String>): Page<LoggingEvent>

    /**
     *
     * Find page by caller
     *
     * @param pageable     {@link Pageable}
     * @param callerClass  {@link List}&lt;{@link String}&gt;
     * @param callerMethod {@link List}&lt;{@link String}&gt;
     * @param levelString  {@link List}&lt;{@link String}&gt;
     * @return {@link Page}&lt;{@link LoggingEvent}&gt;
     */
    @Query(value = "select u from LoggingEvent u where u.callerClass in ?1 and u.callerMethod in ?2 and levelString in ?3")
    public fun findPageByCaller(
        pageable: Pageable, callerClass: List<String>, callerMethod: List<String>, levelString: List<String>
    ): Page<LoggingEvent>

}

package local.intranet.bttf.api.service

import local.intranet.bttf.api.domain.BttfConst
import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.LoggingEventInfo
import local.intranet.bttf.api.model.entity.LoggingEvent
import local.intranet.bttf.api.model.repository.LoggingEventRepository
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import org.jetbrains.annotations.NotNull
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.PageImpl


/**
 *
 * {@link LoggingEventService} for
 * {@link local.intranet.bttf.BttfApplication}
 * <p>
 * An entity {@link LoggingEvent} without setters is only used to read data
 * written by logback-spring DbAppender
 *
 * @author Radek Kádner
 *
 */
@Service
public class LoggingEventService {

    private val log = LoggerFactory.getLogger(javaClass)

    @Autowired
    private lateinit var loggingEventRepository: LoggingEventRepository

    /**
     *
     * countTotalLoggingEvents
     * <p>
     * Used
     * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository#countTotalLoggingEvents}
     *
     * @return {@link List}&lt;{@link LevelCount}&gt;
     */
    @Synchronized
    @Transactional(readOnly = true)
    public fun countTotalLoggingEvents(): List<LevelCount> = loggingEventRepository.countTotalLoggingEvents()

    /**
     *
     * findPageByLevelString {@link LoggingEvent}
     * <p>
     * Used
     * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository#findPageByLevelString}
     *
     * @param pageable    {@link Pageable}
     * @param levelString {@link List}&lt;{@link String}&gt; Filter must be or empty result!
     *
     * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
     */
    @Synchronized
    @Transactional(readOnly = true)
    @Throws(Exception::class)
    public fun findPageByLevelString(pageable: Pageable, messageString: List<String>): Page<LoggingEventInfo> {
        try {
            val list = mutableListOf<LoggingEventInfo>()
            val listPage: Page<LoggingEvent> = loggingEventRepository.findPageByLevelString(pageable, messageString)
            listPage.forEach {
                list.add(makeLoggingEventInfo(it))
            }
            return PageImpl<LoggingEventInfo>(list, pageable, listPage.totalElements)

        } catch (e: Exception) {
            // log.error(e.message, e)
            throw e
        }
    }

    /**
     *
     * findPageByCaller {@link LoggingEvent}
     * <p>
     * Used
     * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository#findPageByCaller}
     *
     * @param pageable     {@link Int}
     * @param cnt          {@link Int}
     * @param sort         {@link Sort}
     * @param callerClass  {@link List}&lt;{@link String}&gt;
     * @param callerMethod {@link List}&lt;{@link String}&gt;
     * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
     */
    @Synchronized
    @Transactional(readOnly = true)
    @Throws(Exception::class)
    public fun findPageByCaller(
        page: Int, @NotNull cnt: Int, sort: Sort, callerClass: List<String>, callerMethod: List<String>
    ): Page<LoggingEventInfo> {
        try {
            var s: String = sort.toString()
            val direction: Direction
            if (s.contains(Direction.DESC.toString())) {
                direction = Direction.DESC
            } else {
                direction = Direction.ASC
            }
            s = s.replace(Direction.ASC.toString(), "").replace(Direction.DESC.toString(), "").replace(":", "").trim()
            val pageable: Pageable = PageRequest.of(page, cnt, JpaSort.unsafe(direction, s.split(" ,")))
            val list = mutableListOf<LoggingEventInfo>()
            val listPage = loggingEventRepository.findPageByCaller(pageable, callerClass, callerMethod, listOf("INFO"))
            listPage.forEach {
                list.add(makeLoggingEventInfo(it))
            }

            return PageImpl<LoggingEventInfo>(list, pageable, listPage.totalElements)

        } catch (e: Exception) {
            // log.error(e.message, e)
            throw e
        }
    }

    /**
     *
     * Make loggingEventInfo
     *
     * @param loggingEvent {@link LoggingEvent}
     * @return {@link LoggingEventInfo}
     */
    @Synchronized
    protected fun makeLoggingEventInfo(loggingEvent: LoggingEvent): LoggingEventInfo {
        val s: List<String> = loggingEvent.callerClass.split("\\.")
        // if (dbg.toBoolean()) logger.debug("arg0:{} arg1:{} arg2:{} arg3:{}", arg0, arg1, arg2, arg3)
        return LoggingEventInfo(
            loggingEvent.id?.let { loggingEvent.id } ?: 0L,
            loggingEvent.formattedMessage,
            loggingEvent.levelString,
            if (s.size > 0) s.last() else "",
            loggingEvent.callerMethod,
            loggingEvent.arg0?.let { loggingEvent.arg0 } ?: BttfConst.NULL,
            loggingEvent.arg1?.let { loggingEvent.arg1 } ?: BttfConst.NULL,
            loggingEvent.arg2?.let { loggingEvent.arg2 } ?: BttfConst.NULL,
            loggingEvent.arg3?.let { loggingEvent.arg3 } ?: BttfConst.NULL,
            ZonedDateTime.ofInstant(Instant.ofEpochMilli(loggingEvent.timestmp), ZoneId.systemDefault()))
        // if (dbg.toBoolean()) log.debug("{}", ret)
        // return ret
    }

}

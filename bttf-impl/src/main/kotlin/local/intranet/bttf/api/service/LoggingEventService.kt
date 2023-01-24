package local.intranet.bttf.api.service

import java.util.Date

import javax.validation.constraints.NotNull

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.jpa.domain.JpaSort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import local.intranet.bttf.api.info.LevelCount
import local.intranet.bttf.api.info.LoggingEventInfo
import local.intranet.bttf.api.model.entity.LoggingEvent
import local.intranet.bttf.api.model.repository.LoggingEventRepository

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

    private val log = LoggerFactory.getLogger(LoggingEventService::class.java)
    
    @Value("\${bttf.app.debug:false}")
    private lateinit var dbg: String // toBoolean

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
    @Transactional(readOnly = true)
    fun countTotalLoggingEvents(): List<LevelCount> {
        val ret = loggingEventRepository.countTotalLoggingEvents()
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

    /**
     *
     * findPageByLevelString {@link LoggingEvent}
     * <p>
     * Used
     * {@link local.intranet.bttf.api.model.repository.LoggingEventRepository#findPageByLevelString}
     *
     * @param pageable    {@link Pageable}
     * @param levelString {@link List}&lt;{@link String}&gt;
     *
     * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
     */
    @Transactional(readOnly = true)
    @Throws(Exception::class)
    fun findPageByLevelString(pageable: Pageable, levelString: List<String>): Page<LoggingEventInfo> {
        try {
            val pa: Page<LoggingEvent> = loggingEventRepository.findPageByLevelString(pageable, levelString)
            val list = mutableListOf<LoggingEventInfo>()
            for (l in pa) {
                list.add(makeLoggingEventInfo(l))
            }
            val ret = PageImpl<LoggingEventInfo>(list, pageable, pa.totalElements)
            
            if (dbg.toBoolean()) log.debug("{}", ret)
            return ret
            
        } catch (e: Exception) {
            log.error(e.message, e)
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
     * @param pageable     int
     * @param cnt          int
     * @param sort         {@link Sort}
     * @param callerClass  {@link List}&lt;{@link String}&gt;
     * @param callerMethod {@link List}&lt;{@link String}&gt;
     * @return {@link Page}&lt;{@link LoggingEventInfo}&gt;
     */
    @Transactional(readOnly = true)
    @Throws(Exception::class)
    fun findPageByCaller(
        page: Int, @NotNull cnt: Int, sort: Sort, callerClass: List<String>,
        callerMethod: List<String>
    ): Page<LoggingEventInfo> {
        try {
            var s: String = sort.toString()
            var direction: Direction = Direction.ASC
            if (s.contains(Direction.DESC.toString())) {
                direction = Direction.DESC
            }
            s = s.replace(Direction.ASC.toString(), "").replace(Direction.DESC.toString(), "").replace(":", "")
            s = s.trim()
            val pageable: Pageable = PageRequest.of(page, cnt, JpaSort.unsafe(direction, s.split(" ,")))
            val pa = loggingEventRepository.findPageByCaller(pageable, callerClass, callerMethod, listOf("INFO"))
            val list = mutableListOf<LoggingEventInfo>()
            for (l in pa) {
                list.add(makeLoggingEventInfo(l))
            }
            
            val ret = PageImpl<LoggingEventInfo>(list, pageable, pa.totalElements)
            if (dbg.toBoolean()) log.debug("{}", ret)
            return ret
            
        } catch (e: Exception) {
            log.error(e.message, e)
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
    protected fun makeLoggingEventInfo(loggingEvent: LoggingEvent): LoggingEventInfo {
        val s: List<String> = loggingEvent.callerClass.split("\\.")
        val arg0: String = loggingEvent.arg0?.let { loggingEvent.arg0 } ?: "[NULL]"
        val arg1: String = loggingEvent.arg1?.let { loggingEvent.arg1 } ?: "[NULL]"
        val arg2: String = loggingEvent.arg2?.let { loggingEvent.arg2 } ?: "[NULL]"
        val arg3: String = loggingEvent.arg3?.let { loggingEvent.arg3 } ?: "[NULL]"
        // if (dbg.toBoolean()) logger.debug("arg0:{} arg1:{} arg2:{} arg3:{}", arg0, arg1, arg2, arg3)
        val ret: LoggingEventInfo = LoggingEventInfo(
            loggingEvent.id?.let { loggingEvent.id } ?: 0L, loggingEvent.formattedMessage,
            loggingEvent.levelString, if (s.size > 0) s.last() else "", loggingEvent.callerMethod,
            arg0, arg1, arg2, arg3, Date(loggingEvent.timestmp)
        )
        if (dbg.toBoolean()) log.debug("{}", ret)
        return ret
    }

}

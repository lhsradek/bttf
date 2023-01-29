package local.intranet.bttf.api.redis

import java.util.UUID
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.lang.Nullable
import org.springframework.stereotype.Service

/**
 *
 * {@link RedisMessageSubscriber} for
 * {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial <br>
 * https://www.baeldung.com/spring-data-redis-pub-sub
 * https://juejin.cn/post/6844903834200834062
 *
 */
@Service
class RedisMessageSubscriber : MessageListener {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     *
     * On message
     *
     * @param message {@link Message}
     * @param pattern {@link ByteArray?}
     */
    override fun onMessage(message: Message, @Nullable pattern: ByteArray?) {
        if (pattern == null) {
            log.info("[message:{} uuid:{}]",
                message.toString(), UUID.randomUUID())
        } else {
            log.info("[message:{} uuid:{} pattern:{}]",
                message.toString(), UUID.randomUUID(), String(pattern))
        }
    }
}

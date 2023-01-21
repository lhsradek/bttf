package local.intranet.bttf.api.redis

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
 * <p>
 * <img src="/bttf/res/redis.png"/>
 * <p>
 *
 */
@Service
class RedisMessageSubscriber: MessageListener {

   	val logger = LoggerFactory.getLogger(RedisMessageSubscriber::class.java)

	/**
	 * 
	 * On message
	 * 
	 * @param message {@link Message}
	 * @param pattern {@link ByteArray?}
	 */
	override fun onMessage(message: Message, @Nullable pattern: ByteArray?) {
		logger.info("OnMessage '{}'", String(message.body))
	}
}

package local.intranet.bttf.api.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericToStringSerializer

/**
 *
 * {@link RedisConfig} for {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial <br/>
 * https://www.baeldung.com/spring-data-redis-properties <br/>
 * https://www.baeldung.com/spring-session <br/>
 * https://www.baeldung.com/jedis-java-redis-client-library
 * <p>
 * <img src="/bttf/res/redis.png"/>
 *
 */
@Configuration
@EnableAutoConfiguration
class RedisConfig {

    @Value("\${spring.redis.password}")
    private lateinit var password: String

    @Value("\${spring.redis.database}")
    private lateinit var database: String // toInt

    @Value("\${spring.redis.host}")
    private lateinit var host: String

    @Value("\${spring.redis.port}")
    private lateinit var port: String // toInt

    @Value("\${spring.redis.timeout}")
    private lateinit var timeout: String // toLong

    /**
     *
     * Get JedisConnectionFactory
     *
     * @return {@link JedisConnectionFactory}
     */
    @Bean
    fun jedisConnectionFactory(): JedisConnectionFactory {
        val ret = JedisConnectionFactory()
        ret.standaloneConfiguration?.let { ret.standaloneConfiguration?.setPassword(password) }
        ret.standaloneConfiguration?.let { ret.standaloneConfiguration?.setDatabase(database.toInt()) }
        ret.standaloneConfiguration?.let { ret.standaloneConfiguration?.setHostName(host) }
        ret.standaloneConfiguration?.let { ret.standaloneConfiguration?.setPort(port.toInt()) }
        @Suppress("DEPRECATION")
        ret.clientConfiguration.getPoolConfig().get().setMaxWaitMillis(timeout.toLong())
        return ret
    }

    /**
     *
     * Get redisTemplate
     *
     * @return {@link RedisTemplate}&lt;{@link String}, {@link Object}&gt;
     */
    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val ret: RedisTemplate<String, Any> = RedisTemplate()
        ret.setConnectionFactory(jedisConnectionFactory())
        ret.setValueSerializer(GenericToStringSerializer<Any>(Any::class.java))
        return ret
    }

    /**
     * Get messageListener
     *
     * @return {@link MessageListenerAdapter}
     */
    @Bean
    fun messageListener(): MessageListenerAdapter {
        return MessageListenerAdapter(RedisMessageSubscriber())
    }

    /**
     *
     * Get redisContainer
     *
     * @return {@link RedisMessageListenerContainer}
     */
    @Bean
    fun redisContainer(): RedisMessageListenerContainer {
        val ret = RedisMessageListenerContainer()
        ret.setConnectionFactory(jedisConnectionFactory())
        ret.addMessageListener(messageListener(), topic())
        return ret
    }

    /**
     *
     * Get redisPublisher
     *
     * @return {@link MessagePublisher}
     */
    @Bean
    fun redisPublisher(): MessagePublisher {
        return RedisMessagePublisher(redisTemplate(), topic())
    }

    /**
     *
     * Get topic
     *
     * @return {@link ChannelTopic}
     */
    @Bean
    fun topic(): ChannelTopic {
        return ChannelTopic("bttf:messageQueue")
    }

}

package local.intranet.bttf.api.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

/**
 *
 * {@link RedisMessagePublisher} for
 * {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * <img src="/bttf/res/redis.png"/>
 * <p>
 *
 */
@Service
class RedisMessagePublisher : MessagePublisher {

    @Autowired
    private var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    private var topic: ChannelTopic

    /**
     *
     * Constructor with parameters
     *
     * @param redisTemplate {@link RedisTemplate}&lt;{@link String}, {@link Object}&gt;
     * @param topic         {@link ChannelTopic}
     */
    constructor(redisTemplate: RedisTemplate<String, Any>, topic: ChannelTopic) {
        this.redisTemplate = redisTemplate
        this.topic = topic
    }

    override fun publish(message: String) {
        redisTemplate.convertAndSend(topic.topic, message)
    }

}
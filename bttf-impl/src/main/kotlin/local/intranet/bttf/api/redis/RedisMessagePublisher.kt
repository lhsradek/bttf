package local.intranet.bttf.api.redis

import local.intranet.bttf.api.domain.MessagePublisher

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service


/**
 *
 * {@link RedisMessagePublisher} for
 * {@link local.intranet.bttf.BttfApplication}.
 *
 */
@Service
@AutoConfigureAfter(RedisMessageSubscriber::class)
public class RedisMessagePublisher : MessagePublisher {

    @Autowired
    private var redisTemplate: RedisTemplate<String, Any>

    @Autowired
    private var topic: ChannelTopic

    /**
     *
     * Constructor with parameters
     *
     * @param redisTemplate {@link RedisTemplate}&lt;{@link String}, {@link Any}&gt;
     * @param topic         {@link ChannelTopic}
     */
    public constructor(redisTemplate: RedisTemplate<String, Any>, topic: ChannelTopic) {
        this.redisTemplate = redisTemplate
        this.topic = topic
    }

    public override fun publish(message: String) = redisTemplate.convertAndSend(topic.topic, message)

}
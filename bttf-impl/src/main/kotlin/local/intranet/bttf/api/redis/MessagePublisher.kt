package local.intranet.bttf.api.redis

/**
 *
 * {@link MessagePublisher} for
 * {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial
 * <p>
 * <img src="/bttf/res/redis.png"/>
 * <p>
 *
 */
interface MessagePublisher {

    /**
     *
     * Publish message
     *
     * @param message {@link String}
     */
    fun publish(message: String)

}
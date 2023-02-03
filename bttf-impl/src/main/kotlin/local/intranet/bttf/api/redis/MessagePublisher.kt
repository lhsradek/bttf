package local.intranet.bttf.api.redis

/**
 *
 * {@link MessagePublisher} for
 * {@link local.intranet.bttf.BttfApplication}.
 * <p>
 * https://www.baeldung.com/spring-data-redis-tutorial
 *
 */
public interface MessagePublisher {

    /**
     *
     * Publish message
     *
     * @param message {@link String}
     */
    public fun publish(message: String)

}
package local.intranet.bttf.api.info.content

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public interface ServiceProvider<R : Request, T : Response> {

    /**
     *
     * Perform
     *
     * @param {@Request}
     * @return {@Response}
     */
    public fun perform(request: R): T

    /**
     *
     * Get Instance Name
     *
     * @return {@String}
     */
    public fun getInstanceName(): String = "Provider"

}
package local.intranet.bttf.api.info.content

/**
 *
 * https://blog.root.cz/trpaslikuv-blog/spring-boot-actuator-sledovani-stavu-aplikace/
 */
public interface ServiceProvider <R : Request, T : Response> {

    public fun perform(request : R): T
    
    public fun getInstanceName(): String = "Provider"

}
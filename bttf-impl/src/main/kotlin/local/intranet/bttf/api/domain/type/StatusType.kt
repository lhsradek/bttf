package local.intranet.bttf.api.domain.type

/**
 * 
 * {@link StatusType} for
 * {@link local.intranet.bttf.api.service.JobService} and
 * {@link local.intranet.bttf.api.model.entity.Counter}
 * <p>
 * UP, DOWN, NONE
 * 
 * @author Radek Kádner
 */
public enum class StatusType(val status: String) {

	/**
	 * 
	 * UP = "UP"
	 * <p>
	 * Service is ready
	 */
	UP("UP"),

	/**
	 * 
	 * DOWN = "DOWN"
	 * <p>
	 * Service isn't ready
	 */
	DOWN("DOWN"),

	/**
	 * 
	 * NONE = "NONE"
	 * <p>
	 * NONE is for non REST request mapping services as
	 * {@link local.intranet.bttf.api.service.LoggingEventService} and
	 * {@link local.intranet.bttf.api.service.LoginAttemptService}
	 */
	NONE("NONE")

}

package local.intranet.bttf.api.domain

import com.fasterxml.jackson.annotation.JsonProperty

/**
 *
 * {@link Countable} for {@link local.intranet.core.api.info.JobInfo},
 * {@link local.intranet.core.api.service.JobService}
 * 
 * 
 * @author Radek Kádner
 *
 */
public interface Countable {

	/**
	 * 
	 * Number of invocations
	 * <p>
	 * &#64;JsonProperty("count")
	 * 
	 * @return number of invocations from count
	 */
	@JsonProperty("count")
	public fun countValue(): Long

}

package local.intranet.bttf.api.domain

import java.time.ZonedDateTime
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * {@link Invocationable} for 
 * {@link local.intranet.core.api.info.JobInfo},
 * {@link local.intranet.core.api.service.JobService}
 * 
 * @author Radek Kádner
 *
 */
public interface Invocationable : Countable {

	/**
	 *
	 * Time of last invocation
	 * <p>
	 * &#64;JsonInclude(JsonInclude.Include.NON_NULL)
	 * <p>
	 * 
	 * @return lastInvocation
	 */
	@JsonProperty("date")
	@JsonFormat(pattern = Contented.CONTENT_DATE_REST_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	public fun lastInvocation(): ZonedDateTime

}

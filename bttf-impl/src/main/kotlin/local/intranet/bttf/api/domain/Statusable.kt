package local.intranet.bttf.api.domain

import local.intranet.bttf.api.domain.type.StatusType

/**
 * 
 * {@link Statusable} for 
 * {@link local.intranet.core.api.info.JobInfo},
 * {@link local.intranet.core.api.service.JobService}
 *
 * 
 * @author Radek Kádner
 *
 */
public interface Statusable {

	/**
	 * 
	 * Get status
	 * 
	 * @return {@link StatusType}
	 */
	public fun getStatus(): StatusType

}

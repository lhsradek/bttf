package local.intranet.bttf.api.model.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import local.intranet.bttf.api.model.entity.Counter

/**
 * 
 * {@link CounterRepository} is repository for CRUD with
 * {@link local.intranet.bttf.api.model.entity.Counter}
 * 
 * @author Radek Kádner
 *
 */
interface CounterRepository : CrudRepository<Counter, Long> {

	/**
	 *
	 * Find by name
	 * 
	 * @param counterName {@link String}
	 * @return {@link Counter}?
	 */
	@Query(value = "select u from Counter u where u.counterName = ?1")
	fun findByName(counterName: String): Counter?

}

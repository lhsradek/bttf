package local.intranet.bttf.api.model.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import local.intranet.bttf.api.info.ServiceCount
import local.intranet.bttf.api.model.entity.Counter

/**
 * 
 * {@link CounterRepository} is repository for CRUD with
 * {@link local.intranet.bttf.api.model.entity.Counter}
 * 
 * @author Radek Kádner
 *
 */
public interface CounterRepository : CrudRepository<Counter, Long> {

    /**
     *
     * Count total CounterName
     *
     * @return {@link List}&lt;{@link ServiceCount}&gt;
     */
    @Query(value = "select new local.intranet.bttf.api.info.ServiceCount(u.counterName, u.cnt) " +
            "from Counter u order by u.counterName asc"
    )
    public fun countTotalCounterName(): List<ServiceCount>

 	/**
	 *
	 * Find by name
	 * 
	 * @param counterName {@link String}
	 * @return {@link Counter}?
	 */
	@Query(value = "select u from Counter u where u.counterName = ?1")
	public fun findByName(counterName: String): Counter?

}

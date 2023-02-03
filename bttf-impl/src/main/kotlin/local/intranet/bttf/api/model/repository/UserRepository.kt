package local.intranet.bttf.api.model.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import local.intranet.bttf.api.model.entity.User

/**
 *
 * {@link UserRepository} is repository for CRUD with
 * {@link local.intranet.bttf.api.model.entity.User}
 *
 * @author Radek Kádner
 *
 */
public interface UserRepository : CrudRepository<User, Long> {

    /**
     *
     * Find by name
     *
     * @param userName {@link String}
     * @return {@link User}?
     */
    @Query(value = "select u from User u where u.userName = ?1")
    public fun findByName(userName: String): User?

}

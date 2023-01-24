package local.intranet.bttf.api.model.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

import local.intranet.bttf.api.model.entity.Role

/**
 *
 * {@link RoleRepository} is repository for CRUD with
 * {@link local.intranet.bttf.api.model.entity.Role}
 *
 * @author Radek Kádner
 *
 */
interface RoleRepository : CrudRepository<Role, Long> {

    /**
     *
     * Find by name
     *
     * @param roleName {@link String}
     * @return {@link Role}?
     */
    @Query(value = "select u from Role u where u.roleName = ?1")
    fun findByName(roleName: String): Role?

}

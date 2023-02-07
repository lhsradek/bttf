package local.intranet.bttf.api.model.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import local.intranet.bttf.api.model.entity.MessageEvent

/**
 * 
 * {@link MessageEventRepository} is repository for CRUD with
 * {@link local.intranet.bttf.api.model.entity.MessageEvent}
 * 
 * @author Radek Kádner
 *
 */
public interface MessageEventRepository : CrudRepository<MessageEvent, Long> {}

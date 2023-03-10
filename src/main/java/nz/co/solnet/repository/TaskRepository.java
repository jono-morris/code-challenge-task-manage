package nz.co.solnet.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import nz.co.solnet.model.Task;
import nz.co.solnet.model.TaskStatus;

/**
 * A repository for performing actions on persisted {@link Task} entities.
 */
public interface TaskRepository extends CrudRepository<Task, Integer> {

	/**
	 * Retrieve a {@link Task} from the data store using it's unique id.
	 * @param id the id of the {@code Task} to search for
	 * @return the {@code Task} if found
	 */
	@Query("SELECT task FROM Task task WHERE task.id =:id")
	@Transactional(readOnly = true)
	Optional<Task> findById(@Param("id") Integer id);

	/**
	 * Retrieve all {@link Task}s from the data store.
	 */
//	@Query("SELECT task FROM Task task ORDER BY task.dueDate DESC")
//	@Transactional(readOnly = true)
//	Page<Task> findAll(Pageable pageable);

	
	@Transactional(readOnly = true)
	Iterable<Task> findAll();
	
	
	/**
	 * Retrieve {@link Tasks}s from the data store by date and status, returning all tasks
	 * before the given due-date with the given status.
	 * @param date the upper date bound for due tasks
	 * @param status the task status to find
	 * @return a Collection of matching {@link Tasks}s
	 */
	@Query("SELECT task FROM Task task WHERE task.dueDate <= :date AND task.status = :status ORDER BY task.dueDate DESC")
	@Transactional(readOnly = true)
	Iterable<Task> findBeforeDueDateWithStatus(@Param("date") LocalDate date, @Param("status") TaskStatus status);

	/**
	 * Save a {@link Task} to the data store, either inserting or updating it.
	 * @param task the {@link Task} to save
	 */
//	void save(Task task);

	/**
	 * Delete a {@link Task} from the data store.
	 * @param id the id of the task to delete
	 */
	void deleteById(@Param("id") Integer id);

}

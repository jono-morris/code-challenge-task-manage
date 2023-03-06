package nz.co.solnet;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * A repository for performing CRUD actions on {@link Task} instances.
 */
public interface TaskRepository extends Repository<Task, Integer> {

	/**
	 * Retrieve a {@link Task} from the data store by id.
	 * @param id the id to search for
	 * @return the {@link Task} if found
	 */
	@Query("SELECT task FROM Task task WHERE task.id =:id")
	@Transactional(readOnly = true)
	Task findById(@Param("id") Integer id);

	/**
	 * Retrieve all {@link Task}s from the data store.
	 */
	@Query("SELECT task FROM Task task ORDER BY task.dueDate DESC")
	@Transactional(readOnly = true)
	Page<Task> findAll(Pageable pageable);

	/**
	 * Retrieve {@link Tasks}s from the data store by date and status, returning all tasks
	 * before the given due-date and with the given status.
	 * @param dueDate the date tasks must be due before
	 * @param status the task status to find
	 * @return a Collection of matching {@link Tasks}s
	 */
	@Query("SELECT task FROM Task task WHERE task.dueDate < :date AND task.status = :status ORDER BY task.dueDate DESC")
	@Transactional(readOnly = true)
	Page<Task> findBeforeDueDateAndStatus(@Param("date") LocalDate date, @Param("status") TaskStatus status,
			Pageable pageable);

	/**
	 * Save a {@link Task} to the data store, either inserting or updating it.
	 * @param task the {@link Task} to save
	 */
	void save(Task task);

	/**
	 * Delete a {@link Task} from the data store.
	 * @param id the id of the task to delete
	 */
	void deleteById(@Param("id") Integer id);

}

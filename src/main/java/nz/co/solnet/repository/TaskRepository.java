package nz.co.solnet.repository;

import java.time.LocalDate;
import java.util.Optional;

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
	 * Retrieve a {@code Task} from the data store using it's unique id.
	 * @param id the id of the {@code Task} to search for
	 * @return the {@code Task} if found
	 */
	@Query("SELECT task FROM Task task WHERE task.id =:id")
	@Transactional(readOnly = true)
	Optional<Task> findById(@Param("id") Integer id);


	/**
	 * Retrieves all {@code Tasks} from the repository.
	 * @return a collection of {@code Task} instances retrieved from the repository
	 */
	@Transactional(readOnly = true)
	Iterable<Task> findAll();
	
	/**
	 * Retrieve {@link Tasks}s from the repository by date and status, returning all tasks
	 * before the given due-date with the given status.
	 * @param date the upper date bound for due tasks
	 * @param status the task status to find
	 * @return a collection of {@code Task} instances retrieved from the repository
	 */
	@Query("SELECT task FROM Task task WHERE task.dueDate <= :date AND task.status = :status ORDER BY task.dueDate DESC")
	@Transactional(readOnly = true)
	Iterable<Task> findBeforeDueDateWithStatus(@Param("date") LocalDate date, @Param("status") TaskStatus status);

	/**
	 * Delete a {@link Task} from the repository.
	 * @param id the id of the task to delete
	 */
	void deleteById(@Param("id") Integer id);
}

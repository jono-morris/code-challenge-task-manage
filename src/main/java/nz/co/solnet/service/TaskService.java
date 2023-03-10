package nz.co.solnet.service;

import java.util.Optional;

import nz.co.solnet.model.Task;

/**
 * A Service for maintaining {@code Task} instances.
 */
public interface TaskService {

	/**
	 * Creates a new {@code Task}.
	 * @param task
	 * @return
	 */
	public Task createTask(Task task);

	/**
	 * Retrieves a {@code Task} by Id.
	 * @param taskId
	 * @return
	 */
	public Optional<Task> getTaskById(int id);

	/**
	 * Gets all {@code Tasks}.
	 * @return a collection of all {@code Tasks}
	 */
	public Iterable<Task> getTasks();

	/**
	 * Gets all overdue {@code Tasks} from the repository.
	 * @return a collection of all overdue {@code Tasks}
	 */
	public Iterable<Task> getOverdueTasks();
	
	/**
	 * Updates a {@code Task} instance.
	 * 
	 * @param id the id of the {@code Task} to update  
	 * @param task the {@code Task} to update
	 * @return details of the updated {@code Task}
	 */
	public Task updateTask(int id, Task task);

	/**
	 * Deletes a single {@code Task}.
	 * @param id the id of the {@code Task} to delete 
	 */
	public void deleteTaskById(int id);
}

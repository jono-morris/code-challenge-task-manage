package nz.co.solnet.service;

import java.util.Optional;

import nz.co.solnet.model.Task;

/**
 * A Service for maintaining {@code Task} instances.
 */
public interface TaskService {

	/**
	 * Creates a new {@code Task}.
	 * @param task the {@code Task} instance to save
	 * @return the created {@code Task}
	 */
	public Task createTask(Task task);

	/**
	 * Retrieves a {@code Task} by id.
	 * @param id the id of the {@code Task} to retrieve
	 * @return the found {@code Task} if any
	 */
	public Optional<Task> getTaskById(int id);

	/**
	 * Gets all {@code Tasks} from the repository.
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
	 * @param task details of the {@code Task} to update
	 * @return the updated {@code Task}
	 */
	public Task updateTask(int id, Task task);

	/**
	 * Deletes a single {@code Task}.
	 * @param id the id of the {@code Task} to delete 
	 */
	public void deleteTaskById(int id);
}

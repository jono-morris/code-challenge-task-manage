package nz.co.solnet.api;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nz.co.solnet.model.Task;
import nz.co.solnet.service.TaskService;

/**
 * The Controller for maintaining {@link Task} instances.
 */
@RestController
@RequestMapping("/v1/tasks/")
public class TaskController {

	/** The {@code TaskService} implementation injected by the Spring Framework. */
	private final TaskService taskService;
	
	/** Constructor. */
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * Gets details of a {@code Task} from the repository using the provided id.
	 * @param id the id of the task to retrieve
	 * @return the task if one was found
	 */
	@GetMapping("{id}")
	public Optional<Task> getTaskById(@PathVariable("id") int taskId) {
		return taskService.getTaskById(taskId);
	}
	
	/**
	 * Fetches all tasks held in the repository.
	 * @return a collection of all tasks retrieved from the repository
	 */
	@GetMapping
	public Iterable<Task> getAllTasks() {
		return taskService.getTasks();
	}
	
	/**
	 * Fetches all pending overdue tasks held in the repository.
	 * @return a collection of all overdue tasks retrieved from the repository
	 */
	@GetMapping("overdue")
	public Iterable<Task> getOverdueTasks() {
		return taskService.getOverdueTasks();
	}

	/**
	 * Creates a new {@code Task} in the repository.
	 * @param task details of the {@code Task} to save
	 * @return the created {@code Task} instance
	 */
	@PostMapping
	public Task createTask(@Valid @RequestBody Task task) {
		return  taskService.createTask(task);
	}
	
	/**
	 * Updates a {@code Task} in the repository.
	 * @param id the id of the {@code Task} to update
	 * @param task details of the task instance to update
	 * @return the updated {@code Task}
	 */
	@PutMapping("{id}")
	public Task updateTask(@PathVariable("id") int id, @Valid @RequestBody Task task) {
		return taskService.updateTask(id, task);
	}
	
	/**
	 * Deletes a {@code Task} from the repository.
	 * @param id the id of the {@code Task} to delete
	 */
	@DeleteMapping("{id}")
	public void deleteTask(@PathVariable("id") int id) {
		taskService.deleteTaskById(id);
	}
}

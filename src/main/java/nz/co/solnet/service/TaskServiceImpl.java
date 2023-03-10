package nz.co.solnet.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nz.co.solnet.api.TaskNotFoundException;
import nz.co.solnet.model.Task;
import nz.co.solnet.model.TaskStatus;
import nz.co.solnet.repository.TaskRepository;

/**
 * A service implementation for managing {@code Task} instances held in a
 * repository.
 */
@Service
public class TaskServiceImpl implements TaskService {

	/**
	 * The repository managed by the service implementation.
	 */
	private TaskRepository taskRepository;

	/**
	 * Constructor.
	 * 
	 * @param taskRepository the repository injected by the Spring Framework.
	 */
	@Autowired
	public TaskServiceImpl(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Task createTask(Task task) {
		return taskRepository.save(task);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Task> getTaskById(int taskId) {
		return taskRepository.findById(taskId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<Task> getTasks() {
		return taskRepository.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<Task> getOverdueTasks() {
		return taskRepository.findBeforeDueDateWithStatus(LocalDate.now(), TaskStatus.PENDING);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Task updateTask(int taskId, Task task) {

		Task existingTask = taskRepository.findById(taskId)
				.orElseThrow(() -> new TaskNotFoundException(String.format("No task with id %s is available", taskId)));

		existingTask.setTitle(task.getTitle());
		existingTask.setDescription(task.getDescription());
		existingTask.setStatus(task.getStatus());
		existingTask.setDueDate(task.getDueDate());
		existingTask.setCreationDate(task.getCreationDate());
		return this.taskRepository.save(existingTask);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteTaskById(int taskId) {
		taskRepository.findById(taskId)
				.orElseThrow(() -> new TaskNotFoundException(String.format("No task with id %s is available", taskId)));
		taskRepository.deleteById(taskId);
	}
}

package nz.co.solnet.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;

import nz.co.solnet.model.Task;
import nz.co.solnet.model.TaskStatus;

/**
 * These tests start and operate on a running system, suitable for integration
 * testing.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaskControllerApplicationTest {

	/** Assigns a random port to start the server on. */
	@Value(value = "${local.server.port}")
	private int port;

	/** The rest-template automatically provided by Spring Boot. */
	@Autowired
	private TestRestTemplate restTemplate;

	/** Verify that all tasks can be retrieved. */
	@Test
	public void getAllTasks() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/", String.class)).isEqualTo(
				"[{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"},{\"id\":2,\"title\":\"an overdue task\",\"description\":\"overdue pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-02-08\",\"creationDate\":\"2023-02-01\"}]");
	}

	/** Verify that all overdue tasks can be retrieved. */
	@Test
	public void getOverdueTasks() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/overdue", String.class))
				.isEqualTo(
						"[{\"id\":2,\"title\":\"an overdue task\",\"description\":\"overdue pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-02-08\",\"creationDate\":\"2023-02-01\"}]");
	}

	/** Verify that a single task can be retrieved. */
	@Test
	public void getSingleTask() throws Exception {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/1", String.class)).isEqualTo(
				"{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"}");
	}

	/** Verify that a single Task can be updated. */
	@Test
	public void updateTask() throws Exception {

		LocalDate creationDate = LocalDate.now();
		LocalDate dueDate = creationDate.plusDays(2);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// take a snapshot of the task before updating
		Task originalTask = this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/2", Task.class);

		// create an updated task record
		Task updatedTask = new Task();
		updatedTask.setId(originalTask.getId());
		updatedTask.setTitle("an updated task");
		updatedTask.setDescription("an updated task description");
		updatedTask.setStatus(TaskStatus.COMPLETED);
		updatedTask.setDueDate(dueDate);
		updatedTask.setCreationDate(creationDate);

		// write the updated record
		this.restTemplate.put("http://localhost:" + port + "/v1/tasks/2", updatedTask);

		// verify the record was updated
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/", String.class))
				.isEqualTo(String.format(
						"[{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"},"
								+ "{\"id\":2,\"title\":\"an updated task\",\"description\":\"an updated task description\",\"status\":\"COMPLETED\",\"dueDate\":\"%s\",\"creationDate\":\"%s\"}]",
						dueDate.format(formatter), creationDate.format(formatter)));

		// restore the original task record
		this.restTemplate.put("http://localhost:" + port + "/v1/tasks/2", originalTask);

		// verify the record has been restored
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/", String.class))
				.isEqualTo(String.format(
						"[{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"},"
								+ "{\"id\":2,\"title\":\"an overdue task\",\"description\":\"overdue pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-02-08\",\"creationDate\":\"2023-02-01\"}]"));
	}

	/** Verify that a single Task can be added. */
	@Test
	public void createTask() throws Exception {

		LocalDate creationDate = LocalDate.now();
		LocalDate dueDate = creationDate.plusDays(2);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		// create a task instance to write
		Task newTask = new Task();
		newTask.setTitle("a new task");
		newTask.setDescription("an new description");
		newTask.setStatus(TaskStatus.PENDING);
		newTask.setDueDate(dueDate);
		newTask.setCreationDate(creationDate);

		// post the task to create an additional record
		Task addedTask = restTemplate.postForObject("http://localhost:" + port + "/v1/tasks/", newTask, Task.class);

		// verify the task instance returned from the 'create' call
		assertEquals(addedTask.getTitle(), newTask.getTitle());
		assertEquals(addedTask.getDescription(), newTask.getDescription());
		assertEquals(addedTask.getStatus(), newTask.getStatus());
		assertEquals(addedTask.getDueDate(), newTask.getDueDate());
		assertEquals(addedTask.getCreationDate(), newTask.getCreationDate());

		// verify the task was added
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/", String.class))
				.isEqualTo(String.format(
						"[{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"},"
								+ "{\"id\":2,\"title\":\"an overdue task\",\"description\":\"overdue pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-02-08\",\"creationDate\":\"2023-02-01\"},"
								+ "{\"id\":3,\"title\":\"a new task\",\"description\":\"an new description\",\"status\":\"PENDING\",\"dueDate\":\"%s\",\"creationDate\":\"%s\"}]",
						dueDate.format(formatter), creationDate.format(formatter)));

		// remove the added task
		this.restTemplate.delete("http://localhost:" + port + "/v1/tasks/3");

		// verify the task has been removed
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/v1/tasks/", String.class))
				.isEqualTo(String.format(
						"[{\"id\":1,\"title\":\"task to do\",\"description\":\"future pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-05-02\",\"creationDate\":\"2023-03-05\"},"
								+ "{\"id\":2,\"title\":\"an overdue task\",\"description\":\"overdue pending task\",\"status\":\"PENDING\",\"dueDate\":\"2023-02-08\",\"creationDate\":\"2023-02-01\"}]"));
	}
}

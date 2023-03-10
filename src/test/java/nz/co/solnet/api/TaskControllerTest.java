package nz.co.solnet.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import nz.co.solnet.model.Task;
import nz.co.solnet.model.TaskStatus;
import nz.co.solnet.service.TaskService;

/**
 * Unit tests for {@link TaskController} where Spring Boot instantiates
 * only the web layer rather than the whole context.
 */
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

	/** The task id to use for testing. */
	private static final int TEST_TASK_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	/**
	 * Mock instance of the task repository that the {@code TaskController} will
	 * use.
	 */
	@MockBean
	private TaskService tasksServicetasks;

	/** A {@code Task} instance available for testing purposes. */
	private Task task() {
		Task task = new Task();
		task.setId(TEST_TASK_ID);
		task.setTitle("a task to do");
		task.setDescription("task description");
		task.setStatus(TaskStatus.PENDING);
		task.setDueDate(LocalDate.now().plusDays(2));
		task.setCreationDate(LocalDate.now());

		return task;
	};

	@BeforeEach
	void setup() {
		Task task = task();
		given(this.tasksServicetasks.getTaskById(TEST_TASK_ID)).willReturn(Optional.of(task));
		given(this.tasksServicetasks.getTasks()).willReturn(Lists.newArrayList(task));
		given(this.tasksServicetasks.getOverdueTasks()).willReturn(Lists.newArrayList(task));
		given(this.tasksServicetasks.updateTask(any(Integer.class), any(Task.class))).willReturn(task);
		given(this.tasksServicetasks.createTask(any(Task.class))).willReturn(task);
	}

	/** Verify that the operation to get a single task is successful and returns HTTP 200. */
	@Test
	void testGetTask() throws Exception {
		mockMvc.perform(get("/v1/tasks/{id}", TEST_TASK_ID)).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(TEST_TASK_ID)).andExpect(jsonPath("$.title").value("a task to do"))
				.andExpect(jsonPath("$.description").value("task description"))
				.andExpect(jsonPath("$.status").value("PENDING"));
	}

	/** Verify that the operation to get all tasks is successful and returns HTTP 200. */
	@Test
	void testGetAllTasks() throws Exception {
		mockMvc.perform(get("/v1/tasks/")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(TEST_TASK_ID))
				.andExpect(jsonPath("$[0].title").value("a task to do"))
				.andExpect(jsonPath("$[0].description").value("task description"))
				.andExpect(jsonPath("$[0].status").value("PENDING"));
	}

	/** Verify that the operation to list all overdue tasks is successful and returns HTTP 200. */
	@Test
	void testGetAllOverdueTasks() throws Exception {
		mockMvc.perform(get("/v1/tasks/overdue")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value(TEST_TASK_ID))
				.andExpect(jsonPath("$[0].title").value("a task to do"))
				.andExpect(jsonPath("$[0].description").value("task description"))
				.andExpect(jsonPath("$[0].status").value("PENDING"));
	}

	/** Verify that the operation to delete a single task if successful and returns HTTP 200. */
	@Test
	public void testDelete() throws Exception {
		this.mockMvc.perform(delete("/v1/tasks/{id}", TEST_TASK_ID)).andExpect(status().isOk());
		Mockito.verify(tasksServicetasks).deleteTaskById(TEST_TASK_ID);
	}

	/** Verify that an operation to update a single task is successful and returns HTTP 200. */
	@Test
	void testUpdate() throws Exception {
		mockMvc.perform(put("/v1/tasks/{id}", TEST_TASK_ID).contentType(MediaType.APPLICATION_JSON)
				.content(toJsonString(task()))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

		Mockito.verify(tasksServicetasks).updateTask(any(Integer.class), any(Task.class));
	}

	/** Verify that an error is returned when attempting to update a task with incomplete information. */
	@Test
	void testUpdateValidationFailure() throws Exception {
		Task task = task();
		task.setTitle("");
		task.setCreationDate(null);

		mockMvc.perform(
				put("/v1/tasks/{id}", TEST_TASK_ID).contentType(MediaType.APPLICATION_JSON).content(toJsonString(task)))
				.andExpect(status().is4xxClientError()).andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(content().json("{'errors':['creation date required', 'title required']}"));
	}

	/** Verify that an operation to add a single task is successful and returns HTTP 200. */
	@Test
	void testCreate() throws Exception {
		mockMvc.perform(post("/v1/tasks/").contentType(MediaType.APPLICATION_JSON).content(toJsonString(task())))
				.andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON));

		Mockito.verify(tasksServicetasks).createTask(any(Task.class));
	}

	/**
	 * Utility method for converting a Task to a JSON string.
	 */
	private String toJsonString(Task task) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
		String requestJson = ow.writeValueAsString(task);
		return requestJson;
	}
}

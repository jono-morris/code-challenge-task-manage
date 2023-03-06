package nz.co.solnet;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Unit tests for {@link TaskController}.
 */
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

	/** The task id to use for testing. */
	private static final int TEST_TASK_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	/** Mock instance of the task repository that the {@code TaskController} will use. */
	@MockBean
	private TaskRepository tasks;

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
		given(this.tasks.findById(TEST_TASK_ID)).willReturn(task);
		given(this.tasks.findAll(any(Pageable.class))).willReturn(new PageImpl<Task>(Lists.newArrayList(task)));
		given(this.tasks.findBeforeDueDateAndStatus(any(LocalDate.class), any(TaskStatus.class), any(Pageable.class)))
				.willReturn(new PageImpl<Task>(Lists.newArrayList(task)));
	}

	/** Verify that 'new' HTTP GET operation is successful and returns HTTP 200. */
	@Test
	void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/tasks/new")).andExpect(status().isOk()).andExpect(model().attributeExists("task"))
				.andExpect(view().name("tasks/createOrUpdateTaskForm"));
	}

	/** Verify that the 'new' HTTP POST operation is successful and returns HTTP 301. */
	@Test
	void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/tasks/new").param("title", "a title").param("description", "a description")
				.param("dueDate", "2023-03-08").param("status", "PENDING")).andExpect(status().is3xxRedirection());
	}

	/**
	 * Verify that the 'new' HTTP POST operation returns validation errors if there are
	 * missing details.
	 */
	@Test
	void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/tasks/new").param("title", "a title").param("description", "a description"))
				.andExpect(status().isOk()).andExpect(model().attributeHasErrors("task"))
				.andExpect(model().attributeHasFieldErrors("task", "status"))
				.andExpect(model().attributeHasFieldErrors("task", "dueDate"))
				.andExpect(view().name("tasks/createOrUpdateTaskForm"));
	}

	/** Verify that the 'find' HTTP GET operation is successful and returns HTTP 200. */
	@Test
	void testInitFindForm() throws Exception {
		mockMvc.perform(get("/tasks/find")).andExpect(status().isOk()).andExpect(model().attributeExists("task"))
				.andExpect(view().name("tasks/findTasks"));
	}

	/**
	 * Verify that the 'all' HTTP GET operation is successful and returns HTTP 301 for the
	 * single task record.
	 */
	@Test
	void testProcessFindFormSuccess() throws Exception {
		Page<Task> page = new PageImpl<Task>(Lists.newArrayList(task()));
		Mockito.when(this.tasks.findAll(any(Pageable.class))).thenReturn(page);
		mockMvc.perform(get("/tasks?page=1")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/tasks/1"));
	}

	/**
	 * Verify that the 'all' HTTP GET operation is successful and returns HTTP 200 for
	 * multiple task records.
	 */
	@Test
	void testProcessFindFormSuccessMultipleTask() throws Exception {
		Page<Task> page = new PageImpl<Task>(Lists.newArrayList(task(), task()));
		Mockito.when(this.tasks.findAll(any(Pageable.class))).thenReturn(page);
		mockMvc.perform(get("/tasks?page=1")).andExpect(status().isOk()).andExpect(view().name("tasks/tasksList"));
	}

	/**
	 * Verify that the 'all overdue' HTTP GET operation is successful and returns HTTP 301
	 * for the single task record.
	 */
	@Test
	void testProcessFindFormOverdue() throws Exception {
		Page<Task> page = new PageImpl<Task>(Lists.newArrayList(task(), new Task()));
		Mockito.when(this.tasks.findAll(any(Pageable.class))).thenReturn(page);
		mockMvc.perform(get("/tasks/overdue?page=1")).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/tasks/1"));
	}

	/** Verify that the 'edit' HTTP GET operation is successful and returns HTTP 200. */
	@Test
	void testInitUpdateTaskForm() throws Exception {
		mockMvc.perform(get("/tasks/{taskId}/edit", TEST_TASK_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("task"))
				.andExpect(model().attribute("task", hasProperty("title", is("a task to do"))))
				.andExpect(model().attribute("task", hasProperty("description", is("task description"))))
				.andExpect(model().attribute("task", hasProperty("status", is(TaskStatus.PENDING))))
				.andExpect(view().name("tasks/createOrUpdateTaskForm"));

	}

	/**
	 * Verify that the 'edit' HTTP POST operation is successful and returns HTTP 301 for
	 * the single task record.
	 */
	@Test
	void testProcessUpdateTaskFormUnchangedSuccess() throws Exception {
		mockMvc.perform(post("/tasks/{taskId}/edit", TEST_TASK_ID)).andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/tasks/{taskId}"));
	}

	/**
	 * Verify that the 'edit' HTTP POST operation returns validation errors when the
	 * edited fields are empty.
	 */
	@Test
	void testProcessUpdateTaskFormHasErrors() throws Exception {
		mockMvc.perform(post("/tasks/{taskId}/edit", TEST_TASK_ID).param("title", "").param("description", "")
				.param("status", "").param("dueDate", "")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("task"))
				.andExpect(model().attributeHasFieldErrors("task", "title"))
				.andExpect(model().attributeHasFieldErrors("task", "description"))
				.andExpect(model().attributeHasFieldErrors("task", "status"))
				.andExpect(model().attributeHasFieldErrors("task", "dueDate"))
				.andExpect(view().name("tasks/createOrUpdateTaskForm"));
	}

}

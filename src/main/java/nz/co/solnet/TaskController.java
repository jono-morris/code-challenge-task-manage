package nz.co.solnet;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Controller for maintaining {@link Task} instances.
 */
@Controller
public class TaskController {

	private static final String VIEWS_TASKS_CREATE_OR_UPDATE_FORM = "tasks/createOrUpdateTaskForm";

	/**
	 * The page size when returning multiple pages of {@code Task}s during a search.
	 */
	private static final int PAGE_SIZE = 5;

	/** The respository for performing CRUD actions of task instances. */
	private final TaskRepository tasks;

	public TaskController(TaskRepository tasks) {
		this.tasks = tasks;
	}

	@InitBinder("task")
	public void initTaskBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new TaskValidator());
	}

	/**
	 * Called before each and every @RequestMapping annotated method ensuring we always
	 * have fresh data, make sure that Task object always has an id.
	 * @param taskId a {@code Task}s Id, optional;
	 * @return Task
	 */
	@ModelAttribute("task")
	public Task findTask(@PathVariable(name = "taskId", required = false) Integer taskId) {
		return taskId == null ? new Task() : this.tasks.findById(taskId);
	}

	/**
	 * Creates a model object for creating a new {@code Task}.
	 * @return a page allowing task details to be entered
	 */
	@GetMapping("/tasks/new")
	public String initCreationForm(ModelMap model) {
		Task task = new Task();
		model.put("task", task);

		return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
	}

	/**
	 * Creates a new {@code Task} using provided details.
	 * @param task the {@code Task} to save
	 * @param result the result of validating the provided task
	 * @return a page to display the saved task
	 */
	@PostMapping("/tasks/new")
	public String processCreationForm(@Valid Task task, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
		}
		task.setCreationDate(LocalDate.now());
		this.tasks.save(task);

		return "redirect:/tasks/" + task.getId();
	}

	/**
	 * Returns a page providing options for finding {@code Tasks}s.
	 * @return a page for finding {@code Task}s
	 */
	@GetMapping("/tasks/find")
	public String initFindForm() {
		return "tasks/findTasks";
	}

	/**
	 * Fetches all tasks held in the repository.
	 * @param page which page of results to find when the repository contains a paginated
	 * list of {@code Task}s
	 * @param result allows an error to be provided in case no tasks were found in the
	 * repository
	 * @param model the model attributes for the view
	 * @return a page displaying details of a single {@code Task} if only one task was
	 * found else a paginated list of {@code Task}s
	 */
	@GetMapping("/tasks")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Task task, BindingResult result,
			Model model) {

		Page<Task> tasksResults = tasks.findAll(PageRequest.of(page - 1, PAGE_SIZE));
		if (tasksResults.isEmpty()) {
			result.reject("notFound", "not found");
			return "tasks/findTasks";
		}
		if (tasksResults.getTotalElements() == 1) {
			// 1 task found
			task = tasksResults.iterator().next();
			return "redirect:/tasks/" + task.getId();
		}

		// multiple tasks found
		return addPaginationModel(page, model, tasksResults);
	}

	/**
	 * Fetches all pending overdue tasks held in the repository.
	 * @param page which page of results to find when the repository contains a paginated
	 * list of {@code Task}s
	 * @param result allows an error to be provided in case no tasks were found in the
	 * repository
	 * @param model the model attributes for the view
	 * @return a page displaying details of a single {@code Task} if only one task was
	 * found else a paginated list of {@code Task}s
	 */
	@GetMapping("/tasks/overdue")
	public String processFindOverdueForm(@RequestParam(defaultValue = "1") int page, Task task, BindingResult result,
			Model model) {

		Page<Task> tasksResults = tasks.findBeforeDueDateAndStatus(LocalDate.now(), TaskStatus.PENDING,
				PageRequest.of(page - 1, PAGE_SIZE));
		if (tasksResults.isEmpty()) {
			result.reject("notFound", "not found");
			return "tasks/findTasks";
		}
		if (tasksResults.getTotalElements() == 1) {
			// 1 task found
			task = tasksResults.iterator().next();
			return "redirect:/tasks/" + task.getId();
		}

		// multiple tasks found
		return addPaginationModel(page, model, tasksResults);
	}

	private String addPaginationModel(int page, Model model, Page<Task> paginated) {
		List<Task> listTasks = paginated.getContent();
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", paginated.getTotalPages());
		model.addAttribute("totalItems", paginated.getTotalElements());
		model.addAttribute("listTasks", listTasks);
		return "tasks/tasksList";
	}

	/**
	 * Retrieves a {@code Task} from the repository using the provided id so that the
	 * {@code Task} details can be edited.
	 * @param taskId the id of the {@code Task} to edit
	 * @return a page allowing task details to be entered
	 */
	@GetMapping("/tasks/{taskId}/edit")
	public String initUpdateForm(@PathVariable("taskId") int taskId, ModelMap model) {
		Task task = tasks.findById(taskId);
		model.put("task", task);

		return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
	}

	/**
	 * Saves the updated {@code Task} using provided details.
	 * @param task the {@code Task} to save
	 * @param result the result validating the provided task
	 * @param taskId the id of the edited {@code Task}
	 * @return a page to display the saved task
	 */
	@PostMapping("/tasks/{taskId}/edit")
	public String processUpdateForm(@Valid Task task, BindingResult result, @PathVariable("taskId") int taskId) {
		if (result.hasErrors()) {
			return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
		}
		task.setId(taskId);
		this.tasks.save(task);

		return "redirect:/tasks/{taskId}";
	}

	/**
	 * Gets details of a {@code Task} from the repository using the provided id.
	 * @param taskId the id of the task to display
	 * @return the model attributes for the view
	 */
	@GetMapping("/tasks/{taskId}")
	public ModelAndView showTask(@PathVariable("taskId") int taskId) {
		ModelAndView mav = new ModelAndView("tasks/taskDetails");
		Task task = this.tasks.findById(taskId);
		mav.addObject(task);
		return mav;
	}

	/**
	 * Deletes a {@code Task} with the provided id from the repository.
	 * @param taskId the id of the {@code Task} to delete
	 * @return a page displaying the remaining tasks
	 */
	@DeleteMapping("/tasks/{taskId}")
	public String deleteTask(@PathVariable("taskId") int taskId) {
		tasks.deleteById(taskId);
		return "redirect:/tasks";
	}

}

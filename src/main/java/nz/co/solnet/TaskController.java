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

@Controller
public class TaskController {

	private static final String VIEWS_TASKS_CREATE_OR_UPDATE_FORM = "tasks/createOrUpdateTaskForm";

	private static final int PAGE_SIZE = 5;

	private final TaskRepository tasks;

	public TaskController(TaskRepository tasks) {
		this.tasks = tasks;
	}

	// https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-initbinder-model-design
	// it is strongly recommended that you do not use types from your domain model
	// such as
	// JPA or Hibernate entities as the model object in data binding scenarios.
	@InitBinder("task")
	public void initTaskBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new TaskValidator());
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: - Make sure
	 * we always have fresh data - Since we do not use the session scope, make sure that
	 * Task object always has an id (Even though id is not part of the form fields)
	 * @param taskId
	 * @return Task
	 */
	@ModelAttribute("task")
	public Task findTask(@PathVariable(name = "taskId", required = false) Integer taskId) {
		return taskId == null ? new Task() : this.tasks.findById(taskId);
	}

	@GetMapping("/tasks/new")
	public String initCreationForm(ModelMap model) {
		Task task = new Task();
		model.put("task", task);

		return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/tasks/new")
	public String processCreationForm(@Valid Task task, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
		}
		task.setCreationDate(LocalDate.now());
		this.tasks.save(task);

		return "redirect:/tasks/" + task.getId();
	}

	@GetMapping("/tasks/find")
	public String initFindForm() {
		return "tasks/findTasks";
	}

	/**
	 * Fetch all tasks
	 * @param page
	 * @param result
	 * @param model
	 * @return
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

	@GetMapping("/tasks/overdue")
	public String processFindOverdueForm(@RequestParam(defaultValue = "1") int page, Task task, BindingResult result,
			Model model) {

		Page<Task> tasksResults = tasks.findBeforeDateAndStatus(LocalDate.now(), TaskStatus.PENDING,
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

	@GetMapping("/tasks/{taskId}/edit")
	public String initUpdateForm(@PathVariable("taskId") int taskId, ModelMap model) {
		Task task = tasks.findById(taskId);
		model.put("task", task);

		return VIEWS_TASKS_CREATE_OR_UPDATE_FORM;
	}

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
	 * Custom handler for displaying a task.
	 * @param taskId the ID of the task to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/tasks/{taskId}")
	public ModelAndView showTask(@PathVariable("taskId") int taskId) {
		ModelAndView mav = new ModelAndView("tasks/taskDetails");
		Task task = this.tasks.findById(taskId);
		mav.addObject(task);
		return mav;
	}

	@DeleteMapping("/tasks/{taskId}")
	public String deleteTask(@PathVariable("taskId") int taskId) {
		tasks.deleteById(taskId);
		return "redirect:/tasks";
	}

}

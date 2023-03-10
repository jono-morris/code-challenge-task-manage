package nz.co.solnet.api;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import nz.co.solnet.model.Task;

/**
 * Validates a {@link Task} object.
 */
public class TaskValidator implements Validator {

	/** The message key for fields that require and entry. */
	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		Task task = (Task) obj;

		// the title cannot be empty
		if (!StringUtils.hasLength(task.getTitle())) {
			errors.rejectValue("title", REQUIRED, REQUIRED);
		}

		// the description cannot be empty
		if (!StringUtils.hasLength(task.getDescription())) {
			errors.rejectValue("description", REQUIRED, REQUIRED);
		}

		// the Status must be provided
		if (null == task.getStatus()) {
			errors.rejectValue("status", REQUIRED, REQUIRED);
		}

		// the due-date must be set
		if (task.getDueDate() == null) {
			errors.rejectValue("dueDate", REQUIRED, REQUIRED);
		}
	}

	/**
	 * This Validator validates only Task instances.
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Task.class.isAssignableFrom(clazz);
	}

}

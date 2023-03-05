package nz.co.solnet;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TaskValidator implements Validator {

	private static final String REQUIRED = "required";

	@Override
	public void validate(Object obj, Errors errors) {
		Task task = (Task) obj;
		String title = task.getTitle();

		// title validation
		if (!StringUtils.hasLength(title)) {
			errors.rejectValue("title", REQUIRED, REQUIRED);
		}

		if (!StringUtils.hasLength(task.getDescription())) {
			errors.rejectValue("description", REQUIRED, REQUIRED);
		}

		if (null == task.getStatus()) {
			errors.rejectValue("status", REQUIRED, REQUIRED);
		}

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

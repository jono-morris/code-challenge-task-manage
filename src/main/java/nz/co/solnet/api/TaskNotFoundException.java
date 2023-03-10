package nz.co.solnet.api;

/**
 * Exception thrown when a task is not found in the repository.
 */
public class TaskNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7044047857170920277L;

	/**
	 * Constructs a new exception with a detail message.
	 * 
	 * @param message the detail message
	 */
	public TaskNotFoundException(String message) {
		super(message);
	}
}

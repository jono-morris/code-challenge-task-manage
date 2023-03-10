package nz.co.solnet.model;

/**
 * Status values that a {@link Task} may have during it's life-cycle.
 */
public enum TaskStatus {

	/** A {@code Task} was completed successfully. */
	COMPLETED,

	/**
	 * A {@code Task} was cancelled before it was finished, no further action expected.
	 */
	CANCELED,

	/** A {@code Task} is still open. */
	PENDING;

}

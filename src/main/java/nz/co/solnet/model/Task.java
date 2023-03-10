package nz.co.solnet.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * A {@code Task} domain object.
 */
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {

	@Column(name = "title")
	@NotEmpty(message = "title required")
	private String title;

	@Column(name = "description")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private TaskStatus status;

	@Column(name = "due_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dueDate;

	@Column(name = "creation_date")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull(message = "creation date required")
	private LocalDate creationDate;

	public LocalDate getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDate creationDate) {
		this.creationDate = creationDate;
	}

	public LocalDate getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDate dueDate) {
		this.dueDate = dueDate;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
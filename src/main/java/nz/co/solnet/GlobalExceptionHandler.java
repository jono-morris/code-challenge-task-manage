package nz.co.solnet;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import nz.co.solnet.api.TaskNotFoundException;

/**
 * Global error handling component for exceptions thrown during operation of the API.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Handles {@link MethodArgumentNotValidException} thrown when details passed to the API fail to validate.
	 * @param ex the exception to handle
	 * @return Not Found (status code 404)
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Map<String, List<String>> body = new HashMap<>();

		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				.map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

		body.put("errors", errors);

		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * Handles {@link TaskNotFoundException} thrown when a resource could not be found.
	 * @param ex the exception to handle
	 * @return Not Found (status code 404)
	 */
	@ExceptionHandler(value = { TaskNotFoundException.class })
	protected ResponseEntity<Object> handleNotFoundException(TaskNotFoundException ex) {
		
		List<String> errors = Arrays.asList(ex.getMessage());
		Map<String, List<String>> body = new HashMap<>();
		body.put("errors", errors);
		
		return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
	}
}

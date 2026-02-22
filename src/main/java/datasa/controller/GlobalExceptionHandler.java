package datasa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();
		
		for (FieldError fe : e.getBindingResult().getFieldErrors()) {
			errors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
		}
		
		Map<String, Object> body = new HashMap<>();
		body.put("message", "MBTI를 다시 입력해주세요.");
		body.put("errors", errors);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
	
	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handle(ResponseStatusException e) {
		return ResponseEntity
				.status(e.getStatusCode())
				.body(Map.of(
						"status", e.getStatusCode().value(),
						"message", e.getReason()
				));
	}
}

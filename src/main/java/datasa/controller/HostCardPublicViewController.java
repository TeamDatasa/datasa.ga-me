package datasa.controller;

import datasa.domain.dto.HostCardResponse;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hosts")
public class HostCardPublicViewController {
	
	private final UserRepository userRepository;
	
	@GetMapping("/{hostId}/card")
	public HostCardResponse getHostCard(@PathVariable Long hostId) {
		User host = userRepository.findById(hostId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "호스트 없음"));
		
		if (host.getRole() != User.Role.HOST) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "호스트 아님");
		}
		
		if (!host.getHostCardPublic()) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비공개");
		}
		
		Integer age = calcAge(host.getBirthDate());
		return HostCardResponse.from(host, age);
	}
	
	private Integer calcAge(LocalDate birthDate) {
		if (birthDate == null) return null;
		return Period.between(birthDate, LocalDate.now()).getYears();
	}
}

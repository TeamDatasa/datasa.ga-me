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
		User user = userRepository.findById(hostId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음"));
		
		// 탈퇴 사용자는 카드 조회 불가(원하시면 이것도 허용으로 바꿀 수 있음)
		if (user.getStatus() == User.Status.DELETED) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자 없음");
		}
		
		Integer age = calcAge(user.getBirthDate());
		return HostCardResponse.from(user, age);
	}
	
	private Integer calcAge(LocalDate birthDate) {
		if (birthDate == null) return null;
		return Period.between(birthDate, LocalDate.now()).getYears();
	}
}
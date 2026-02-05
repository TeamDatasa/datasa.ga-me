package datasa.controller;

import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import datasa.service.TripLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip")
public class TripLikeController {
	
	private final TripLikeService tripLikeService;
	private final UserRepository userRepository;
	
	@PostMapping("/{tripId}/like")
	public ResponseEntity<TripLikeResponse> toggleLike(@PathVariable Long tripId,
													   Authentication authentication) {
		
		String email = extractEmail(authentication);
		if (email == null || email.isBlank()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."));
		
		TripLikeService.TripLikeResult result = tripLikeService.toggleLike(tripId, user);
		return ResponseEntity.ok(new TripLikeResponse(result.liked(), result.count()));
	}
	
	
	private String extractEmail(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) return null;
		
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails ud) {
			return ud.getUsername();
		}
		return String.valueOf(principal);
	}
	
	public record TripLikeResponse(boolean liked, long count) {
	}
}

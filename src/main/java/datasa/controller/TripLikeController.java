package datasa.controller;


import datasa.domain.entity.User;
import datasa.service.TripLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trip")
public class TripLikeController {
	
	
	private static final Long TEST_USER_ID = 1L;
	
	
	private final TripLikeService tripLikeService;
	
	
	@PostMapping("/{tripId}/like")
	public ResponseEntity<TripLikeResponse> toggleLike(@PathVariable Long tripId) {
		// test
		User user = new User();
		user.setUserId(TEST_USER_ID);
		
		TripLikeService.TripLikeResult result = tripLikeService.toggleLike(tripId, user);
		
		return ResponseEntity.ok(new TripLikeResponse(result.liked(), result.count()));
	}
	
	
	public record TripLikeResponse(boolean liked, long count) {
	}
}
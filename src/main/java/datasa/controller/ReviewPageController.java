package datasa.controller;

import datasa.domain.entity.Review;
import datasa.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewPageController {
	
	private final ReviewRepository reviewRepository;
	
	@GetMapping("/{reviewId}")
	public String detail(@PathVariable Long reviewId, Model model) {
		Review r = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new IllegalArgumentException("Review not found"));
		
		model.addAttribute("review", r);
		return "users/reviewsdetail";
	}
}

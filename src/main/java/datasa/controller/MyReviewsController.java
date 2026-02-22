package datasa.controller;

import datasa.domain.dto.ReviewCreateRequest;
import datasa.domain.dto.ReviewForm;
import datasa.domain.dto.ReviewUpdateRequest;
import datasa.domain.entity.Review;
import datasa.repository.ReviewRepository;
import datasa.service.MyReviewPageService;
import datasa.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage/reviews")
public class MyReviewsController {
	
	private final MyReviewPageService myReviewPageService;
	private final ReviewService reviewService;
	private final ReviewRepository reviewRepository;
	
	@GetMapping
	public String page(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(required = false) String mode,
			@RequestParam(required = false) Long tripId,
			@RequestParam(required = false) Long reviewId,
			Model model
	) {
		String email = userDetails.getUsername();
		
		model.addAttribute("items", myReviewPageService.getMyJoinedTrips(email));
		model.addAttribute("mode", mode == null ? "" : mode);
		
		// 폼 기본값
		ReviewForm form = new ReviewForm();
		if (tripId != null) form.setTripId(tripId);
		if (reviewId != null) form.setReviewId(reviewId);
		
		// edit/delete면 기존 리뷰 로드해서 채워넣기
		if ("edit".equals(mode) && reviewId != null) {
			Review r = reviewRepository.findById(reviewId)
					.orElseThrow(() -> new IllegalArgumentException("Review not found"));
			form.setReviewId(r.getReviewId());
			form.setTripId(r.getTrip().getTripId());
			form.setRating(r.getRating());
			form.setContent(r.getContent());
			model.addAttribute("targetReview", r);
		}
		
		if ("delete".equals(mode) && reviewId != null) {
			Review r = reviewRepository.findById(reviewId)
					.orElseThrow(() -> new IllegalArgumentException("Review not found"));
			form.setReviewId(r.getReviewId());
			form.setTripId(r.getTrip().getTripId());
			form.setContent(r.getContent()); // 확인용
			model.addAttribute("targetReview", r);
		}
		
		model.addAttribute("form", form);
		return "users/myreview";
	}
	
	@PostMapping("/create")
	public String create(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @ModelAttribute("form") ReviewForm form,
			BindingResult br,
			Model model
	) {
		if (br.hasErrors()) {
			model.addAttribute("items", myReviewPageService.getMyJoinedTrips(userDetails.getUsername()));
			model.addAttribute("mode", "new");
			return "users/myreview";
		}
		
		reviewService.create(
				userDetails.getUsername(),
				new ReviewCreateRequest(form.getTripId(), form.getRating(), form.getContent())
		);
		
		return "redirect:/mypage/reviews";
	}
	
	@PostMapping("/update")
	public String update(
			@AuthenticationPrincipal UserDetails userDetails,
			@Valid @ModelAttribute("form") ReviewForm form,
			BindingResult br,
			Model model
	) {
		if (form.getReviewId() == null) throw new IllegalArgumentException("reviewId required");
		
		if (br.hasErrors()) {
			model.addAttribute("items", myReviewPageService.getMyJoinedTrips(userDetails.getUsername()));
			model.addAttribute("mode", "edit");
			return "users/myreviews";
		}
		
		reviewService.update(
				userDetails.getUsername(),
				form.getReviewId(),
				new ReviewUpdateRequest(form.getRating(), form.getContent())
		);
		
		return "redirect:/mypage/reviews";
	}
	
	@PostMapping("/delete")
	public String delete(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam Long reviewId
	) {
		reviewService.delete(userDetails.getUsername(), reviewId);
		return "redirect:/mypage/reviews";
	}
}

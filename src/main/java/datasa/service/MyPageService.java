package datasa.service;

import datasa.domain.dto.MyPageProfileResponse;
import datasa.domain.dto.MyPageProfileUpdateRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class MyPageService {
	
	private final UserRepository userRepository;
	
	@Transactional(readOnly = true)
	public MyPageProfileResponse getProfile(String email) {
		User user = getActiveUser(email);
		return toResponse(user);
	}
	
	@Transactional
	public MyPageProfileResponse updateProfile(String email, MyPageProfileUpdateRequest req) {
		User user = getActiveUser(email);
		
		user.updateProfile(
				req.getName(),
				req.getBirthDate(),
				req.getGender(),
				req.getCountryCode(),
				req.getRegion(),
				req.getMbti(),
				req.getSmoking(),
				req.getDrinking(),
				req.getBio()
		);
		
		return toResponse(user);
	}
	
	@Transactional
	public void deactivateAccount(String email) {
		User user = getActiveUser(email);
		user.deactivate();
	}
	
	private User getActiveUser(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		
		if (user.getStatus() != User.Status.ACTIVE) {
			throw new SecurityException("Inactive user");
		}
		return user;
	}
	
	private MyPageProfileResponse toResponse(User u) {
		Integer age = (u.getBirthDate() == null)
				? null
				: Period.between(u.getBirthDate(), LocalDate.now()).getYears();
		
		return MyPageProfileResponse.builder()
				.email(u.getEmail())
				.name(u.getName())
				.birthDate(u.getBirthDate())
				.age(age)
				.gender(u.getGender())
				.countryCode(u.getCountryCode())
				.region(u.getRegion())
				.mbti(u.getMbti())
				.smoking(u.getSmoking())
				.drinking(u.getDrinking())
				.bio(u.getBio())
				.profileImageUrl(u.getProfileImageUrl())
				.role(u.getRole())
				.localVerified(u.getLocalVerified())
				.build();
	}
}

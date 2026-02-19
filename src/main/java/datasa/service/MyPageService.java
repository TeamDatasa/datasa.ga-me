package datasa.service;

import datasa.domain.dto.HostCardPublicResponse;
import datasa.domain.dto.MyPageProfileResponse;
import datasa.domain.dto.MyPageProfileUpdateRequest;
import datasa.domain.entity.User;
import datasa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


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
		
		if (req.getMbti() != null) user.setMbti(req.getMbti().toUpperCase());
		if (req.getSmoking() != null) user.setSmoking(req.getSmoking());
		if (req.getDrinking() != null) user.setDrinking(req.getDrinking());
		if (req.getBio() != null) user.setBio(req.getBio());
		
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
	
	@Transactional(readOnly = true)
	public HostCardPublicResponse getHostCardPublic(String email) {
		User user = getActiveUser(email);
		return new HostCardPublicResponse(user.isHostCardPublic());
	}
	
	@Transactional
	public void updateHostCardPublic(String email, Boolean isPublic) {
		User user = getActiveUser(email);
		
		// null 들어오면 false로 처리(안전)
		boolean v = Boolean.TRUE.equals(isPublic);
		
		// 엔티티가 HOST 검증까지 해줌
		user.toggleHostCardPublic(v);
	}
	
	@Transactional
	public MyPageProfileResponse setPresetProfileImage(String email, String profileImageUrl) {
		User user = getActiveUser(email);
		
		List<String> allowed = List.of(
				"/images/profile/profile1.png",
				"/images/profile/profile2.png",
				"/images/profile/profile3.png",
				"/images/profile/profile4.png",
				"/images/profile/profile5.png",
				"/images/profile/profile6.png"
		);
		
		if (!allowed.contains(profileImageUrl)) {
			throw new IllegalArgumentException("허용되지 않은 이미지");
		}
		
		user.setProfileImageUrl(profileImageUrl);
		return toResponse(user);
	}
	
	
}

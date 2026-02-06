package datasa.domain.dto;

import datasa.domain.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HostCardResponse {
	private Long hostId;
	private String name;
	private Integer age;
	private String gender;
	private String mbti;
	private String bio;
	
	public static HostCardResponse from(User u, Integer age) {
		return HostCardResponse.builder()
				.hostId(u.getUserId())
				.name(u.getName())
				.age(age)
				.gender(u.getGender() != null ? u.getGender().name() : null)
				.mbti(u.getMbti())
				.bio(u.getBio())
				.build();
	}
}

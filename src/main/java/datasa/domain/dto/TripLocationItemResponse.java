package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 여행 글 상세(read)에서 표시할 장소(일정) 1건
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripLocationItemResponse {
	private Integer orderNo;
	private String placeName;
	private String address;
	private Double lat;
	private Double lng;
	private String placeId;
}
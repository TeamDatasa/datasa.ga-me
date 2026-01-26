package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * writeForm에서 지도 선택으로 추가되는 일정 장소 1건
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripWriteSchedulePlaceRequest {
	private String placeId;    // kakao place id
	private String placeName;
	private String address;
	private Double lat;
	private Double lng;
}
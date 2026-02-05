package datasa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatReadEventDto {
    private Long roomId;
    private Long userId;
}

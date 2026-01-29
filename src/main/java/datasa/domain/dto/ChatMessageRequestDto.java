package datasa.domain.dto;



import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDto {

    /** 사용자가 입력한 원문 */
    private String originalText;

    /** 번역을 원하는 경우만 (ex: "en", "ja") */
    private String targetLanguage;
}

package datasa.domain.dto;

import datasa.domain.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Builder
@Setter
@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long messageId;
    private Long roomId;

    private Long senderId;
    private String senderNickname;
    private String senderProfileImageUrl;
    private String originalText;
    private String translatedText;
    private String targetLanguage;

    private LocalDateTime createdAt;

    public static ChatMessageResponseDto from(ChatMessage message) {
        return new ChatMessageResponseDto(
                message.getMessageId(),
                message.getChatRoom().getRoomId(),
                message.getSender().getUserId(),
                message.getSender().getName(),
                message.getSender().getProfileImageUrl(),
                message.getOriginalText(),
                message.getTranslatedText(),
                message.getTargetLanguage(),
                message.getCreatedAt()
        );
    }

    public static ChatMessageResponseDto fromTranslated(
            ChatMessage message,
            String translatedText,
            String targetLanguage
    ) {
        ChatMessageResponseDto dto = from(message);
        dto.setTranslatedText(translatedText);
        dto.setTargetLanguage(targetLanguage);
        return dto;
    }


}

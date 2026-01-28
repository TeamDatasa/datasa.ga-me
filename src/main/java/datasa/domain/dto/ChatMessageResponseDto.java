package datasa.domain.dto;

import datasa.domain.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponseDto {

    private Long messageId;
    private Long roomId;

    private Long senderId;
    private String senderNickname;

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
                message.getOriginalText(),
                message.getTranslatedText(),
                message.getTargetLanguage(),
                message.getCreatedAt()
        );
    }
}



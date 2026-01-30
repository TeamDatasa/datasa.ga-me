package datasa.service;


import datasa.client.GoogleTranslateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final GoogleTranslateClient googleTranslateClient;

    public String translate(String text, String targetLanguage) {
        try {
            return googleTranslateClient.translate(text, targetLanguage);
        } catch (Exception e) {
            System.err.println("[GOOGLE TRANSLATE FAIL] " + e.getMessage());
            return "[번역 실패]";
        }
    }
}

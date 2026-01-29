package datasa.service;

import org.springframework.stereotype.Service;

/**
 * C_008 채팅 번역 서비스 (Stub)
 * 실제 번역 API 연동 시 이 클래스만 교체하면 됨
 */
@Service
public class TranslationService {

    public String translate(String text, String targetLanguage) {

        //  현재는 Stub (가짜 번역)
        // 나중에 Papago / Google / OpenAI API로 교체
        return "[번역:" + targetLanguage + "] " + text;
    }


}


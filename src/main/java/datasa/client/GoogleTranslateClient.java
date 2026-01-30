package datasa.client;


import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
public class GoogleTranslateClient {

    private static final String GOOGLE_TRANSLATE_URL =
            "https://translate.googleapis.com/translate_a/single";

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String translate(String text, String targetLanguage) {

        String url = UriComponentsBuilder
                .fromUriString(GOOGLE_TRANSLATE_URL)
                .queryParam("client", "gtx")
                .queryParam("sl", "auto")
                .queryParam("tl", targetLanguage)
                .queryParam("dt", "t")
                .queryParam("q", text)
                .build()
                .toUriString();

        List<Object> response = restTemplate.getForObject(url, List.class);

        /*
         * 응답 구조:
         * [
         *   [
         *     ["번역문", "원문", null, null, ...]
         *   ],
         *   null,
         *   "en",
         *   ...
         * ]
         */
        return (String) ((List<?>) ((List<?>) response.get(0)).get(0)).get(0);
    }
}

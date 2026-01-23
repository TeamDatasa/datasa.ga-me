package datasa.service;

import datasa.config.NaverSearchProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NaverLocalSearchService {
	
	private final NaverSearchProperties props;
	
	private final RestTemplate restTemplate = new RestTemplate();
	
	public String search(String query, int display) {
		int safeDisplay = Math.min(Math.max(display, 1), 10);
		
		URI uri = UriComponentsBuilder
				.fromUriString("https://openapi.naver.com/v1/search/local.json")
				.queryParam("query", query)
				.queryParam("display", safeDisplay)
				.encode(StandardCharsets.UTF_8)
				.build()
				.toUri();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("X-Naver-Client-Id", props.clientId());
		headers.set("X-Naver-Client-Secret", props.clientSecret());
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		
		HttpEntity<Void> entity = new HttpEntity<>(headers);
		
		ResponseEntity<String> resp =
				restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
		
		return resp.getBody();
	}
}

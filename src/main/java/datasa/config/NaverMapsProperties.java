package datasa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.maps")
public record NaverMapsProperties(String clientId, String clientSecret) {}

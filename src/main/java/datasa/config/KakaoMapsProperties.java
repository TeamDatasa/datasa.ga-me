package datasa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// kakao.maps
@ConfigurationProperties(prefix = "kakao.maps")
public record KakaoMapsProperties(String jsKey) { }

package datasa;

import datasa.config.NaverMapsProperties;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties(NaverMapsProperties.class) // naver map api
public class GaMeApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(GaMeApplication.class, args);
	}
	
	// naver map 동작하는지 확인용 log
	@Bean
	CommandLineRunner envCheck() {
		return args -> {
			System.out.println("NAVER_MAPS_CLIENT_ID=" + System.getenv("NAVER_MAPS_CLIENT_ID"));
		};
	}
	
}
    
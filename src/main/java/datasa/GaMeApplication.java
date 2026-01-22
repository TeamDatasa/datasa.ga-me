package datasa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"datasa", "domain"})
@EntityScan(basePackages = "domain.entity")
@EnableJpaRepositories(basePackages = "datasa.repository")
public class GaMeApplication {

	public static void main(String[] args) {
		SpringApplication.run(GaMeApplication.class, args);
	}

}
    
package plagiatchecker.statservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StatServiceApplication.class, args);
	}

}

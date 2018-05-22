package hackqc18.Acclimate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcclimateApplication {

	public static void main(String[] args) {
//		StaticParser staticParser = new StaticParser();
		SpringApplication.run(AcclimateApplication.class, args);
	}
}

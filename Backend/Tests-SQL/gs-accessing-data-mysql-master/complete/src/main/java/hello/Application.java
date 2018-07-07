package hello;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class Application {
	
	private static final Logger log = LoggerFactory.getLogger(Application.class);
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    
    
    @Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			// save a couple of Users
			repository.save(new User("Jack Bauer", "bauer@gmail.com"));
			repository.save(new User("Chloe O'Brian", "obrian@gmail.com"));
			repository.save(new User("Kim  Bauer", "bauer@gmail.com"));
			repository.save(new User("David Palmer", "palmer@gmail.com"));
			repository.save(new User("Michelle Dessler", "dessler@gmail.com"));

			// fetch all Users
			log.info("Users found with findAll():");
			log.info("-------------------------------");
			for (User user : repository.findAll()) {
				log.info(user.toString());
			}
			log.info("");

			// fetch an individual customer by ID
			repository.findById(1L)
				.ifPresent(user -> {
					log.info("Customer found with findById(1L):");
					log.info("--------------------------------");
					log.info(user.toString());
					log.info("");
				});

			log.info("");
		};
	}
}

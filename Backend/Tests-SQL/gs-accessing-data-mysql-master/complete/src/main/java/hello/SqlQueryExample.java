package hello;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

public class SqlQueryExample {

    @Autowired
    private UserRepository userRepository;
    private MainController controller;


    public void runExample() {
    	controller.addNewUserManually ("Bob Smith", "bob.smith@gmail.com");
    	controller.addNewUserManually ("Marie Smith", "marie.smith@gmail.com");

        Optional<User> user1 = loadUserById(0);
        System.out.printf("User loaded: %s%n", user1);
        Optional<User> user2 = loadUserById(1);
        System.out.printf("User loaded: %s%n", user2);
    }

    public Optional<User> loadUserById(long id) {
        return userRepository.findById(id);
    }
    
    
    /**
    public void saveUsers(User[] users) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("id");

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(persons);
        int[] ints = simpleJdbcInsert.executeBatch(batch);
        System.out.printf("Batch Rows inserted: %s%n", Arrays.toString(ints));
    }
    **/
}
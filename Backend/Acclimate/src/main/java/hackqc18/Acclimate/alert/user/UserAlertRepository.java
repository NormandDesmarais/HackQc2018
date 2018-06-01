package hackqc18.Acclimate.alert.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import hackqc18.Acclimate.alert.Alert;

/**
 * Interface used to define the user alert repository. This interface will
 * automatically be instantiated as a Singleton by Spring at compile time by
 * inserting the following declaration in classes that needs this repository:
 *
 * @Autowired private UserAlertRepository userAlertRepository;
 */
// The @Repository informs Spring that this class plays the role of
// a repository class. Spring will instantiate it as a Singleton
// so that only one instance will be available at any time.
@Repository
public interface UserAlertRepository extends CrudRepository<Alert, String> {

    // CRUD stands for Create, Retrieve, Update and Delete, which are the
    // four basic database methods. A repository is a class that manages
    // connection to the database to create, retrieve, update and delete
    // entities (instances of a class saved to the database).
    //
    // Thanks to the magic of Spring, we only need to define interfaces
    // for our entities.
    // Spring will automatically create at compile time the methods and
    // a Singleton (a unique single instance) of the class with this
    // annotation:
    //
    // @Autowired
    // private UserAlertRepository userAlertRepository;
    //
    // With this simple declaration, Spring creates a concrete
    // implementation of the UserAlertRepository, makes it a Singleton
    // and instantiate the variable userAlertRepository to the
    // corresponding instance. This declaration could be used in
    // different classes, it will always refer to the same instance
    // (singleton) of UserAlertRepository.
    //
    // A repository interface for a given Entity (class) is created by
    // extending the CrudRepository interface with the class type
    // associated to the entity, followed by the type of its primary key.
    //
    // The Entity class must be annotated according to the JPA
    // (Java Persistence API) annotation "rules" and must follow
    // others JPA entities' constraints.
    //
    // By extending the CrudRepository, the basic CRUD methods are
    // automatically acquired, like the findById method. To add find
    // method by other property (attribute) types, one just need to
    // create a method. For example to have a find by the "name" property,
    // one just need to define the method:
    //
    // public Alert findByName(String name);
    //
    // Spring will automatically create the concrete implementation
    // for this method, or any similar methods, at compile time. It
    // mandatory to respect the camel case rule when naming the method.
    //
    // In the case where we have entities related to one another, it
    // is possible to refine the find method. For example, if we have
    // an entity (class) Student with @Id studentId that has also a
    // property "courses" defined as
    // private List<Course> courses;
    // And if Course is also an entity with @Id public long theId,
    // then we might want to find all students registered to a given course.
    // This could be done automatically and without effort, by adding
    // the following method in the StudentRepository interface:
    //
    // public List<Student> findByCourseTheId(long courseId);
    //
    // And that's it. No need to write the implementation, Spring will
    // infer it automatically if the method name respect the "rules"
    // and the classes respect the JPA annotations and rules.

}

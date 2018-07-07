package com.logicbig.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.object.SqlQuery;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

public class SqlQueryExample {

    @Autowired
    private DataSource dataSource;
    private SqlQuery<Person> personSqlQuery;

    @PostConstruct
    private void postConstruct() {
        personSqlQuery = new PersonSqlQuery(dataSource, "select * from ApplintestTable");
        personSqlQuery.declareParameter(new SqlParameterValue(Types.INTEGER, "id"));
    }

    public void runExmaple() {
        //Person person1 = Person.create("Dana", "Whitley", "464 Gorsuch Drive");
        //Person person2 = Person.create("Robin", "Cash", "64 Zella Park");
        //savePersons(new Person[]{person1, person2});
        Person person = loadPersonById(1);
        System.out.printf("Person loaded: %s%n", person);
        person = loadPersonById(2);
        System.out.printf("Person loaded: %s%n", person);
    }

    public Person loadPersonById(int id) {
        List<Person> persons = personSqlQuery.execute(id);
        if (persons.size() == 1) {
            return persons.get(0);
        }
        return null;
    }

    public void savePersons(Person[] persons) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("PERSON")
                .usingGeneratedKeyColumns("id");

        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(persons);
        int[] ints = simpleJdbcInsert.executeBatch(batch);
        System.out.printf("Batch Rows inserted: %s%n", Arrays.toString(ints));

    }
}
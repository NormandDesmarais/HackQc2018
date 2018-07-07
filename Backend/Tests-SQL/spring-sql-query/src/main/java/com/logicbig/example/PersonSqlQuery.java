package com.logicbig.example;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.object.SqlQuery;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;


public class PersonSqlQuery extends SqlQuery<Person> {

    public PersonSqlQuery(DataSource dataSource, String sql) {
        super(dataSource, sql);
    }

    @Override
    protected RowMapper<Person> newRowMapper(Object[] parameters, Map<?, ?> context) {
        System.out.printf("newRowMapper params: %s context: %s%n", Arrays.toString(parameters), context);
        return (ResultSet resultSet, int rowNum) -> {
            Person person = new Person();
            person.setId(resultSet.getInt("ID"));
            person.setPsw(resultSet.getInt("PSW"));
            person.setSalt(resultSet.getInt("SALT"));
            person.setName(resultSet.getString("NAME"));
            person.setUsername(resultSet.getString("USERNAME"));
            return person;
        };
    }
}
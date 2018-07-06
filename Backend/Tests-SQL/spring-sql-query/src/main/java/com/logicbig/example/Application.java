package com.logicbig.example;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


@SpringBootApplication
public class Application {
	
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        System.out.println("-- Running SqlQuery example --");
        context.getBean(SqlQueryExample.class).runExmaple();
        context.close();
    }
}
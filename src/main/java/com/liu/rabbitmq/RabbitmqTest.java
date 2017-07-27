package com.liu.rabbitmq;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RabbitmqTest {
    
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context=new ClassPathXmlApplicationContext("application-context.xml");
        Proceducer proceducer=(Proceducer) context.getBean("proceducer") ; 
        Person person=new Person("liucc",22);
        System.out.println(person);
        proceducer.sendMessage(person);        
        
    }
}
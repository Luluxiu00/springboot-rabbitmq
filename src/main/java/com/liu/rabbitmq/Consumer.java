package com.liu.rabbitmq;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

import javax.annotation.Resource;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

/**
 * 功能概要：消费接收
 * 
 * @author liucc
 * @since  2016年12月1日 
 */
public class Consumer implements MessageListener {
    //private Logger logger = LoggerFactory.getLogger(Consumer.class);
    @Resource
    private AmqpTemplate amqpTemplate;
    
    @Override
    public void onMessage(Message message) {
        //logger.info("receive message:{}",message);
        try {
            //将字节流对象转换成Java对象
            Person person=(Person) new ObjectInputStream(new ByteArrayInputStream(message.getBody())).readObject();
            System.out.println("年龄："+person.getAge());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String replyTo = message.getMessageProperties().getReplyTo();
        MessageConverter messageConverter=new SimpleMessageConverter();
        
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.getHeaders().putAll(message.getMessageProperties().getHeaders());
        
        String response=new String("收到返回消息");
        //将Java对象转成Message对象，并作为返回的内容，回送给生产者
        Message message2=messageConverter.toMessage(response, messageProperties);
        amqpTemplate.send(replyTo, message2);
        
        
    }
}
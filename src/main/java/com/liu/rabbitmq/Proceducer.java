package com.liu.rabbitmq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.stereotype.Service;

/**
 * 功能概要：消息产生,提交到队列中去
 * 
 * @author liucc
 * @since  2016年12月1日 
 */
@Service
public class Proceducer  {

    private Logger logger = LoggerFactory.getLogger(Proceducer.class);

    @Resource
    private AmqpTemplate amqpTemplate;

    public void sendMessage(Object message){
      //发送消息到消息队列服务器中，并得到回馈内容
      Object object=amqpTemplate.convertSendAndReceive("queueTest",message,new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties props = message.getMessageProperties();
                //把版本加入消息头中
                props.setHeader("header", "1.0.0");
                props.setExpiration(String.valueOf(30000));
                logger.debug("设置RPC消息的TTL为{}", 30000);
                return message;
            }
        });
      System.out.println(object);
    }
}
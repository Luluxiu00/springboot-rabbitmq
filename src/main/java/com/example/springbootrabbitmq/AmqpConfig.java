package com.example.springbootrabbitmq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.rabbitmq.client.Channel;

@Configuration
public class AmqpConfig {

	public static final String EXCHANGE = "spring-boot-exchange";
	public static final String QUEUE = "spring-boot-queue";
	public static final String ROUTINGKEY = "spring-boot-routingKey";
	
	
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		connectionFactory.setPublisherConfirms(true);
		return connectionFactory;
	}
	
	
	@Bean
	@Scope("prototype")
	public RabbitTemplate rabbitTemplate(){
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
		return rabbitTemplate;
	}
	
	/**
	 * 交换机
	 * @return
	 */
	@Bean
	public DirectExchange defaultExchange(){
		return new DirectExchange(EXCHANGE);
	}
	
	/**
	 * 队列
	 * @return
	 */
	@Bean
	public Queue queue(){
		return new Queue(QUEUE, true);
	}
	
	/**
	 * 绑定
	 * @return
	 */
	@Bean
	public Binding binding(){
		return BindingBuilder.bind(queue()).to(defaultExchange()).with(ROUTINGKEY);
	}
	
	
	/**
	 * 消息消费者
	 */
	@Bean
	public SimpleMessageListenerContainer messageContainer(){
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(queue());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
		container.setMessageListener(new ChannelAwareMessageListener() {
			
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				System.out.println("receive msg: " + new String(body));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				//确认消息消费成功
			}
		});
		
		return container;
	}
}
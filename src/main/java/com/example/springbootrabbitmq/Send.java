package com.example.springbootrabbitmq;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;
/**
 * 消息生产者
 * @author 326944
 *
 */
@Component
public class Send implements ConfirmCallback {
	
	private RabbitTemplate rabbitTemplate;

	/**
	 * 构造方法注入
	 * @param content
	 */
	public Send(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
		rabbitTemplate.setConfirmCallback(this);  //rabbitTemplate如果是单例,那回调就是最后设置的内容
	}
	
	
	public void sendMessage(String content){
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY, content, correlationId);
		
	}


	/**
	 * 回调
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.print("回调id: " + correlationData);
		if (ack) {
			System.out.println("消息消费成功");
		}else{
			System.out.println("消息消费失败: " + cause);
		}
		
	}
}

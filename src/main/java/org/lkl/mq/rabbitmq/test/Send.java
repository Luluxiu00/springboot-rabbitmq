package org.lkl.mq.rabbitmq.test;  

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

/**  
 * 消息publish  
 *   
 * @author liaokailin  
 * @version $Id: Send.java, v 0.1 2015年10月22日 下午3:48:09 liaokailin Exp $  
 */  
public class Send {  
	//交换机
    public final static String EXCHANGE_NAME = "test-exchange";  

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {  
        /**  
         * 配置amqp broker 连接信息  
         */  
        ConnectionFactory facotry = new ConnectionFactory();  
        facotry.setUsername("guest");  
        facotry.setPassword("guest");  
        facotry.setVirtualHost("/");  
        facotry.setHost("localhost");
        facotry.setPort(5672);  
        //获取一个链接  
        Connection conn = facotry.newConnection(); 
        //通过Channel进行通信  
        Channel channel = conn.createChannel();  

        // channel.exchangeDeclare(Send.EXCHANGE_NAME, "direct", true); //如果消费者已创建，这里可不声明  
        channel.confirmSelect(); //Enables publisher acknowledgements on this channel  
        channel.addConfirmListener(new ConfirmListener() {  

            @Override  
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {  
                System.out.println("[handleNack] :" + deliveryTag + "," + multiple);  

            }  

            @Override  
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {  
                System.out.println("[handleAck] :" + deliveryTag + "," + multiple);  
            }  
        });  

        String message = "lkl-";  
        //消息持久化 MessageProperties.PERSISTENT_TEXT_PLAIN  
        //发送多条信息，每条消息对应routekey都不一致  
        for (int i = 0; i < 10; i++) {  
            channel.basicPublish(EXCHANGE_NAME, message + (i % 2), MessageProperties.PERSISTENT_TEXT_PLAIN,  
                (message + i).getBytes());  
            System.out.println("[send] msg " + (message + i) + " of routingKey is " + (message + (i % 2)));  
        }  

    }  
}  
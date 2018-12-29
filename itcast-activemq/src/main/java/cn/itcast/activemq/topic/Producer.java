package cn.itcast.activemq.topic;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

public class Producer {

	public static void main(String[] args) throws Exception {
		// 1. 创建ActiveMQConnectionFactory连接工厂，需要ActiveMQ的服务地址，使用的是tcp协议
		String brokerURL = "tcp://192.168.12.168:61616";
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);

		// 2. 使用连接工厂创建连接
		Connection connection = factory.createConnection();

		// 3. 使用连接对象开启连接，使用start方法
		connection.start();

		// 4. 从连接对象里获取session
		// 第一个参数的作用是，是否使用JTA分布式事务，设置为false不开启
		// 第二个参数是设置应答方式，如果第一个参数是true，那么第二个参数就失效了，这里设置的应答方式是自动应答
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// 5. 从session获取消息类型Destination（模式（队列还是订阅），对应的名称），获取topic（名称为mytopic）
		// 参数就是设置订阅名称
		Topic topic = session.createTopic("mytopic");

		// 6. 从session中获取消息的生产者
		MessageProducer producer = session.createProducer(topic);
		//设置为持久化方式消息
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);

		// 7. 创建消息体 使用TextMessage类型
		TextMessage textMessage = new ActiveMQTextMessage();
		// 设置消息的内容
		textMessage.setText("你好！传智播客。");

		// 8. 使用消息的生产者发送消息
		producer.send(textMessage);
		System.out.println("【生产者】已发布主题模式消息... --- 你好！传智播客。");

		// 9. 释放资源
		producer.close();
		session.close();
		connection.close();
	}

}

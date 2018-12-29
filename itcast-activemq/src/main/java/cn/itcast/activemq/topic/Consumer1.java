package cn.itcast.activemq.topic;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer1 {

	public static void main(String[] args) throws Exception {
		// 1. 创建ActiveMQConnectionFactory连接工厂，需要ActiveMQ的服务地址，使用的是tcp协议
		String brokerURL = "tcp://192.168.12.168:61616";
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerURL);
		factory.setClientID("consumer-1");

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
		

		// 6. 从session中获取消息的消费者
		MessageConsumer consumer = session.createDurableSubscriber(topic, "consumer-1");
		
		// 7.接受消息
		// 参数表示接受消息等待的时间，单位是毫秒
		while (true) {
			Message message = consumer.receive(200000l);

			// 判断消息类型是TextMessage
			if (message instanceof TextMessage) {
				// 如果是，则进行强转
				TextMessage textMessage = (TextMessage) message;

				// 8. 消费消息，打印消息内容
				System.out.println("消费者1接收到消息：" + textMessage.getText());
			}
		}

	}

}

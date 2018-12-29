package cn.itcast.activemq.spring.queue;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class Producer {

	public static void main(String[] args) {
		// 创建spring容器
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-activemq-queue.xml");

		// 从spring容器中获取JMSTemplate，这个对象是用于发送消息的
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);

		//创建消息模式
		Destination destination = (Destination)context.getBean("queueDestination");
		
		// 使用JMSTemplate发送消息
		jmsTemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = new ActiveMQTextMessage();
				textMessage.setText("你好！黑马。---spring-queue的方式发送");

				System.out.println("已发送消息...");

				return textMessage;
			}
		});
	}
}

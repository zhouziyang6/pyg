package cn.itcast.activemq.spring.topic;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

public class TopicMessageListener2 extends AbstractAdaptableMessageListener {

	@Override
	public void onMessage(Message message, Session session) throws JMSException {
		// 判断消息类型是TextMessage
		if (message instanceof TextMessage) {
			// 如果是，则进行强转
			TextMessage textMessage = (TextMessage) message;
			try {
				// 消费消息，打印消息内容
				String text = textMessage.getText();
				System.out.println("TopicMessageListener2-消费者2消息监听器接收到消息；消息内容为：" + text);
			} catch (Exception e) {
				e.printStackTrace();
			}
			/**
			 * 在spring的配置文件配置监听容器的时候如果 AcknowledgeMode配置为CLIENT_ACKNOWLEDGE的话：
			 * 那么在监听器代码中抛出异常或者执行session.recover();则会将信息重新发送6次（默认每秒发一个消息）
			 * 在重发6次后消息还是处理失败，那么消息将自动到DLQ-死信队列(Dead Letter Queue用来保存处理失败或者过期的消息；
			 * 默认在ActiveMQ队列里面的名称为：ActiveMQ.DLQ)
			 */
			session.recover();
			
			/**
			 * 什么时候会重发：
			 * Messages are redelivered to a client when any of the following occurs:
			
			    A transacted session is used and rollback() is called.
			    A transacted session is closed before commit() is called.
			    A session is using CLIENT_ACKNOWLEDGE and Session.recover() is called.
			 */
		}
	}
}

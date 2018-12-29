package cn.itcast.activemq.spring.topic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer2 {

	public static void main(String[] args) {
		// 创建spring容器
		new ClassPathXmlApplicationContext("applicationContext-activemq-topic-consumer2.xml");
	}

}

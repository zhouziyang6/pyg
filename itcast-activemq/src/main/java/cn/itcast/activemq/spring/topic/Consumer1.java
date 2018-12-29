package cn.itcast.activemq.spring.topic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Consumer1 {

	public static void main(String[] args) {
		// 创建spring容器
		new ClassPathXmlApplicationContext("applicationContext-activemq-topic-consumer1.xml");
	}

}

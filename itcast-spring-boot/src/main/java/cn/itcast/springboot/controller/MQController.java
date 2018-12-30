package cn.itcast.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 生产者,发送消息
 */
@RequestMapping("/mq")
@RestController
public class MQController {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    /**
     * 发送一个MQ消息到队列
     */
    @GetMapping("/send")
    public String sendMapMsg(){
        Map<String,Object> map = new HashMap<>();
        map.put("id",123);
        map.put("name","传智播客");
        jmsMessagingTemplate.convertAndSend("spring.boot.map.queue",map);
        return "发送消息完成.";
    }

    @GetMapping("/sendSms")
    public String sendSmsMsg(){
        Map<String, String> map = new HashMap<>();
        map.put("mobile", "17671738100");
        map.put("signName", "黑马");
        map.put("templateCode", "SMS_125018593");
        map.put("templateParam", "{\"code\":\"667889\"}");
        jmsMessagingTemplate.convertAndSend("itcast_sms_queue", map);
        return "发送 sms 消息完成。";
    }

}

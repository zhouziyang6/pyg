package com.pinyougou.item.activemq.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

public class ItemDeleteMessageListener extends AbstractAdaptableMessageListener {
    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;
    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //接收数据并转换
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //遍历处理
        for (Long goodsId : goodsIds) {
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(filename);
            if (file.exists()){
                file.delete();
            }
        }
    }
}

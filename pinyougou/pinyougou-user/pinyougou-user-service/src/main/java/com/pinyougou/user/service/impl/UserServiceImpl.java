package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import tk.mybatis.mapper.entity.Example;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service(interfaceClass = UserService.class)
public class UserServiceImpl extends BaseServiceImpl<TbUser> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private ActiveMQQueue itcastSmsQueue;

    @Value("${signName}")
    private String signName;
    @Value("${templateCode}")
    private String templateCode;

    @Override
    public PageResult search(Integer page, Integer rows, TbUser user) {
        PageHelper.startPage(page, rows);

        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(user.get***())){
            criteria.andLike("***", "%" + user.get***() + "%");
        }*/

        List<TbUser> list = userMapper.selectByExample(example);
        PageInfo<TbUser> pageInfo = new PageInfo<>(list);

        return new PageResult(pageInfo.getTotal(), pageInfo.getList());
    }
    //生成验证码 并发送到指定的手机号码 发送同时储存在redis里 用于之后的验证
    @Override
    public void sendSmsCode(String phone) {
        //1、随机生成 6 位随机数作为验证码
        String code = (long)(Math.random() * 1000000) + "";
        System.out.println("验证码为：" + code);
        //2、存入到 redis 中并设置 5 分钟过期时间
        redisTemplate.boundValueOps(phone).set(code);
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.MINUTES);
        //3、发送短信相关参数到 activeMQ
        jmsTemplate.send(itcastSmsQueue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile",phone);
                mapMessage.setString("signName",signName);
                mapMessage.setString("templateCode",templateCode);
                mapMessage.setString("templateParam","{\"code\":"+code+"}");
                return mapMessage;
            }
        });
    }

    /**
     * 如果redis中对应的手机号存在并且与用户传递的一致说明验证通过
     * @param phone
     * @param smsCode
     * @return
     */
    @Override
    public boolean checkSmsCode(String phone, String smsCode) {
        //得到发送短信时 存储在 redis 里面的手机号码
        String code = (String) redisTemplate.boundValueOps(phone).get();
        //smsCode是用户输入的验证码  和redis里面的进行比较
        if (smsCode.equals(code)){
            //校验成功清除验证码 删除redis里面存储的数据
            redisTemplate.delete(phone);
            return true;
        }
        return false;
    }
}

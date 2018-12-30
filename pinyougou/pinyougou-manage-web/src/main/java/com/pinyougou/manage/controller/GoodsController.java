package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue itemSolrQueue;
    @Autowired
    private ActiveMQQueue itemSolrDeleteQueue;
    @Autowired
    private ActiveMQTopic itemTopic;
    @Autowired
    private ActiveMQTopic itemDeleteTopic;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll(){
        return goodsService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        return goodsService.findPage(page, rows);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods){
        try {
            //设置商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未申请审核
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    @GetMapping("/findOne")
    public Goods findOne(Long id){
        return goodsService.findGoodsById(id);
    }
    @PostMapping("/update")
    public Result update(@RequestBody Goods goods){
        try {
            //校验商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!sellerId.equals(oldGoods.getSellerId())||sellerId.equals(goods.getGoods().getSellerId())){
                return Result.fail("修改非法");
            }
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    /**
     * 蒋选择了的那些商品spu id数组对应的商品的删除状态修改为1
     * @param ids 商品spu id数组
     * @return  操作结果
     */
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.deleteByIds(ids);//已修改成物理删除
            //商品详细信息和商品sku都要一起删除 (未实现)

            //删除solr中对应的商品索引数据
            //itemSearchService.deleteItemByGoodsList(Arrays.asList(ids));
            sendMQsg(itemSolrDeleteQueue,ids);

            //发送主题消息
            sendMQsg(itemDeleteTopic,ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 发送MQ消息
     * @param destination 发送模式  (父类)
     * @param ids 商品spu id数组
     * @throws JMSException
     */
    private void sendMQsg(Destination destination,Long[] ids) throws JMSException {
        jmsTemplate.send(destination , new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage();
                objectMessage.setObject(ids);
                return objectMessage;
            }
        });
    }


    /**
     * 分页查询列表
     *
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods,
                             @RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        /*//只能查询当前商家自己的商品
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);*/
        return goodsService.search(goods,page,rows);
    }
    /**
     * 批量更新商品的审核状态
     * @param ids 商品spu id数组
     * @param status 要修改的状态 审核状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            goodsService.updateStatus(ids, status);
            if ("2".equals(status)){
                //如果审核通过则需要更新solr索引库数据
                //查询到需要更新的商品列表
                List<TbItem> itemList = goodsService.findItemListByGoodsIdsAndStatus(ids,"1");
                //导入商品列表到solr索引库
                //itemSearchService.importItemList(itemList);
                jmsTemplate.send(itemSolrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        TextMessage textMessage = session.createTextMessage();
                        textMessage.setText(JSON.toJSONString(itemList));
                        return textMessage;
                    }
                });
                //发送主题消息
                sendMQsg(itemTopic,ids);
            }
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }
}

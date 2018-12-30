package com.pinyougou.item.activemq.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.adapter.AbstractAdaptableMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据商品spu id数组生成商品详情静态页面
 */
public class ItemAuditMessageListener extends AbstractAdaptableMessageListener {
    //注入freeMarker
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference
    private GoodsService goodsService;

    @Reference
    private ItemCatService itemCatService;

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;

    @Override
    public void onMessage(Message message, Session session) throws JMSException {
        //接收数据并转换  (前面需要什么数据就转换成什么数据)
        ObjectMessage objectMessage = (ObjectMessage) message;
        Long[] goodsIds = (Long[]) objectMessage.getObject();

        //遍历数组并生成商品静态页面
        if (goodsIds != null && goodsIds.length > 0) {
            for (Long goodsId : goodsIds) {
                genItemHtml(goodsId);
            }
        }
        System.out.println("同步生成静态页面完成.");

    }

    /**
     * 根据商品spu id查询商品基本、描述、sku列表，
     * 并加载商品1、2、3级商品分类中文名称结合Freemarker模版生成html静态页面到指定路径。
     *
     * @param goodsId 商品spu id
     */
    private void genItemHtml(Long goodsId) {
        try {
            //获取模版
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //获取数据
            Map<String, Object> dataModel = new HashMap<>();
            //根据商品的spu id查询商品基本信息(基本,描述,已启用的sku列表)
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");

            //goods 商品基本信息
            dataModel.put("goods", goods.getGoods());
            //goodsDesc 商品描述信息
            dataModel.put("goodsDesc", goods.getGoodsDesc());
            //itemList 商品sku列表
            dataModel.put("itemList", goods.getItemList());
            //itemCat1 1级商品分类中文名称
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            //itemCat2 2级商品分类中文名称
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            //itemCat3 3级商品分类中文名称
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());

            //指定静态文件存放的路径
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            FileWriter fileWriter = new FileWriter(filename);

            //输出
            template.process(dataModel, fileWriter);

            //关闭资源
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

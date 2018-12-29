package com.pinyougou.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.Goods;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
//生成静态页面
@RestController
@RequestMapping("/test")
public class PageTestController {

    @Value("${ITEM_HTML_PATH}")
    private String ITEM_HTML_PATH;//properties里面的值

    @Reference
    private ItemCatService itemCatService;


    @Reference
    private GoodsService goodsService;


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    /**
     * 审核商品后生成商品 html 页面到指定路径
     * @param goodsIds 商品 id 集合
     * @return
     */
    @GetMapping("/audit")
    public String audit(Long[] goodsIds){
        for (Long goodsId : goodsIds) {
            genItemHtml(goodsId);
        }
        return "success";
    }
    private void genItemHtml(Long goodsId){
        try {
            //创建配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");

            //加载数据
            HashMap<String, Object> dataModel = new HashMap<>();
            //根据商品id查询商品基本,描述,启用的sku列表
            Goods goods = goodsService.findGoodsByIdAndStatus(goodsId, "1");
            //商品基本信息
            dataModel.put("goods",goods.getGoods());
            //商品描述信息
            dataModel.put("goodsDesc",goods.getGoodsDesc());

            //查询三级商品分类
            TbItemCat itemCat1 = itemCatService.findOne(goods.getGoods().getCategory1Id());
            dataModel.put("itemCat1", itemCat1.getName());
            TbItemCat itemCat2 = itemCatService.findOne(goods.getGoods().getCategory2Id());
            dataModel.put("itemCat2", itemCat2.getName());
            TbItemCat itemCat3 = itemCatService.findOne(goods.getGoods().getCategory3Id());
            dataModel.put("itemCat3", itemCat3.getName());

            //查询SKU商品列表
            dataModel.put("itemList",goods.getItemList());

            //输出到指定路径
            String filename = ITEM_HTML_PATH + goodsId + ".html";

            FileWriter fileWriter = new FileWriter(filename);
            //渲染模板数据
            template.process(dataModel,fileWriter);
            //关闭输出
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //模拟删除商品移除页面
    /**
     * 删除商品后删除指定路径下的商品 html 页面
     * @param goodsIds 商品 id 集合
     * @return
     */
    @GetMapping("/delete")
    public String delete(Long[] goodsIds){
        for (Long goodsId : goodsIds) {
            String filename = ITEM_HTML_PATH + goodsId + ".html";
            File file = new File(filename);
            if (file.exists()){
                file.delete();
            }
        }
        return "success";
    }
}

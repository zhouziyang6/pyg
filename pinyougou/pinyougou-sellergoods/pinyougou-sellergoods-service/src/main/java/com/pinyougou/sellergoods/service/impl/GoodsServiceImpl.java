package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(interfaceClass = GoodsService.class)
public class GoodsServiceImpl extends BaseServiceImpl<TbGoods> implements GoodsService {
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsDescMapper goodsDescMapper;
    @Autowired
    private ItemCatMapper itemCatMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private ItemMapper itemMapper;
    @Override
    public PageResult search(TbGoods goods, Integer page, Integer rows) {
        PageHelper.startPage(page,rows);

        Example example = new Example(TbGoods.class);
        Example.Criteria criteria = example.createCriteria();
        /*if(!StringUtils.isEmpty(seller.get***())){
            criteria.andLike("***", "%" + seller.get***() + "%");
        }*/

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息
        goodsMapper.insertSelective(goods.getGoods());//insert 没写值插入null insertSelective写什么插什么 没写的不改动

        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        saveItemList(goods);


    }

    private void saveItemList(Goods goods) {
        //如果启用了规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())){
            //新增SKU (类似   华为（HUAWEI） mate20pro手机 亮黑色 8G+128G 全网通（UD屏内指纹版）)
            for (TbItem item : goods.getItemList()) {

                //商品名称(SKU名称)
                String title = goods.getGoods().getGoodsName();

                //组合规格选项形成SKU标题
                Map<String,Object> map = JSON.parseObject(item.getSpec());
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    title+=" "+entry.getValue().toString();
                }
                item.setTitle(title);
                saveItemValue(goods, item);

                itemMapper.insertSelective(item);
            }
        }else {
            //如果没有启用规格,那就把spu标题当做一条sku,则只存在一条SKU信息
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(999);
            item.setStatus("0");
            item.setIsDefault("1");
            item.setSpec("{}");
            saveItemValue(goods, item);
            itemMapper.insertSelective(item);
        }
    }


    //抽取通用方法(新增SKU通用信息)
    private void saveItemValue(Goods goods, TbItem item) {
        //图片
        List<Map> imgList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imgList!=null&&imgList.size()>0){
            //将商品的第一张图作为sku的图片
            item.setImage(imgList.get(0).get("url").toString());
        }

        //商品分类id
        item.setCategoryid(goods.getGoods().getCategory3Id());

        //商品分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
        item.setCategory(itemCat.getName());

        //创建时间(系统时间)
        item.setCreateTime(new Date());

        //更新时间
        item.setUpdateTime(item.getCreateTime());

        //SPU商品id
        item.setGoodsId(goods.getGoods().getId());

        //商家id
        item.setSellerId(goods.getGoods().getSellerId());

        //商检名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getName());

        //品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
    }
}

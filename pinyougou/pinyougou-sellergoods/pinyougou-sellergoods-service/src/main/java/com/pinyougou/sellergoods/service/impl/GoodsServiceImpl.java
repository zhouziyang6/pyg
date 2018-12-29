package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
@Transactional
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

        //不查询删除状态为删除的商品
        criteria.andNotEqualTo("isDelete","1");

        if(!StringUtils.isEmpty(goods.getSellerId())){
            criteria.andEqualTo("sellerId",goods.getSellerId());
        }
        if(!StringUtils.isEmpty(goods.getAuditStatus())){
            criteria.andEqualTo("auditStatus",goods.getAuditStatus());
        }
        if(!StringUtils.isEmpty(goods.getGoodsName())){
            criteria.andLike("goodsName","%"+goods.getGoodsName()+"%");
        }

        List<TbGoods> list = goodsMapper.selectByExample(example);
        PageInfo<TbGoods> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public void addGoods(Goods goods) {
        //新增商品基本信息
        goodsMapper.insertSelective(goods.getGoods());//insert 没写值插入null insertSelective写什么插什么 没写的不改动

        //int x=1/0;//测试事务用


        //新增商品描述信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insertSelective(goods.getGoodsDesc());
        saveItemList(goods);


    }

    @Override
    public Goods findGoodsById(Long id) {

        return findGoodsByIdAndStatus(id,null);
    }

    @Override
    public void updateGoods(Goods goods) {
        //更新商品基本信息
        goods.getGoods().setAuditStatus("0");//修改过则重新设置为未审核
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());

        //更新商品描述信息
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());

        //删除原有的SKU列表
        TbItem param = new TbItem();
        param.setGoodsId(goods.getGoods().getId());
        itemMapper.delete(param);

        //保存商品SKU列表
        saveItemList(goods);

    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        TbGoods goods = new TbGoods();
        goods.setAuditStatus(status);
        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id", Arrays.asList(ids));
        goodsMapper.updateByExampleSelective(goods,example);//批量更新商品的审核状态

        //如果审核通过则将SKU设置为启动状态 审核通过
        if ("2".equals(status)){
            //更新内容
            TbItem item = new TbItem();
            item.setStatus("1");
            //更新条件
            Example itemExample = new Example(TbItem.class);
            itemExample.createCriteria().andIn("goodsId",Arrays.asList(ids));
            itemMapper.updateByExampleSelective(item,itemExample);

        }

    }
    //逻辑删除
    @Override
    public void deleteGoodsByIds(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsDelete("1");//修改状态为删除 solr删除应该是在manage里面删除的 不是逻辑删除更新

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //批量更新商品的删除状态为删除
        goodsMapper.updateByExampleSelective(goods,example);
    }
    //上架
    @Override
    public void itemUpshelf(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsMarketable("1");//修改状态为上架

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //批量更新商品上架
        goodsMapper.updateByExampleSelective(goods,example);
    }
    //下架
    @Override
    public void soldOut(Long[] ids) {
        TbGoods goods = new TbGoods();
        goods.setIsMarketable("0");//修改状态为下架

        Example example = new Example(TbGoods.class);
        example.createCriteria().andIn("id",Arrays.asList(ids));
        //批量更新商品下架
        goodsMapper.updateByExampleSelective(goods,example);
    }

    //根据商品SPU id集合和状态查询这些商品对应的SKU列表
    @Override
    public List<TbItem> findItemListByGoodsIdsAndStatus(Long[] ids, String status) {
        //创建条件对象
        Example example = new Example(TbItem.class);
        //根据状态查询
        example.createCriteria().andEqualTo("status",status).andIn("goodsId", Arrays.asList(ids));
        return itemMapper.selectByExample(example);
    }
    //根据商品id查询商品基本,描述,启用的sku列表
    @Override
    public Goods findGoodsByIdAndStatus(Long goodsId, String itemStatus) {
        Goods goods = new Goods();
        //查询商品SPU
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
        goods.setGoods(tbGoods);

        //查询商品描述
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
        goods.setGoodsDesc(tbGoodsDesc);

        //查询商品SKU列表
        Example example = new Example(TbItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("goodsId",goodsId);//andEqualTo ?=传进来的值

        if (!StringUtils.isEmpty(itemStatus)){
            criteria.andEqualTo("status",itemStatus);
        }
        example.orderBy("isDefault").desc();//按照是否是默认值降序排序,默认值为1,否则为0
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
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

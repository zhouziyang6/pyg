package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * 根据搜索关键字和其他条件到solr中查询数据
     * @param searchMap 搜索条件
     * @return 查询结果
     */
    Map<String, Object> search(Map<String, Object> searchMap);

    //导入商品列表到solr索引库
    void importItemList(List<TbItem> itemList);

    //删除solr中对应的商品索引数据
    void deleteItemByGoodsList(List<Long> goodsIdList);
}

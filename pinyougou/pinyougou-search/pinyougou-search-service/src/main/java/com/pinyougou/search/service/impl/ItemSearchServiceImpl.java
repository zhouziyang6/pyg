package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 实现搜索功能 打开搜索页面，在搜索框输入要搜索的关键字，点击搜索按钮即可进行搜索，展示搜索结果
 */
@Service(interfaceClass = ItemSearchService.class)//如果没有事务的话可以不需要写
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;//操作solr核心


    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();
        //处理搜索关键字的时候包含空格
        if (!StringUtils.isEmpty(searchMap.get("keywords"))){//replaceAll 把该字符串某个字符替换成xx
            searchMap.put("keywords",searchMap.get("keywords").toString().replaceAll(" ",""));
        }
        if (!StringUtils.isEmpty(searchMap.get("keywords"))) {
            //创建高亮搜索对象
            SimpleHighlightQuery query = new SimpleHighlightQuery();
            //查询条件 is 会对搜索关键字分词
            Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
            query.addCriteria(criteria);


            fiterQuery(searchMap, query);


            //设置高亮
            HighlightOptions highlightOptions = new HighlightOptions();
            highlightOptions.addField("item_title");//高亮域
            highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮起始标签
            highlightOptions.setSimplePostfix("</em>");//高亮结束标签
            query.setHighlightOptions(highlightOptions);
            //查询
            HighlightPage<TbItem> itemHighlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
            //处理高亮标题
            List<HighlightEntry<TbItem>> highlighted = itemHighlightPage.getHighlighted();
            if (highlighted != null && highlighted.size() > 0) {
                for (HighlightEntry<TbItem> entry : highlighted) {
                    List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                    if (highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets() != null) {
                        //设置高亮标题
                        entry.getEntity().setTitle(highlights.get(0).getSnipplets().get(0));
                    }
                }
            }
            //设置返回的商品列表
            resultMap.put("rows", itemHighlightPage.getContent());
            resultMap.put("totalPages", itemHighlightPage.getTotalPages());
            resultMap.put("total", itemHighlightPage.getTotalElements());
        } else {
            SimpleQuery query = new SimpleQuery("*:*");
            fiterQuery(searchMap, query);
            ScoredPage<TbItem> scoredPage = solrTemplate.queryForPage(query, TbItem.class);
            resultMap.put("rows", scoredPage.getContent());
            resultMap.put("totalPages",scoredPage.getTotalPages());
            resultMap.put("total",scoredPage.getTotalElements());
        }
        return resultMap;
    }
    //导入商品列表到solr索引库
    @Override
    public void importItemList(List<TbItem> itemList) {
        for (TbItem item : itemList) {
            Map specMap = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(specMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    //删除solr中对应的商品索引数据
    @Override
    public void deleteItemByGoodsList(List<Long> goodsIdList) {
        Criteria criteria = new Criteria("item_goodsid").in(goodsIdList);
        SimpleQuery query = new SimpleQuery(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }


    private void fiterQuery(Map<String, Object> searchMap, SimpleQuery query) {
        //按照分类过滤
        if (!StringUtils.isEmpty(searchMap.get("category"))) {
            Criteria categoryCriteria = new Criteria("item_category").is(searchMap.get("category"));
            SimpleFilterQuery categoryFiterQuery = new SimpleFilterQuery(categoryCriteria);
            query.addFilterQuery(categoryFiterQuery);
        }
        //按照品牌过滤
        if (!StringUtils.isEmpty(searchMap.get("brand"))) {
            Criteria brandCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            SimpleFilterQuery brandFiterQuery = new SimpleFilterQuery(brandCriteria);
            query.addFilterQuery(brandFiterQuery);
        }
        //按照规格过滤
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map<String, String>)searchMap.get("spec");
            Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Criteria specCriteria = new Criteria("item_spec_" + entry.getKey()).is(entry.getValue());
                SimpleFilterQuery specFiterQuery = new SimpleFilterQuery(specCriteria);
                query.addFilterQuery(specFiterQuery);
            }
        }
        //按照价格区间筛选
        if (!StringUtils.isEmpty(searchMap.get("price"))) {
            //获取起始,结束价格
            String[] prices = searchMap.get("price").toString().split("-");

            //价格大于等于起始价格
            Criteria startPriceCriteria = new Criteria("item_price").greaterThanEqual(prices[0]);
            SimpleFilterQuery starPriceFiterQuery = new SimpleFilterQuery(startPriceCriteria);
            query.addFilterQuery(starPriceFiterQuery);

            //价格小于等于结束价格
            if (!"*".equals(prices[1])) {
                Criteria endPriceCriteria = new Criteria("item_price").lessThanEqual(prices[1]);
                SimpleFilterQuery endPriceQuery = new SimpleFilterQuery(endPriceCriteria);
                query.addFilterQuery(endPriceQuery);
            }
        }



        //设置分页信息
        Integer pageNo = 1;
        Integer pageSize = 20;
        if (searchMap.get("pageNo") != null) {
            pageNo = Integer.parseInt(searchMap.get("pageNo").toString());
        }
        if (searchMap.get("pageSize") != null) {
            pageSize = Integer.parseInt(searchMap.get("pageSize").toString());
        }
        query.setOffset((pageNo - 1) * pageSize);//起始索引号
        query.setRows(pageSize);//页大小

        if (!StringUtils.isEmpty(searchMap.get("sortField"))&&!StringUtils.isEmpty(searchMap.get("sort"))){
            String sortOrder = searchMap.get("sort").toString();
            Sort sort = new Sort(sortOrder.equals("DESC") ? Sort.Direction.DESC : Sort.Direction.ASC,
                    "item_" + searchMap.get("sortField").toString());
            query.addSort(sort);
        }


    }
}

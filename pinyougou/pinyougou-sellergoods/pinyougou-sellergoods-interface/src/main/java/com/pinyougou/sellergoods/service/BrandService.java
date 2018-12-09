package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {
    /**
     * 查询品牌列表
     * @return 品牌列表
     */
    List<TbBrand> queryAll();
}

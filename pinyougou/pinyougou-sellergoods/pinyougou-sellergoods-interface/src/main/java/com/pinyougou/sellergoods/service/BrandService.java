package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.service.BaseService;

import java.util.List;

public interface BrandService extends BaseService<TbBrand> {
    /**
     * 查询品牌列表
     * @return 品牌列表
     */
    List<TbBrand> queryAll();

    List<TbBrand> testPage(Integer page, Integer rows);
}

package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import com.pinyougou.service.impl.BaseServiceImpl;
import com.pinyougou.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

//将服务暴露到dubbo注册中心
@Service(interfaceClass = BrandService.class)
public class BrandServiceImpl extends BaseServiceImpl<TbBrand> implements BrandService {

    @Autowired
    private BrandMapper brandMapper;
    @Override
    public List<TbBrand> queryAll() {
        return brandMapper.queryAll();
    }

    //dao层继承了通用接口的方法,不需要dao层写一般的查询语句,原来的查询语句不受影响
    @Override
    public List<TbBrand> testPage(Integer page, Integer rows) {
        PageHelper.startPage(page,rows);
        return brandMapper.selectAll();

    }
    //模糊 查询品牌
    @Override
    public PageResult search(TbBrand brand,Integer page, Integer rows) {
        //设置分页
        PageHelper.startPage(page,rows);
        //设置查询条件
        Example example = new Example(TbBrand.class);
        Example.Criteria criteria = example.createCriteria();
        //如果只查找首字符
        if (!StringUtils.isEmpty(brand.getFirstChar())){
            criteria.andEqualTo("firstChar",brand.getFirstChar());
        }
        //如果只模糊查找品牌名
        if (!StringUtils.isEmpty(brand.getName())){
            criteria.andLike("name", "%" + brand.getName() + "%");
        }
        List<TbBrand> list = brandMapper.selectByExample(example);
        PageInfo<TbBrand> pageInfo = new PageInfo<>(list);
        return new PageResult(pageInfo.getTotal(),pageInfo.getList());
    }

    @Override
    public List<Map<String, String>> selectOptionList() {
        return brandMapper.selectOptionList();

    }
}

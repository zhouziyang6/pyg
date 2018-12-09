package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/brand")
//@Controller
@RestController //组合了ResponseBody和Controller两个注解 对类中的所有方法生效  彻底的前后端分离
public class BrandController {

    //注入代理对象
    @Reference
    private BrandService brandService;
    /**
     * 查询品牌列表
     * @return 品牌列表json格式字符串
     */
    /*@RequestMapping(value = "/findAll",method = RequestMethod.GET)
    @ResponseBody*/
    @GetMapping("/findAll")
    public List<TbBrand> findAll(){

        return brandService.queryAll();
    }
}

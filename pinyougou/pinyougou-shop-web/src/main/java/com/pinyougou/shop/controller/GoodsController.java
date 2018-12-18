package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @RequestMapping("/findAll")
    public List<TbGoods> findAll(){
        return goodsService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        return goodsService.findPage(page, rows);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Goods goods){
        try {
            //设置商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未申请审核
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    @GetMapping("/findOne")
    public TbGoods findOne(Long id){
        return goodsService.findOne(id);
    }
    @PostMapping("/update")
    public Result update(@RequestBody TbGoods goods){
        try {
            goodsService.update(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            goodsService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }
    /**
     * 分页查询列表
     *
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods,
                             @RequestParam(value = "page",defaultValue = "1")Integer page,
                             @RequestParam(value = "rows",defaultValue = "10")Integer rows){
        return goodsService.search(goods,page,rows);
    }
}

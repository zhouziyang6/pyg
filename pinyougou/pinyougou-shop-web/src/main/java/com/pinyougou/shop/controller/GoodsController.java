package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import com.pinyougou.vo.Goods;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/goods")
@RestController
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    @RequestMapping("/findAll")
    public List<TbGoods> findAll() {
        return goodsService.findAll();
    }

    @RequestMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return goodsService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Goods goods) {
        try {
            //设置商家
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goods.getGoods().setAuditStatus("0");//未申请审核
            goods.getGoods().setIsMarketable("1");//默认上架,增加后审核通过就会上架,
            goodsService.addGoods(goods);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }

    @GetMapping("/findOne")
    public Goods findOne(Long id) {
        return goodsService.findGoodsById(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Goods goods) {
        try {
            //校验商家
            TbGoods oldGoods = goodsService.findOne(goods.getGoods().getId());
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!sellerId.equals(oldGoods.getSellerId()) || !sellerId.equals(goods.getGoods().getSellerId())) {
                return Result.fail("修改非法");
            }
            goodsService.updateGoods(goods);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            goodsService.deleteGoodsByIds(ids);//逻辑删除
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbGoods goods,
                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        //只能查询当前商家自己的商品
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(goods, page, rows);
    }

    /**
     * 批量更新商品的审核状态
     *
     * @param ids    商品spu id数组
     * @param status 要修改的状态
     * @return 操作结果
     */
    @GetMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return Result.ok("修改商品状态成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改商品状态失败");
    }

    //上架
    @GetMapping("/itemUpshelf")
    public Result itemUpshelf(Long[] ids) {
        try {
            /*for (Long id : ids) {
                Goods goods = goodsService.findGoodsById(id);
                if ("2".equals(goods.getGoods().getAuditStatus())) {
                    goods.getGoods().getId()
                }
            }*/

            List list = new ArrayList<>();
            for (Long id : ids) {
                Goods goods = goodsService.findGoodsById(id);
                /*if ("2".equals(goods.getGoods().getAuditStatus())) {
                }增加这个以后solr更新受影响,只需要增加商品时候默认是上架状态,,就算未审核也是上架状态,因为未审核的商品也无法上架*/
                    list.add(id);
            }
            ids = (Long[]) list.toArray(new Long[list.size()]);
            goodsService.itemUpshelf(ids);
            return Result.ok("上架成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上架失败");
    }

    //下架
    @GetMapping("/soldOut")
    public Result soldOut(Long[] ids) {
        try {
            goodsService.soldOut(ids);
            return Result.ok("下架成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("下架失败");
    }
}

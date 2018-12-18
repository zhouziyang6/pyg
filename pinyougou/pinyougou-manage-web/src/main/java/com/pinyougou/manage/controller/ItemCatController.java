package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/itemCat")
@RestController
public class ItemCatController {
    @Reference
    private ItemCatService itemCatService;
    @RequestMapping("/findAll")
    public List<TbItemCat> findAll(){
        return itemCatService.findAll();
    }
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                               @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return itemCatService.findPage(page,rows);
    }
    @PostMapping("/add")
    public Result add(@RequestBody TbItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    @GetMapping("findOne")
    public TbItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }
    @PostMapping("/update")
    public Result update(@RequestBody TbItemCat itemCat){
        try {
            itemCatService.update(itemCat);
            return Result.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("更新失败");
    }@PostMapping("/delete")
    public Result delete(Long[] ids){
        try {
            itemCatService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }
    //分页查询列表
    @PostMapping("/search")
    public PageResult search(@RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "rows",defaultValue = "10") Integer rows,
                             @RequestBody TbItemCat itemCat){
        return itemCatService.search(page,rows,itemCat);
    }
    //根据父分类id查询子分类列表
    @GetMapping("/findByParentId")
    public List<TbItemCat> findByParentId(Long parentId){
        TbItemCat itemCat = new TbItemCat();
        itemCat.setParentId(parentId);
        return itemCatService.findByWhere(itemCat);//通用Mapper, 根据条件查询对象
    }
}

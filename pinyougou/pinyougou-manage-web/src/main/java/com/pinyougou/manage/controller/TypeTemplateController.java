package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/typeTemplate")
@RestController
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;

    // 查找所有数据
    @RequestMapping
    public List<TbTypeTemplate> findAll(){
        return typeTemplateService.findAll();
    }
    //分页
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page",defaultValue = "1") Integer page,
                               @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return typeTemplateService.findPage(page,rows);
    }

    // 增加模板
    @PostMapping("/add")
    public Result add(@RequestBody TbTypeTemplate tbTypeTemplate){
        try {
            typeTemplateService.add(tbTypeTemplate);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    @GetMapping("/findOne")
    public TbTypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    // 修改模板
    @PostMapping("/update")
    public Result update(@RequestBody TbTypeTemplate tbTypeTemplate){
        try {
            typeTemplateService.update(tbTypeTemplate);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    // 批量删除模板
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    // 查询模板  分页查询模板列表
    @PostMapping("/search")
    public PageResult search(@RequestBody TbTypeTemplate tbTypeTemplate,
                             @RequestParam(value = "page",defaultValue = "1") Integer page,
                             @RequestParam(value = "rows",defaultValue = "10") Integer rows){
        return typeTemplateService.search(tbTypeTemplate,page,rows);
    }


}

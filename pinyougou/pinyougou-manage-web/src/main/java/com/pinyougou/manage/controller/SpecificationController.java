package com.pinyougou.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpacificationService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import com.pinyougou.vo.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/specification")
@RestController
public class SpecificationController {
    @Reference
    private SpacificationService spacificationService;
    @RequestMapping("/findAll")
    public List<TbSpecification> findAll(){
        return spacificationService.findAll();
    }
    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page" ,defaultValue = "1")Integer page,
                               @RequestParam(value = "rows" ,defaultValue = "10")Integer rows){
        return spacificationService.findPage(page,rows);
    }
    @PostMapping("/add")
    public Result add(@RequestBody Specification specification){
        try {
            spacificationService.add(specification);
            return Result.ok("增加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("增加失败");
    }
    @GetMapping("/findOne")
    public Specification findOne(Long id){
        return spacificationService.findOne(id);
    }
    @PostMapping("/update")
    public Result update(@RequestBody Specification specification){
        try {
            spacificationService.update(specification);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }
    @GetMapping("/delete")
    public Result delete(Long[] ids){
        try {
            spacificationService.deleteSpecificationByIds(ids);
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
    public PageResult search(@RequestParam(value = "page" ,defaultValue = "1")Integer page,
                             @RequestParam(value = "rows" ,defaultValue = "10")Integer rows,
                             @RequestBody TbSpecification specification){
        return spacificationService.search(page,rows,specification);
    }

    /**
     * 查询品牌数据 返回符合select2格式的数据
     */
    @GetMapping("/selectOptionList")
    public List<Map<String,String>> selectOptionList(){
        return spacificationService.selectOptionList();
    }
}

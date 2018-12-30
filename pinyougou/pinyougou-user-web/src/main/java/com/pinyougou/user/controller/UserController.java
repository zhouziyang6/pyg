package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.PhoneFormatCheckUtils;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.vo.PageResult;
import com.pinyougou.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.regex.PatternSyntaxException;

@RequestMapping("/user")
@RestController
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAll();
    }

    @GetMapping("/findPage")
    public PageResult findPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                               @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return userService.findPage(page, rows);
    }

    @PostMapping("/add")
    public Result add(@RequestBody TbUser user, String smsCode) {
        Result result = Result.fail("注册失败");
        try {
            if (PhoneFormatCheckUtils.isPhoneLegal(user.getPhone())) {
                //校验验证码 checkSmsCode() 参数为
                if (userService.checkSmsCode(user.getPhone(), smsCode)) {
                    user.setCreated(new Date());
                    user.setUpdated(user.getCreated());
                    user.setPassword(DigestUtils.md5Hex(user.getPassword()));
                    userService.add(user);
                    result = Result.ok("注册成功");

                } else {
                    result = Result.fail("验证码不正确!注册失败!");
                }
            } else {
                result = Result.fail("手机号码不正确!注册失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送验证码
     *
     * @param phone 要发送的手机号码
     * @return 操作结果
     */
    @GetMapping("/sendSmsCode")
    public Result sendSmsCode(String phone) {
        Result result = Result.fail("发送短信验证码失败.");
        try {
            //使用工具类判断输入的号码是否是一个正确的手机号码 PhoneFormatCheckUtils.isPhoneLegal(phone) 参数为手机号码
            if (PhoneFormatCheckUtils.isPhoneLegal(phone)) {
                userService.sendSmsCode(phone);
                result = Result.ok("发送验证码成功!");
            } else {
                result = Result.fail("手机号码格式错误! 发送验证码失败!");
            }
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }


    @GetMapping("/findOne")
    public TbUser findOne(Long id) {
        return userService.findOne(id);
    }

    @PostMapping("/update")
    public Result update(@RequestBody TbUser user) {
        try {
            userService.update(user);
            return Result.ok("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("修改失败");
    }

    @GetMapping("/delete")
    public Result delete(Long[] ids) {
        try {
            userService.deleteByIds(ids);
            return Result.ok("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("删除失败");
    }

    /**
     * 分页查询列表
     *
     * @param user 查询条件
     * @param page 页号
     * @param rows 每页大小
     * @return
     */
    @PostMapping("/search")
    public PageResult search(@RequestBody TbUser user, @RequestParam(value = "page", defaultValue = "1") Integer page,
                             @RequestParam(value = "rows", defaultValue = "10") Integer rows) {
        return userService.search(page, rows, user);
    }

}

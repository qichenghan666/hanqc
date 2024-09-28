package com.itheima.controller;

import ch.qos.logback.core.util.StringUtil;
import com.itheima.pojo.Result;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JwtUtil;
import com.itheima.utils.Md5Util;
import com.itheima.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        //查询用户
        User user = userService.findByUsername(username);
        //注册
        if (user == null) {
            //没有占用
            userService.register(username, password);
            return Result.success();
        } else {
            return Result.error("用户名已占用");
        }
    }

    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username,
                                @Pattern(regexp = "^\\S{5,16}$") String password) {
        //查询用户
        User user = userService.findByUsername(username);
        //登录
        String md5String = Md5Util.getMD5String(password);
        if (user != null && user.getPassword().equals(md5String)) {
            //登录成功
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("username", user.getUsername());
            String token = JwtUtil.genToken(claims);
            return Result.success(token);
        } else {
            return Result.error("用户名或密码错误");
        }
    }
    // 普通方法4
//@GetMapping("/userInfo")
//    public Result<User> userInfo(@RequestHeader(name = "Authorization") String token){
//        Map<String, Object> claims = JwtUtil.parseToken(token);
//        String username = (String) claims.get("username");
//        User user = userService.findByUsername(username);
//        return Result.success(user);
//    }
@GetMapping("/userInfo")
    public Result<User> userInfo(){
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");
        User user = userService.findByUsername(username);
        return Result.success(user);
    }

    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    @PatchMapping("/updatePwd")
    public Result updateaPwd(@RequestBody Map<String,String> params){
        // 校验参数
       String oldPwd = params.get("old_pwd");
       String newPwd = params.get("new_pwd");
       String rePwd = params.get("re_pwd");
       if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){
           return Result.error("参数错误");
       }
       //原密码是否正确
        Map<String, Object> claims = ThreadLocalUtil.get();
        String username = (String) claims.get("username");
        User loginUser = userService.findByUsername(username);
        String md5String = Md5Util.getMD5String(oldPwd);
        if(!loginUser.getPassword().equals(md5String)){
            return Result.error("原密码错误");
        }
        //新密码是否一致
        if(!newPwd.equals(rePwd)){
            return Result.error("两次密码不一致");
        }
        userService.updatePwd(newPwd);
        return Result.success();
    }
}

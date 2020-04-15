package com.duofei.user.controller;

import com.duofei.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户服务提供类
 * @author duofei
 * @date 2020/4/14
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/pay/{userName}")
    public void userPay(@PathVariable String userName, @RequestParam Float total){
        userService.userPay(userName, total);
    }
}

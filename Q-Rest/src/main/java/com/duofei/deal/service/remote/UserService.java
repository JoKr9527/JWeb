package com.duofei.deal.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户服务
 * @author duofei
 * @date 2020/5/8
 */
@FeignClient("Q-USER")
@RequestMapping("/user")
public interface UserService {

    @GetMapping("/pay/{userName}")
    void userPay(@PathVariable("userName") String userName, @RequestParam("total") Float total);
}

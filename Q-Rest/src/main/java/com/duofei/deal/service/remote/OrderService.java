package com.duofei.deal.service.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 订单服务
 * @author duofei
 * @date 2020/5/8
 */
@FeignClient("Q-ORDER")
@RequestMapping("/order")
public interface OrderService {

    @GetMapping("/createOrder/{userName}/{goodsId}")
    void createOrder(@PathVariable("userName") String userName, @PathVariable("goodsId") String goodsId, @RequestParam("num") Integer num, @RequestParam("total") Float total);
}

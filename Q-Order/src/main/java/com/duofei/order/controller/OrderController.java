package com.duofei.order.controller;

import com.duofei.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单服务提供类
 * @author duofei
 * @date 2020/4/14
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/createOrder/{userName}/{goodsId}")
    public void createOrder(@PathVariable String userName, @PathVariable String goodsId, @RequestParam Integer num, @RequestParam Float total){
        orderService.createOrder(userName, goodsId, num, total);
    }
}

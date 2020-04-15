package com.duofei.deal.controller;

import com.duofei.deal.service.DealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 交易模块接口
 * @author duofei
 * @date 2020/4/14
 */
@RestController
@RequestMapping("/deal")
public class DealController {

    @Autowired
    private DealService dealService;

    @GetMapping("/buy/{userName}/{goodsId}")
    public void buy(@PathVariable String userName, @PathVariable String goodsId, @RequestParam Integer num) {
        dealService.buy(userName, goodsId, num);
    }

}

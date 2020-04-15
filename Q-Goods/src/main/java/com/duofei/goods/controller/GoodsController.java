package com.duofei.goods.controller;

import com.duofei.goods.bean.GoodsInfo;
import com.duofei.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品服务提供类
 * @author duofei
 * @date 2020/4/14
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("/query/{id}")
    public GoodsInfo queryGoodsInfo(@PathVariable String id){
        return goodsService.queryGoodsInfo(id);
    }

    @GetMapping("/reduce/{goodsId}")
    public void goodsReduce(@PathVariable String goodsId, @RequestParam Integer num){
        goodsService.goodsReduce(goodsId, num);
    }

}

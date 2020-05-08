package com.duofei.deal.service.remote;

import com.duofei.deal.bean.GoodsInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品服务
 * @author duofei
 * @date 2020/5/8
 */
@FeignClient("Q-GOODS")
@RequestMapping("/goods")
public interface GoodsService {

    @GetMapping("/reduce/{goodsId}")
    void goodsReduce(@PathVariable("goodsId") String goodsId, @RequestParam("num") Integer num);

    @GetMapping("/query/{id}")
    GoodsInfo queryGoodsInfo(@PathVariable("id") String id);
}

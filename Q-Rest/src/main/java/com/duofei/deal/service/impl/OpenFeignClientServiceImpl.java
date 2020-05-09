package com.duofei.deal.service.impl;

import com.duofei.deal.bean.GoodsInfo;
import com.duofei.deal.service.ProviderService;
import com.duofei.deal.service.remote.GoodsService;
import com.duofei.deal.service.remote.OrderService;
import com.duofei.deal.service.remote.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 使用 open feign 实现远程调用
 * @author duofei
 * @date 2020/5/8
 */
@Service
public class OpenFeignClientServiceImpl implements ProviderService {

    @Resource
    private GoodsService goodsService;
    @Resource
    private OrderService orderService;
    @Resource
    private UserService userService;


    @Override
    public GoodsInfo queryGoodsInfo(String id) throws Exception {
        return goodsService.queryGoodsInfo(id);
    }

    @Override
    public void createOrder(String userName, String goodsId, Integer num, Float total) throws Exception {
        orderService.createOrder(userName, goodsId, num, total);
    }

    @Override
    public void userPay(String userName, Float total) throws Exception {
        userService.userPay(userName, total);
    }

    @Override
    public void goodsReduce(String goodsId, Integer num) throws Exception {
        goodsService.goodsReduce(goodsId, num);
    }
}

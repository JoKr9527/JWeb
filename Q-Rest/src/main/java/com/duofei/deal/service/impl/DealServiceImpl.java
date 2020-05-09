package com.duofei.deal.service.impl;

import com.duofei.deal.bean.GoodsInfo;
import com.duofei.deal.service.DealService;
import com.duofei.deal.service.ProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 交易业务逻辑实现类
 * @author duofei
 * @date 2020/4/14
 */
@Service
public class DealServiceImpl implements DealService {

    @Autowired
    @Qualifier("openFeignClientServiceImpl")
    private ProviderService providerService;

    @Override
    public void buy(String userName, String goodsId, Integer num) throws Exception {
        // 获取商品价格
        GoodsInfo goodsInfo = providerService.queryGoodsInfo(goodsId);
        // 生成订单
        Float total = goodsInfo.getPrice() * num;
        providerService.createOrder(userName, goodsId, num, total);
        // 用户支付成功
        providerService.userPay(userName, total);
        // 商品数量减少
        providerService.goodsReduce(goodsId, num);
    }
}

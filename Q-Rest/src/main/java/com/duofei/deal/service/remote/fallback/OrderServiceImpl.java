package com.duofei.deal.service.remote.fallback;

import com.duofei.deal.service.remote.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 订单服务备用方法实现
 * @author duofei
 * @date 2020/5/8
 */
@Component
public class OrderServiceImpl implements OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public void createOrder(String userName, String goodsId, Integer num, Float total) throws Exception{
        logger.warn("订单服务忙！请稍后重试");
    }
}

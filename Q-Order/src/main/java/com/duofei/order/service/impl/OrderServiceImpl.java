package com.duofei.order.service.impl;

import com.duofei.order.dao.OrderDao;
import com.duofei.order.entity.Order;
import com.duofei.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * 订单服务业务逻辑实现类
 * @author duofei
 * @date 2020/4/14
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public void createOrder(String userName, String goodsId, Integer num, Float total) {
        Order order = new Order();
        order.setNum(num);
        order.setTotal(total);
        order.setGoodsId(goodsId);
        order.setUserName(userName);
        order.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        orderDao.save(order);
    }
}

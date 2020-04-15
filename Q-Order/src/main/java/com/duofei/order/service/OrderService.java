package com.duofei.order.service;

/**
 * 订单服务业务逻辑方法声明接口
 * @author duofei
 * @date 2020/4/14
 */
public interface OrderService {

    /**
     * 订单生成
     * @author duofei
     * @date 2020/4/14
     * @param userName 用户名称
     * @param goodsId 商品Id
     * @param num 购买数量
     * @param total 总价
     */
    void createOrder(String userName, String goodsId, Integer num, Float total);
}

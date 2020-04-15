package com.duofei.deal.service;

/**
 * 交易模块业务逻辑方法声明接口类
 * @author duofei
 * @date 2020/4/14
 */
public interface DealService {

    /**
     * 用户购买商品
     * @author duofei
     * @date 2020/4/14
     * @param userName 用户名
     * @param goodsId 商品id
     * @param num 购买数量
     */
    void buy(String userName, String goodsId, Integer num);
}

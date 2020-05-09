package com.duofei.deal.service;

import com.duofei.deal.bean.GoodsInfo;

/**
 * 用于声明其它服务，封装具体的服务调用过程
 * @author duofei
 * @date 2020/4/14
 */
public interface ProviderService {

    /**
     * 获取商品信息
     * @author duofei
     * @date 2020/4/14
     * @param id 商品id
     * @return GoodsInfo 商品信息
     */
    GoodsInfo queryGoodsInfo(String id) throws Exception;

    /**
     * 订单生成
     * @author duofei
     * @date 2020/4/14
     * @param userName 用户名称
     * @param goodsId 商品Id
     * @param num 购买数量
     * @param total 总价
     */
    void createOrder(String userName, String goodsId, Integer num, Float total) throws Exception;

    /**
     * 用户支付
     * @author duofei
     * @date 2020/4/14
     * @param userName 用户名称
     * @param total 总价
     */
    void userPay(String userName, Float total) throws Exception;

    /**
     * 扣除相应商品数
     * @author duofei
     * @date 2020/4/14
     * @param goodsId 商品id
     * @param num 需要减除的商品数
     */
    void goodsReduce(String goodsId, Integer num) throws Exception;

}

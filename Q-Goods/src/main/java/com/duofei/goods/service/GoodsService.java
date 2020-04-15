package com.duofei.goods.service;

import com.duofei.goods.bean.GoodsInfo;

/**
 * 商品服务业务逻辑方法声明接口
 * @author duofei
 * @date 2020/4/14
 */
public interface GoodsService {

    /**
     * 获取商品信息
     * @author duofei
     * @date 2020/4/14
     * @param id 商品id
     * @return GoodsInfo 商品信息
     */
    GoodsInfo queryGoodsInfo(String id);

    /**
     * 扣除相应商品数
     * @author duofei
     * @date 2020/4/14
     * @param goodsId 商品id
     * @param num 需要减除的商品数
     */
    void goodsReduce(String goodsId, Integer num);
}

package com.duofei.goods.dao;

/**
 * 拓展 goodsDao
 * @author duofei
 * @date 2020/4/15
 */
public interface ExpandGoodsDao {

    /**
     * 减少商品数量
     * @author duofei
     * @date 2020/4/15
     * @param id  主键id
     * @param num 减少的数量
     * @return int 影响的商品数
     */
    int goodsReduce(String id, Integer num);
}

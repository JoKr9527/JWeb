package com.duofei.goods.service.impl;

import com.duofei.goods.bean.GoodsInfo;
import com.duofei.goods.dao.GoodsDao;
import com.duofei.goods.service.GoodsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品服务业务逻辑实现类
 * @author duofei
 * @date 2020/4/14
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public GoodsInfo queryGoodsInfo(String id) {
        return goodsDao.findById(id).map(goods -> {
            GoodsInfo goodsInfo = new GoodsInfo();
            BeanUtils.copyProperties(goods, goodsInfo);
            return goodsInfo;
        }).orElse(null);
    }

    @Override
    public void goodsReduce(String goodsId, Integer num) {
        goodsDao.goodsReduce(goodsId, num);
    }
}

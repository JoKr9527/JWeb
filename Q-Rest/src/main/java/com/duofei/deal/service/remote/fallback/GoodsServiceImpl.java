package com.duofei.deal.service.remote.fallback;

import com.duofei.deal.bean.GoodsInfo;
import com.duofei.deal.service.remote.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 商品服务备用方法实现
 * @author duofei
 * @date 2020/5/8
 */
@Component
public class GoodsServiceImpl implements GoodsService {

    private static Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Override
    public void goodsReduce(String goodsId, Integer num) throws Exception{
        logger.warn("商品服务忙！稍后重试");
    }

    @Override
    public GoodsInfo queryGoodsInfo(String id) throws Exception{
        logger.warn("商品服务忙！稍后重试");
        return null;
    }
}

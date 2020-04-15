package com.duofei.goods.dao.impl;

import com.duofei.goods.dao.ExpandGoodsDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 拓展商品 Dao 实现类
 * @author duofei
 * @date 2020/4/15
 */
@Repository("expandGoodsDao")
public class ExpandGoodsDaoImpl implements ExpandGoodsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int goodsReduce(String id, Integer num) {
        return jdbcTemplate.update("UPDATE q_goods SET num = num - ? WHERE id = ?", num, id);
    }
}

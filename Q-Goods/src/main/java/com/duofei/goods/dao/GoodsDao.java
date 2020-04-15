package com.duofei.goods.dao;

import com.duofei.goods.entity.Goods;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 商品Dao
 * @author duofei
 * @date 2020/4/14
 */
public interface GoodsDao extends JpaRepository<Goods, String>, ExpandGoodsDao {
}

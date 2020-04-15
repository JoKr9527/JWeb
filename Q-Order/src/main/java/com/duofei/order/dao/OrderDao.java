package com.duofei.order.dao;

import com.duofei.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 订单 Dao
 * @author duofei
 * @date 2020/4/15
 */
public interface OrderDao extends JpaRepository<Order, String> {
}

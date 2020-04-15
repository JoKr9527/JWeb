package com.duofei.order.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 订单实体类
 * @author duofei
 * @date 2020/4/15
 */
@Entity(name = "q_order")
public class Order {

    @Id
    private String id;

    private String userName;

    private String goodsId;

    private Integer num;

    private Float total;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Float getTotal() {
        return total;
    }

    public void setTotal(Float total) {
        this.total = total;
    }
}

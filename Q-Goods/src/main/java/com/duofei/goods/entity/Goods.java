package com.duofei.goods.entity;

import org.springframework.context.annotation.Primary;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 商品实体类
 * @author duofei
 * @date 2020/4/14
 */
@Entity(name = "q_goods")
public class Goods {

    @Id
    private String id;

    private String name;

    private Float price;

    private Integer num;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}

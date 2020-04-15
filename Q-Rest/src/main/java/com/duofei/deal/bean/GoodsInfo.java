package com.duofei.deal.bean;

/**
 * 商品信息
 * @author duofei
 * @date 2020/4/14
 */
public class GoodsInfo {

    private String id;

    private String name;

    private Float price;

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
}

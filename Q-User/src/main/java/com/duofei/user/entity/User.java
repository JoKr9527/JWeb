package com.duofei.user.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 用户实体类
 * @author duofei
 * @date 2020/4/15
 */
@Entity(name = "q_user")
public class User {

    @Id
    private String id;

    private String userName;
    /**
     * 余额
     */
    private Float balance;

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

    public Float getBalance() {
        return balance;
    }

    public void setBalance(Float balance) {
        this.balance = balance;
    }
}

package com.duofei.user.dao;

/**
 * 拓展用户 Dao
 * @author duofei
 * @date 2020/4/15
 */
public interface ExpandUserDao {

    /**
     * 用户余额减少
     * @author duofei
     * @date 2020/4/15
     * @param userName 用户名
     * @param total 减去的金额
     * @return int 影响的用户数
     */
    int userReduce(String userName, Float total);
}

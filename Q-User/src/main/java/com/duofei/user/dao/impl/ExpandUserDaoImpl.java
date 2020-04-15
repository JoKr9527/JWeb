package com.duofei.user.dao.impl;

import com.duofei.user.dao.ExpandUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 拓展用户Dao实现类
 * @author duofei
 * @date 2020/4/15
 */
@Repository("expandUserDao")
public class ExpandUserDaoImpl implements ExpandUserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int userReduce(String userName, Float total) {
        return jdbcTemplate.update("UPDATE q_user SET balance = balance - ? WHERE balance >= ? AND userName = ?",
                total, total, userName);
    }
}

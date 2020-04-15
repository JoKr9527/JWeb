package com.duofei.user.dao;

import com.duofei.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 用户 Dao
 * @author duofei
 * @date 2020/4/15
 */
public interface UserDao extends JpaRepository<User, String>, ExpandUserDao {
}

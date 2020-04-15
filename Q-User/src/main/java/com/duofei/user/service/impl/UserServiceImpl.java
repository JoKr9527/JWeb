package com.duofei.user.service.impl;

import com.duofei.user.dao.UserDao;
import com.duofei.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户服务业务逻辑实现类
 * @author duofei
 * @date 2020/4/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public void userPay(String userName, Float total) {
        userDao.userReduce(userName, total);
    }
}

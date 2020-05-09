package com.duofei.deal.service.remote.fallback;

import com.duofei.deal.service.remote.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户服务备用方法实现
 * @author duofei
 * @date 2020/5/8
 */
@Component
public class UserServiceImpl implements UserService {

    private static Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void userPay(String userName, Float total) throws Exception {
        logger.warn("用户服务忙！稍后重试");
    }
}

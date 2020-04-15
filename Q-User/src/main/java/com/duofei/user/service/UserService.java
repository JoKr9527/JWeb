package com.duofei.user.service;

/**
 * 用户服务业务逻辑方法声明接口
 * @author duofei
 * @date 2020/4/14
 */
public interface UserService {

    /**
     * 用户支付
     * @author duofei
     * @date 2020/4/14
     * @param userName 用户名称
     * @param total 总价
     */
    void userPay(String userName, Float total);

}

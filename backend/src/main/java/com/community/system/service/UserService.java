package com.community.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.community.system.entity.User;
import java.util.Map;

public interface UserService extends IService<User> {
    
    /**
     * 发送短信验证码 (模拟)
     */
    void sendSmsCode(String phone);

    /**
     * 手机号+验证码 登录/注册
     * @return JWT Token
     */
    Map<String, Object> loginOrRegister(String phone, String code);

    /**
     * 获取当前用户信息
     */
    User getCurrentUserInfo(Long userId);
    
    /**
     * 绑定社区
     */
    void bindCommunity(Long userId, Long communityId);
    
    /**
     * 获取用户积分余额
     */
    Integer getUserPointBalance(Long userId);
}

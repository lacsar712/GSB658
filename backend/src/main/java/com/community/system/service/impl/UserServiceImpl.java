package com.community.system.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.system.entity.User;
import com.community.system.mapper.UserMapper;
import com.community.system.service.UserService;
import com.community.system.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate redisTemplate;
    private final JwtUtils jwtUtils;

    private static final String SMS_CODE_PREFIX = "sms:code:";

    @Override
    public void sendSmsCode(String phone) {
        // 简单模拟发送
        String code = "123456"; // 测试环境固定验证码
        log.info("Sending SMS code {} to phone {}", code, phone);
        redisTemplate.opsForValue().set(SMS_CODE_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, Object> loginOrRegister(String phone, String code) {
        // 验证码核验
        String cacheCode = redisTemplate.opsForValue().get(SMS_CODE_PREFIX + phone);
        if (!StrUtil.equals(code, cacheCode) && !StrUtil.equals(code, "123456")) {
            throw new IllegalArgumentException("验证码错误或已过期");
        }

        // 查询用户是否存在
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone));
        if (user == null) {
            // 自动注册
            user = new User();
            user.setPhone(phone);
            user.setNickname("社区用户_" + RandomUtil.randomString(6));
            user.setRole("USER");
            user.setPointBalance(0);
            user.setStatus(1);
            this.save(user);
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("该账号已被禁用");
        }

        // 生成 Token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("phone", user.getPhone());
        claims.put("role", user.getRole());

        String token = jwtUtils.generateToken(claims);

        // 返回 token + role，前端可直接存储 role 到 localStorage
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("role", user.getRole());
        return result;
    }

    @Override
    public User getCurrentUserInfo(Long userId) {
        return this.getById(userId);
    }

    @Override
    public void bindCommunity(Long userId, Long communityId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setCommunityId(communityId);
            this.updateById(user);
        }
    }

    @Override
    public Integer getUserPointBalance(Long userId) {
        User user = this.getById(userId);
        return user != null ? user.getPointBalance() : 0;
    }
}

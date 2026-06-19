package com.community.system.controller;

import com.community.system.service.UserService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/sms-code")
    public Result<?> sendCode(@RequestBody Map<String, String> body) {
        userService.sendSmsCode(body.get("phone"));
        return Result.success("验证码发送成功(模拟环境请查阅控制台或使用123456)");
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        Map<String, Object> loginData = userService.loginOrRegister(body.get("phone"), body.get("code"));
        return Result.success(loginData);
    }
}

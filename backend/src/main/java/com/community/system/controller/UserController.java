package com.community.system.controller;

import com.community.system.entity.User;
import com.community.system.service.UserService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = Long.valueOf(request.getAttribute("userId").toString());
        User user = userService.getCurrentUserInfo(userId);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        // 清理敏感信息（如果有的话，目前User类无密码字段）
        return Result.success(user);
    }
}

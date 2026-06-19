package com.community.system.controller;

import com.community.system.service.ThirdPartyService;
import com.community.system.vo.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIChatController {

    private final ThirdPartyService thirdPartyService;

    @PostMapping("/query")
    public Result<String> chatWithAi(@RequestBody Map<String, String> body) {
        String query = body.get("text");
        if (query == null || query.trim().isEmpty()) {
            return Result.error(400, "提问不能为空");
        }
        
        String reply = thirdPartyService.baiduUnitChat(query);
        return Result.success(reply);
    }
}

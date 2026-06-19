package com.community.system.service.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.community.system.service.ThirdPartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Value("${thirdparty.amap.key}")
    private String amapKey;

    @Value("${thirdparty.baidu.client-id}")
    private String baiduApiKey;

    @Value("${thirdparty.baidu.client-secret}")
    private String baiduSecretKey;

    private String baiduAccessToken = null;
    private long baiduTokenExpireTime = 0;

    @Override
    public String amapRegeo(String longitude, String latitude) {
        String url = "https://restapi.amap.com/v3/geocode/regeo?key=" + amapKey + "&location=" + longitude + "," + latitude;
        try {
            String res = HttpUtil.get(url);
            JSONObject json = JSON.parseObject(res);
            if ("1".equals(json.getString("status"))) {
                return json.getJSONObject("regeocode").getString("formatted_address");
            }
        } catch (Exception e) {
            log.error("高德逆地理编码解析异常", e);
        }
        return "未知定位地址";
    }

    @Override
    public String baiduUnitChat(String text) {
        // 简单封装：先获取 Access Token (实际项目应加锁或提前拉取并放入Redis)
        String token = getBaiduAccessToken();
        if (token == null) return "AI服务暂时不可用,请稍后重试";

        // UNIT API V3 (假设使用通用对话, 需在百度云控制台创建技能并获取 service_id)
        String serviceId = "S70000"; // 模拟
        String url = "https://aip.baidubce.com/rpc/2.0/unit/service/v3/chat?access_token=" + token;

        Map<String, Object> request = new HashMap<>();
        request.put("version", "3.0");
        request.put("service_id", serviceId);
        request.put("log_id", System.currentTimeMillis() + "");
        request.put("session_id", "");
        
        Map<String, Object> reqRequest = new HashMap<>();
        reqRequest.put("query", text);
        request.put("request", reqRequest);

        try {
            String res = HttpUtil.post(url, JSON.toJSONString(request));
            JSONObject json = JSON.parseObject(res);
            if (json.getIntValue("error_code") == 0) {
                // 提取返回的第一条reply
                return json.getJSONObject("result").getJSONArray("context").getJSONObject(0)
                        .getJSONArray("system_action").getJSONObject(0).getString("reply");
            }
        } catch (Exception e) {
            log.error("百度UNIT对话接口异常", e);
        }
        return "抱歉，我现在脑子有点乱，没听懂您的话。";
    }

    private synchronized String getBaiduAccessToken() {
        if (System.currentTimeMillis() < baiduTokenExpireTime && baiduAccessToken != null) {
            return baiduAccessToken;
        }
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" 
                + baiduApiKey + "&client_secret=" + baiduSecretKey;
        try {
            String res = HttpUtil.get(url);
            JSONObject json = JSON.parseObject(res);
            baiduAccessToken = json.getString("access_token");
            baiduTokenExpireTime = System.currentTimeMillis() + (json.getLongValue("expires_in") * 1000) - 30000;
        } catch (Exception e) {
            log.error("获取百度AccessToken失败", e);
        }
        return baiduAccessToken;
    }
}

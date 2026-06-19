package com.community.system.service;

public interface ThirdPartyService {
    
    /**
     * 高德地图: 根据经纬度解析成详细的可读地址
     */
    String amapRegeo(String longitude, String latitude);
    
    /**
     * 百度 UNIT: 发送自然语言咨询,得到机器人多轮回复
     */
    String baiduUnitChat(String text);
}

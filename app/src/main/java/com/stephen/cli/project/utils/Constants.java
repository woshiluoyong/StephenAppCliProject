package com.stephen.cli.project.utils;

public class Constants {
    public static final String ServerProtocol = "https://";
    public static final String DefaultIpPort = ServerProtocol + "api.apiopen.top";
    public static final String DefaultServer = DefaultIpPort + "";
    public static final String DefaultWsServer = "ws://123.207.167.163:9010/ajaxchattest";

    public static final String ShareLinkUrl = "http://invite.bifenkk.com/share.html";
    public static final String ShareDescription = "球场上的运算专家、强大的赛事筛选功能、提供足球赛事实时精准比分赛况！";
    public static final String TestVersionStr = "测试版-";

    public static final String Flag_UseToken = "Flag_UseToken", Flag_SelfPageNum = "Flag_SelfPageNum";//api需要用户token标识,自定义分页标识
    public static final String Key_UserInfo = "Key_UserInfo", Key_ConfigInfo = "Key_ConfigInfo";

    public static final int SuccessCode = 200, LoginOutCode = 405;//请求成功码/登录超时码
    public static final int Req_EnterLoginPage = 100, Req_EnterMatchFilter = 101, Req_EnterMatchSelect = 102,
            Req_EnterUserInfo = 103, Req_EnterVipLevel = 104, Req_EnterUserSetting = 105;
}

package com.stephen.cli.project.bean;

/**
 * Created by stephen on 13/08/2019.
 */

public class ResUserInfoBean extends ResBaseBean{
    private Data body;

    public Data getBody() {
        return body;
    }

    public class Data{
        private String adminId;
        private String adminAccount;
        private String userId;
        private String userMblNo;
        private String userTrueName;
        private String token;
        private String clientFlag;
        private long id;
        private String mblNo;
        private String trueName;//真实姓名
        private String headerIconUrl;
        private long createTime;
        private int state;
        private String pushIdenti;//推送用户标识
        private String note;
        private String inviteCustomerId;
        private String inviteCustomerMblNo;
        private String inviteCode;
        private int isVip;//1是vip 0不是
        private long vipEndTime;
        private int matchOrderSetup;//比赛顺序设置1.按联赛2.按开始时间
        private int openVoice;//开启提醒声音 0.否 1.是
        private int openPop;//开启提醒弹窗 0.否 1.是
        private int openShock;//开启提醒震动 0.否 1.是
        private int openTipColMatch;//开启仅提示关注比赛 0.否 1.是
        private int openPushColMatch;//开启推送关注比赛 0.否 1.是
        private int openPushStrong;//开启强主队推送 0.否 1.是
        private int pushPushCustom;//开启自选赛事推送 0.否 1.是
        private String pushType;//推送类型
        private int mblSysType;//手机系统类型 0.android 1.ios
        private String mblSysVersionNo;//系统版本号
        private String mblModel;//手机型号
        private String mblBrandName;//手机品牌名称
        private String romName;//rom名称
        private String romVersionNo;//rom版本号

        public String getAdminId() {
            return adminId;
        }

        public String getAdminAccount() {
            return adminAccount;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserMblNo() {
            return userMblNo;
        }

        public String getUserTrueName() {
            return userTrueName;
        }

        public String getToken() {
            return token;
        }

        public String getClientFlag() {
            return clientFlag;
        }

        public long getId() {
            return id;
        }

        public String getMblNo() {
            return mblNo;
        }

        public String getTrueName() {
            return trueName;
        }

        public String getHeaderIconUrl() {
            return headerIconUrl;
        }

        public long getCreateTime() {
            return createTime;
        }

        public int getState() {
            return state;
        }

        public String getPushIdenti() {
            return pushIdenti;
        }

        public String getNote() {
            return note;
        }

        public String getInviteCustomerId() {
            return inviteCustomerId;
        }

        public String getInviteCustomerMblNo() {
            return inviteCustomerMblNo;
        }

        public String getInviteCode() {
            return inviteCode;
        }

        public int getIsVip() {
            return isVip;
        }

        public long getVipEndTime() {
            return vipEndTime;
        }

        public int getMatchOrderSetup() {
            return matchOrderSetup;
        }

        public int getOpenVoice() {
            return openVoice;
        }

        public int getOpenPop() {
            return openPop;
        }

        public int getOpenShock() {
            return openShock;
        }

        public int getOpenTipColMatch() {
            return openTipColMatch;
        }

        public int getOpenPushColMatch() {
            return openPushColMatch;
        }

        public int getOpenPushStrong() {
            return openPushStrong;
        }

        public int getPushPushCustom() {
            return pushPushCustom;
        }

        public String getPushType() {
            return pushType;
        }

        public int getMblSysType() {
            return mblSysType;
        }

        public String getMblSysVersionNo() {
            return mblSysVersionNo;
        }

        public String getMblModel() {
            return mblModel;
        }

        public String getMblBrandName() {
            return mblBrandName;
        }

        public String getRomName() {
            return romName;
        }

        public String getRomVersionNo() {
            return romVersionNo;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setHeaderIconUrl(String headerIconUrl) {
            this.headerIconUrl = headerIconUrl;
        }
    }
}

package com.stephen.cli.project.bean;

/**
 * Created by stephen on 13/08/2019.
 */

public class ResConfigBean extends ResBaseBean{
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
        private String messageSign;
        private String vipPrivilege;
        private String boutiqueCompetPushExplain;
        private String optionalCompetPushExplain;
        private String aboutUs;
        private String tou;
        private String disclaimer;
        private String workTime;
        private String serviceTel;
        private String serviceQQ;
        private String serviceWX;
        private String boutiqueCompetRuleExplain;
        private String optionalCompetRuleExplain;
        private int inviteGiveVipDay;
        private String vipExpireRemind;
        private String isOpenIosAudit;
        private String isOpenVip;

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

        public String getMessageSign() {
            return messageSign;
        }

        public String getVipPrivilege() {
            return vipPrivilege;
        }

        public String getBoutiqueCompetPushExplain() {
            return boutiqueCompetPushExplain;
        }

        public String getOptionalCompetPushExplain() {
            return optionalCompetPushExplain;
        }

        public String getAboutUs() {
            return aboutUs;
        }

        public String getTou() {
            return tou;
        }

        public String getDisclaimer() {
            return disclaimer;
        }

        public String getWorkTime() {
            return workTime;
        }

        public String getServiceTel() {
            return serviceTel;
        }

        public String getServiceQQ() {
            return serviceQQ;
        }

        public String getServiceWX() {
            return serviceWX;
        }

        public String getBoutiqueCompetRuleExplain() {
            return boutiqueCompetRuleExplain;
        }

        public String getOptionalCompetRuleExplain() {
            return optionalCompetRuleExplain;
        }

        public int getInviteGiveVipDay() {
            return inviteGiveVipDay;
        }

        public String getVipExpireRemind() {
            return vipExpireRemind;
        }

        public String getIsOpenIosAudit() {
            return isOpenIosAudit;
        }

        public String getIsOpenVip() {
            return isOpenVip;
        }
    }
}

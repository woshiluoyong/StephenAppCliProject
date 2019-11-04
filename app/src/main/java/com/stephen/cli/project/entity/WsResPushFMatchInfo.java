package com.stephen.cli.project.entity;

import java.io.Serializable;

public class WsResPushFMatchInfo extends WsResBaseBean implements Serializable {
    private Data body;

    public Data getBody() {
        return body;
    }

    public class Data {
        private long matchId;//一场比赛的 ID
        private long fMatchCompetitionId;//赛事id
        private String hName;//比赛主队名称
        private String aName;//比赛客队名称
        private int hScore;//比赛主队得分
        private int aScore;//比赛客队得分
        private int position;//0-中立 1,主队 2,客队
        private int time;//事件发生的时间(秒)
        private int progressTime;//进行的时间(分)

        private int hRedCardCnt;//比赛主队红牌数
        private int aRedCardCnt;//比赛客队红牌数

        private long elapsedTime;//比赛进行的时间
        private long teeTime;//开球时间
        private int status;//比赛状态 0.未开赛 1.进行中 2.已结束 3.延迟
        private int detailedStatus;//比赛的详细状态(0.比赛异常 1.未开赛 2.上半场 3.中场 4.下半场 5.加时赛 6.加时赛(弃用)7.点球 决战 8.完场 9.推迟 10.中断 11.腰斩 12.取消 13.待定)
        private int hCornerBall;//主队角球数
        private int hRedCard;//主队红牌数
        private int hYellowCard;//主队黄牌数
        private int aCornerBall;//客队角球数
        private int aRedCard;//客队红牌数
        private int aYellowCard;//客队黄牌数
        private float asiaNewestOdds;//亚盘的即盘盘口
        private float bsNewestOdds;//大小球的即盘盘口
        private float euNewestOdds;//欧赔的即盘盘口

        private int main;//是否重要事件
        private String data;//直播内容
        private int matchType;//比赛类型
        private int type;//事件类型(1.进球 2.角球 3.黄牌 4.红牌 5.界外球 6.任意球 7.球门球 8.点球 9.换人 10.比赛 开始 11.中场 12.结束 13.半场比分 15.两黄变红 16.点球未进 17.乌龙球 19.伤 停补时 21.射正 22.射偏 23.进攻 24.危险进攻 25.控球率 26.加时赛结束 27.点 球大战结束)

        public long getMatchId() {
            return matchId;
        }

        public long getfMatchCompetitionId() {
            return fMatchCompetitionId;
        }

        public String gethName() {
            return hName;
        }

        public String getaName() {
            return aName;
        }

        public int gethScore() {
            return hScore;
        }

        public int getaScore() {
            return aScore;
        }

        public int getPosition() {
            return position;
        }

        public int getTime() {
            return time;
        }

        public int getProgressTime() {
            return progressTime;
        }

        public int gethRedCardCnt() {
            return hRedCardCnt;
        }

        public int getaRedCardCnt() {
            return aRedCardCnt;
        }

        public int getStatus() {
            return status;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public long getTeeTime() {
            return teeTime;
        }

        public int getDetailedStatus() {
            return detailedStatus;
        }

        public int gethCornerBall() {
            return hCornerBall;
        }

        public int gethRedCard() {
            return hRedCard;
        }

        public int gethYellowCard() {
            return hYellowCard;
        }

        public int getaCornerBall() {
            return aCornerBall;
        }

        public int getaRedCard() {
            return aRedCard;
        }

        public int getaYellowCard() {
            return aYellowCard;
        }

        public float getAsiaNewestOdds() {
            return asiaNewestOdds;
        }

        public float getBsNewestOdds() {
            return bsNewestOdds;
        }

        public float getEuNewestOdds() {
            return euNewestOdds;
        }
        
        public int getMain() {
            return main;
        }

        public String getData() {
            return data;
        }

        public int getMatchType() {
            return matchType;
        }

        public int getType() {
            return type;
        }
    }
}

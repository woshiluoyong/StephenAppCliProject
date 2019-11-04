package com.stephen.cli.project.scoket;

import android.text.TextUtils;
import android.view.View;

import com.stephen.cli.project.activity.MainActivity;
import com.stephen.cli.project.base.BaseLocalActivity;
import com.stephen.cli.project.base.BaseLocalFragment;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.entity.WsReqLoginBean;
import com.stephen.cli.project.entity.WsResBaseBean;
import com.stephen.cli.project.entity.WsResPushFMatchInfo;
import com.stephen.cli.project.library.BaseActivity;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.Constants;
import com.stephen.cli.project.utils.StephenUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.ByteString;

public class JWebSocketTool {
    private static volatile JWebSocketTool singleton;
    private WsManager wsManager;
    private BaseActivity activity;

    private Timer connectionLostTimer;
    private TimerTask connectionLostTimerTask;
    private int connectionLostTimeout = 20;//心跳间隔时间
    private boolean isDebugOpen = false, isFirstConnected = true;//isDebugOpen为true就不真实启动push
    private String heartBeatMsg = "heartBeatFrame";//默认心跳消息

    private Map<String, BaseLocalActivity> subscribeActivityMap = new HashMap<>();
    private Map<String, BaseLocalFragment> subscribeFragmentMap = new HashMap<>();

    private JWebSocketTool() {}

    public static JWebSocketTool getInstance() {
        if (singleton == null) {
            synchronized (JWebSocketTool.class) {
                if (singleton == null) singleton = new JWebSocketTool();
            }
        }//end of if
        return singleton;
    }

    public void startWebSocket(BaseActivity activity, String wsUrl) {
        startWebSocket(activity, wsUrl,null);
    }

    public void startWebSocket(BaseActivity activity, String wsUrl, String heartBeatMsg) {//heartBeatMsg:心跳消息字符串
        startWebSocket(activity,false, wsUrl, heartBeatMsg);
    }

    public void startWebSocket(BaseActivity activity, boolean isDebugOpen, String wsUrl, String heartBeatMsg) {//isDebugOpen是否不启动socket
        if(null == activity)return;
        if(null != wsManager){
            System.out.println("============WebSocket========当前已经启动socket了,忽略本次启动命令===>");
            return;
        }// end of if

        this.activity = activity;
        subscribeActivityMap.clear();
        subscribeFragmentMap.clear();

        this.isDebugOpen = isDebugOpen;
        if(this.isDebugOpen){
            System.out.println("============WebSocket========注意:未启动WebSocket===>");
            return;
        }// end of if
        if(!TextUtils.isEmpty(heartBeatMsg))this.heartBeatMsg = heartBeatMsg;
        System.out.println("============WebSocket========开始启动WebSocket===heartBeatMsg=>"+this.heartBeatMsg);

        closeWebSocket();
        isFirstConnected = true;
        wsManager = new WsManager.Builder(activity).client(new OkHttpClient().newBuilder().pingInterval(15, TimeUnit.SECONDS).retryOnConnectionFailure(true).build())
                .needReconnect(true).wsUrl(wsUrl).build();
        wsManager.setWsStatusListener(wsStatusListener);
        wsManager.startConnect();
    }

    public boolean isWebSocketConnect(){
        if(null != wsManager){
            return wsManager.isWsConnected();
        }else{
            System.out.println("==========WebSocket=======>无连接实体,无法检查是否连接");
        }
        return false;
    }

    public void closeWebSocket(){
        if(null != wsManager){
            cancelConnectionLostTimer();
            wsManager.stopConnect();
            wsManager.setWsStatusListener(null);
            wsManager = null;
            System.out.println("============WebSocket========关闭WebSocket了===>");
        }else{
            System.out.println("==========WebSocket=======>无连接实体,无需关闭");
        }
    }

    public void sendSocketMessage(String msg){
        if(!TextUtils.isEmpty(msg)){
            if(isWebSocketConnect()){
                wsManager.sendMessage(msg);
                System.out.println("==========WebSocket=======>发送消息:"+msg);
            }else{
                System.out.println("==========WebSocket=======>未连接成功,无法发送");
            }
        }else{
            System.out.println("==========WebSocket=======>发送消息为空,忽略发送");
        }
    }

    private void startConnectionLostTimer() {
        cancelConnectionLostTimer();
        connectionLostTimer = new Timer("WebSocketTimer");
        connectionLostTimerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("==========WebSocket=======>发送一次心跳包");
                sendSocketMessage(heartBeatMsg);
            }
        };
        connectionLostTimer.scheduleAtFixedRate(connectionLostTimerTask, 1000L * connectionLostTimeout, 1000L * connectionLostTimeout);
        System.out.println("==========WebSocket=======>启动定时心跳");
    }

    private void cancelConnectionLostTimer() {
        if( connectionLostTimer != null ) {
            connectionLostTimer.cancel();
            connectionLostTimer = null;
        }//end of if
        if( connectionLostTimerTask != null ) {
            connectionLostTimerTask.cancel();
            connectionLostTimerTask = null;
            System.out.println("==========WebSocket=======>关闭定时心跳");
        }//end of if
    }

    public boolean subscribeOneActivity(String key, BaseLocalActivity baseLocalActivity){
        if(TextUtils.isEmpty(key) || null == baseLocalActivity || null == subscribeActivityMap)return false;
        System.out.println("==========WebSocket=======>增加一个Activity订阅者==>"+key);
        subscribeActivityMap.put(key, baseLocalActivity);
        return true;
    }

    public boolean unsubscribeOneActivity(String key){
        if(TextUtils.isEmpty(key) || null == subscribeActivityMap)return false;
        System.out.println("==========WebSocket=======>移除一个Activity订阅者==>"+key);
        subscribeActivityMap.remove(key);
        return true;
    }

    public boolean subscribeOneFragment(String key, BaseLocalFragment baseLocalFragment){
        if(TextUtils.isEmpty(key) || null == baseLocalFragment || null == subscribeFragmentMap)return false;
        System.out.println("==========WebSocket=======>增加一个Fragment订阅者==>"+key);
        subscribeFragmentMap.put(key, baseLocalFragment);
        return true;
    }

    public boolean unsubscribeOneFragment(String key){
        if(TextUtils.isEmpty(key) || null == subscribeFragmentMap)return false;
        System.out.println("==========WebSocket=======>移除一个Fragment订阅者"+key);
        subscribeFragmentMap.remove(key);
        return true;
    }

    //通知打开
    private void notifyAllSubscriberConnected(){
        if(null != subscribeActivityMap && subscribeActivityMap.values().size() > 0) {
            System.out.println("==========WebSocket====打开事件===>通知"+subscribeActivityMap.values().size()+"个Activity订阅者");
            for(BaseLocalActivity baseLocalActivity : subscribeActivityMap.values()){
                if(null != baseLocalActivity)baseLocalActivity.onWebSocketConnected(isFirstConnected);
            }// end of for
        }// end of if
        if(null != subscribeFragmentMap && subscribeFragmentMap.values().size() > 0){
            System.out.println("==========WebSocket====打开事件===>通知"+subscribeFragmentMap.values().size()+"个Fragment订阅者");
            for(BaseLocalFragment baseLocalFragment : subscribeFragmentMap.values()){
                if(null != baseLocalFragment)baseLocalFragment.onWebSocketConnected(isFirstConnected);
            }// end of for
        }// end of if
    }

    //通知新消息
    private void notifyAllSubscriberMsgReceive(boolean isSuccess, WsResBaseBean wsResBaseBean, JSONObject dataJsonObj, String msgStr){
        if(TextUtils.isEmpty(msgStr))return;
        if(null != subscribeActivityMap && subscribeActivityMap.values().size() > 0) {
            System.out.println("==========WebSocket====新消息===>通知"+subscribeActivityMap.values().size()+"个Activity订阅者");
            for(BaseLocalActivity baseLocalActivity : subscribeActivityMap.values()){
                if(null != baseLocalActivity)baseLocalActivity.onWebSocketMsgArrived(isSuccess, wsResBaseBean, dataJsonObj, msgStr);
            }// end of for
        }// end of if
        if(null != subscribeFragmentMap && subscribeFragmentMap.values().size() > 0){
            System.out.println("==========WebSocket====新消息===>通知"+subscribeFragmentMap.values().size()+"个Fragment订阅者");
            for(BaseLocalFragment baseLocalFragment : subscribeFragmentMap.values()){
                if(null != baseLocalFragment)baseLocalFragment.onWebSocketMsgArrived(isSuccess, wsResBaseBean, dataJsonObj, msgStr);
            }// end of for
        }// end of if
    }

    //判断并登陆socket
    public void loginUserToSocketServer(){
        if(null != activity && activity instanceof BaseLocalActivity){
            ResUserInfoBean.Data loginUserBean = ((BaseLocalActivity)activity).getLoginUserInfoBean();
            if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getToken()))sendSocketMessage(JsonUtil.toJson(new WsReqLoginBean(loginUserBean.getToken())));
        }// end of if
    }

    private WsStatusListener wsStatusListener = new WsStatusListener() {
        @Override
        public void onOpen(Response response) {
            System.out.println("==========WebSocket======onOpenConnected==服务器连接成功=>"+(null != response ? response.toString() : "Null"));
            startConnectionLostTimer();
            loginUserToSocketServer();
            notifyAllSubscriberConnected();
            isFirstConnected = false;
        }

        @Override
        public void onMessage(String message) {
            System.out.println("==========WebSocket======onMessage==新消息==>"+message);
            if(!TextUtils.isEmpty(message)){
                WsResBaseBean wsResBaseBean = (WsResBaseBean)JsonUtil.fromJson(message, WsResBaseBean.class);
                if(null != wsResBaseBean && !TextUtils.isEmpty(wsResBaseBean.getMethod())){
                    switch (wsResBaseBean.getErrorCode()){
                        case 0://成功
                            if("pushFCusOptional".equals(wsResBaseBean.getMethod())){//足球自选赛事推送
                                WsResPushFMatchInfo pushFMatchInfo = (WsResPushFMatchInfo)JsonUtil.fromJson(message, WsResPushFMatchInfo.class);
                                if(null != pushFMatchInfo && null != pushFMatchInfo.getBody()){
                                    StephenUtil.showPushMatchInfoDialog(activity, 0, pushFMatchInfo.getBody().getMatchId(),""+pushFMatchInfo.getBody().getProgressTime(), pushFMatchInfo.getBody().gethName(),
                                            pushFMatchInfo.getBody().getaName(), ""+pushFMatchInfo.getBody().gethScore(), ""+pushFMatchInfo.getBody().getaScore());
                                }// end of if
                            }else if("pushFSysOptional".equals(wsResBaseBean.getMethod())){//足球系统选择赛事推送(强主队推送)
                                WsResPushFMatchInfo pushFMatchInfo = (WsResPushFMatchInfo)JsonUtil.fromJson(message, WsResPushFMatchInfo.class);
                                if(null != pushFMatchInfo && null != pushFMatchInfo.getBody()){
                                    StephenUtil.showPushMatchInfoDialog(activity, 1, pushFMatchInfo.getBody().getMatchId(),""+pushFMatchInfo.getBody().getProgressTime(), pushFMatchInfo.getBody().gethName(),
                                            pushFMatchInfo.getBody().getaName(), ""+pushFMatchInfo.getBody().gethScore(), ""+pushFMatchInfo.getBody().getaScore());
                                }// end of if
                            }else if("pushFGoal".equals(wsResBaseBean.getMethod())){//足球进球
                                WsResPushFMatchInfo pushFMatchInfo = (WsResPushFMatchInfo)JsonUtil.fromJson(message, WsResPushFMatchInfo.class);
                                if(null != pushFMatchInfo && null != pushFMatchInfo.getBody()){
                                    StephenUtil.showPushMatchInfoDialog(activity, 2, pushFMatchInfo.getBody().getMatchId(),""+pushFMatchInfo.getBody().getTime(), pushFMatchInfo.getBody().gethName(),
                                            pushFMatchInfo.getBody().getaName(), ""+pushFMatchInfo.getBody().gethScore(), ""+pushFMatchInfo.getBody().getaScore());
                                }// end of if
                            }else if("pushFRedCard".equals(wsResBaseBean.getMethod())){//足球红牌
                                WsResPushFMatchInfo pushFMatchInfo = (WsResPushFMatchInfo)JsonUtil.fromJson(message, WsResPushFMatchInfo.class);
                                if(null != pushFMatchInfo && null != pushFMatchInfo.getBody()){
                                    StephenUtil.showPushMatchInfoDialog(activity, 3, pushFMatchInfo.getBody().getMatchId(),""+pushFMatchInfo.getBody().getTime(), pushFMatchInfo.getBody().gethName(),
                                            pushFMatchInfo.getBody().getaName(), ""+pushFMatchInfo.getBody().gethRedCardCnt(), ""+pushFMatchInfo.getBody().getaRedCardCnt());
                                }// end of if
                            }else{
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(message);
                                    if(null != jsonObject && jsonObject.has("body"))jsonObject = jsonObject.getJSONObject("body");
                                } catch (Exception e){}
                                notifyAllSubscriberMsgReceive(true, wsResBaseBean, jsonObject, message);
                            }
                            break;
                        default:
                            if("otherLogin".equals(wsResBaseBean.getMethod()) && -2 == wsResBaseBean.getErrorCode()){//下线
                                StephenUtil.curUserLogoutInfo(activity,false);
                                StephenToolUtils.showHintInfoDialog(activity, !TextUtils.isEmpty(wsResBaseBean.getErrMsg()) ? wsResBaseBean.getErrMsg() : "您的账号已被强制下线!", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        StephenToolUtils.startActivityAndClearTopFinish(activity, MainActivity.class, null);
                                    }
                                });
                            }else{
                                if(null != activity && !TextUtils.isEmpty(wsResBaseBean.getErrMsg()))StephenToolUtils.showShortHintInfo(activity, wsResBaseBean.getErrMsg());
                            }
                            notifyAllSubscriberMsgReceive(false, wsResBaseBean, null, message);
                            break;
                    }//end of switch
                }// end of if
            }// end of if
        }

        @Override
        public void onMessage(ByteString bytes) {
            System.out.println("==========WebSocket======onMessage==Byte=>"+(null != bytes ? bytes.toString() : "Null"));
        }

        @Override
        public void onReconnect() {
            System.out.println("==========WebSocket======onReconnect==服务器重连接中=>");
        }

        @Override
        public void onClosing(int code, String reason) {
            System.out.println("==========WebSocket======onClosing==服务器连接关闭中=>"+code+"====>"+reason);
        }

        @Override
        public void onClosed(int code, String reason) {
            System.out.println("==========WebSocket======onClosed==服务器连接已关闭=>"+code+"====>"+reason);
            cancelConnectionLostTimer();
        }

        @Override
        public void onFailure(Throwable t, Response response) {
            System.out.println("==========WebSocket======onFailure==服务器连接失败=>"+(null != t ? t.getMessage() : "Null"));
            cancelConnectionLostTimer();
        }
    };
}

package com.xyebank.stephen.push;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.widget.Toast;

import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushAdapter;
import com.coloros.mcssdk.mode.ErrorCode;
import com.httpurlconnectionutil.RomUtils;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;
import com.huawei.android.hms.agent.push.handler.GetTokenHandler;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class StephenPushUtils {
    private static volatile StephenPushUtils singleton;
    private static final String PushTokenKey = "pushToken",PushPlatformIdKey = "platformId";
    public static final int PushTypeJG = 0,PushTypeXM = 2,PushTypeHW = 1,PushTypeVIVO = 3,PushTypeOPPO = 4;
    private int bootPushType = PushTypeJG;//默认极光
    private Application context = null;
    private Activity activityForHw = null;
    private boolean isDebugOpen = false, isShowInfoMsg = false;//isDebugOpen为true就不真实启动push
    private String miPushAppID = null, miPushAppKEY = null;//小米push相关参数

    private StephenPushUtils() {}

    public static StephenPushUtils getInstance() {
        if(null == singleton){
            synchronized (StephenPushUtils.class) {
                if(null == singleton){
                    singleton = new StephenPushUtils();
                }// end of if
            }// end
        }// end of if
        return singleton;
    }

    public void initStephenPush(final Application context, boolean isDebugOpen, boolean isShowMsg){
        this.context = context;
        this.isDebugOpen = isDebugOpen;
        this.isShowInfoMsg = isShowMsg;
        final String miPushAppId = getMetaDataVal(this.context,"MiPushAppId","AppId=");
        final String miPushAppKey = getMetaDataVal(this.context,"MiPushAppKey","AppKey=");
        final String oppoPushAppId = getMetaDataVal(this.context,"OppoPushAppId","AppId=");
        final String oppoPushAppKey = getMetaDataVal(this.context,"OppoPushAppKey","AppKey=");

        if(RomUtils.isMiui()){
            bootPushType = PushTypeXM;
        }else if(RomUtils.isEmui()){
            bootPushType = PushTypeHW;
        }/*else if(RomUtils.isVivo()){
            bootPushType = PushTypeVIVO;
        }*//*else if(RomUtils.isOppo()){//暂未开放oppo
            bootPushType = PushTypeOPPO;
        }*/else{
            bootPushType = PushTypeJG;
        }
        if(this.isDebugOpen){
            System.out.println("========com.stephen.push=======注意:未启动聚合Push===>");
            return;
        }// end of if
        initStephenPushCore(miPushAppId, miPushAppKey, oppoPushAppId, oppoPushAppKey);
    }

    private void initStephenPushCore(String miPushAppId, String miPushAppKey, String oppoPushAppId, String oppoPushAppKey){//注册启动push服务
        if(getStephenPushType() == PushTypeXM && (TextUtils.isEmpty(miPushAppId) || TextUtils.isEmpty(miPushAppKey))){
            printLogAndMsg("启用小米推送必须同时设置小米的AppId和AppKey,已取消启动推送,请设置值后重试!");
            return;
        }// end of if
        if(getStephenPushType() == PushTypeOPPO && (TextUtils.isEmpty(oppoPushAppId) || TextUtils.isEmpty(oppoPushAppKey))){
            printLogAndMsg("启用OPPO推送必须同时设置oppo的AppId和AppKey,已取消启动推送,请设置值后重试!");
            return;
        }// end of if
        System.out.println("=====com.stephen.push====开始注册启动push服务===>"+getStephenPushType());
        miPushAppID = miPushAppId;
        miPushAppKEY = miPushAppKey;
        switch (getStephenPushType()) {
            case PushTypeXM://小米
                if(isShowInfoMsg)Toast.makeText(context, "初始化小米推送", Toast.LENGTH_LONG).show();
                MiPushClient.registerPush(context, miPushAppID, miPushAppKEY);
                Logger.setLogger(context, new LoggerInterface() {
                    @Override
                    public void setTag(String tag) {}

                    @Override
                    public void log(String content, Throwable t) {
                        System.out.println("=====com.stephen.push=====>" + content + "=====>" + (null != t ? t.getMessage() : ""));
                    }

                    @Override
                    public void log(String content) {
                        System.out.println("=====com.stephen.push=====>" + content);
                    }
                });
                break;
            case PushTypeHW://华为
                if(isShowInfoMsg)Toast.makeText(context, "初始化华为推送", Toast.LENGTH_LONG).show();
                HMSAgent.init(context);
                break;
            case PushTypeVIVO://vivo
                if(isShowInfoMsg)Toast.makeText(context, "初始化VIVO推送", Toast.LENGTH_LONG).show();
                if(PushClient.getInstance(context).isSupport()){
                    PushClient.getInstance(context).initialize();
                    PushClient.getInstance(context).turnOnPush(new IPushActionListener() {//打开推送服务
                        @Override
                        public void onStateChanged(int code) {
                            if (code == 0) {//打开推送服务成功
                                System.out.println("=====com.stephen.push==打开Vivo推送服务成功===>");
                            } else {
                                printLogAndMsg("打开Vivo推送服务失败("+code+")");
                            }
                        }
                    });
                }else{
                    printLogAndMsg("初始化VIVO推送失败,系统不支持");
                }
                break;
            case PushTypeOPPO://oppo
                if(isShowInfoMsg)Toast.makeText(context, "初始化OPPO推送", Toast.LENGTH_LONG).show();
                if(PushManager.isSupportPush(context)){
                    PushManager.getInstance().register(context, oppoPushAppId, oppoPushAppKey, new PushAdapter() {
                        @Override
                        public void onRegister(int code, String regId) {
                            if (code == ErrorCode.SUCCESS) {//注册成功
                                System.out.println("=====com.stephen.push==注册OppoPush成功===>"+regId);
                                recordStephenPushToken(PushTypeOPPO, regId);
                            } else {
                                printLogAndMsg("初始化OPPO推送失败,注册失败("+code+")");
                            }
                        }
                    });
                }else{
                    printLogAndMsg("初始化OPPO推送失败,系统不支持");
                }
                break;
            default://极光
                JPushInterface.init(context);
                JPushInterface.setDebugMode(isShowInfoMsg);
                if(isShowInfoMsg)Toast.makeText(context, "初始化极光推送", Toast.LENGTH_LONG).show();
                break;
        }// end of switch
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                recordStephenMiPushToken();//谨慎起见,再设一次
                recordStephenJPushToken();//谨慎起见,再设一次
                recordStephenVivoPushToken();//谨慎起见,再设一次
                recordStephenOppoPushToken();//谨慎起见,再设一次
                if(getStephenPushType() == PushTypeHW){
                    setActivityForBindHw(null);
                    HMSAgent.Push.getToken(new GetTokenHandler() {
                        @Override
                        public void onResult(int rst) {
                            System.out.println("===com.stephen.push===华为推送==GetTokenHandler====>"+(0 == rst ? "成功" : "失败:Code:"+rst));
                        }
                    });
                }// end of if
            }
        }, 3000);
    }

    //应用主activity的onCreate方法中必须调用
    public void setActivityForBindHw(Activity activityForHw){
        if(isDebugOpen)return;
        if(null != activityForHw)this.activityForHw = activityForHw;
        if(null != this.activityForHw)HMSAgent.connect(this.activityForHw, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                System.out.println("===com.stephen.push===华为推送==onConnect====>"+(0 == rst ? "成功" : "失败:Code:"+rst));
            }
        });
    }

    private void recordStephenMiPushToken(){
        String pushToken = MiPushClient.getRegId(context);
        System.out.println("======com.stephen.push====MiPushToken=>"+pushToken);
        if(!TextUtils.isEmpty(pushToken))SharedUtil.putString(context,PushTokenKey+PushTypeXM, pushToken);
    }

    private void recordStephenJPushToken(){
        String pushToken = JPushInterface.getRegistrationID(context);
        System.out.println("======com.stephen.push====JPushToken=>"+pushToken);
        if(!TextUtils.isEmpty(pushToken))SharedUtil.putString(context,PushTokenKey+PushTypeJG, pushToken);
    }

    private void recordStephenVivoPushToken(){
        String pushToken = PushClient.getInstance(context).getRegId();
        System.out.println("======com.stephen.push====JPushToken=>"+pushToken);
        if(!TextUtils.isEmpty(pushToken))SharedUtil.putString(context,PushTokenKey+PushTypeVIVO, pushToken);
    }

    private void recordStephenOppoPushToken(){
        String pushToken = PushManager.getInstance().getRegisterID();
        System.out.println("======com.stephen.push====JPushToken=>"+pushToken);
        if(!TextUtils.isEmpty(pushToken))SharedUtil.putString(context,PushTokenKey+PushTypeOPPO, pushToken);
    }

    //记录推送对应token
    public void recordStephenPushToken(int curPushType,String pushToken){
        if(getStephenPushType() != curPushType){
            printLogAndMsg("记录PushToken推送和目前启动的推送类型不一致,请检查!");
            return;
        }// end of if
        switch(curPushType){
            case PushTypeJG:
            case PushTypeXM:
            case PushTypeHW:
            case PushTypeVIVO:
            case PushTypeOPPO:
                SharedUtil.putString(context,PushTokenKey+getStephenPushType(), pushToken);
                break;
            default:
                printLogAndMsg("记录PushToken推送标识类型不存在,请检查!");
                break;
        }// end of switch
    }

    private void printLogAndMsg(String msg){
        if(TextUtils.isEmpty(msg))msg = "消息内容为空!";
        if(isShowInfoMsg)Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        System.out.println("=====com.stephen.push=====>"+msg);
    }

    // 获取推送通道类型
    public int getStephenPushType(){
        return bootPushType;
    }

    // 获取推送标识(华为为华为token,其他是注册id)
    public String getStephenPushTokenOrRegId(){
        return getStephenPushTokenOrRegId(getStephenPushType());
    }

    public String getStephenPushTokenOrRegId(int curPushType){
        String pushToken = SharedUtil.getString(context,PushTokenKey+curPushType);
        if(isDebugOpen)return pushToken;
        switch(curPushType){
            case PushTypeJG:
                if(TextUtils.isEmpty(pushToken))pushToken = JPushInterface.getRegistrationID(context);
                break;
            case PushTypeXM:
                if(TextUtils.isEmpty(pushToken))pushToken = MiPushClient.getRegId(context);
                break;
            case PushTypeVIVO:
                if(TextUtils.isEmpty(pushToken))pushToken = PushClient.getInstance(context).getRegId();
                break;
            case PushTypeOPPO:
                if(TextUtils.isEmpty(pushToken))pushToken = PushManager.getInstance().getRegisterID();
                break;
        }// end of switch
        System.out.println("=====com.stephen.push===外部获取当前Push=TokenOrRegId=>"+pushToken);
        return (TextUtils.isEmpty(pushToken)) ? "" : pushToken;
    }

    public void setStephenPushAlias(String setAlias){
        if(isDebugOpen)return;
        System.out.println("=====com.stephen.push===外部设置当前Push=别名=>"+setAlias);
        switch(getStephenPushType()){
            case PushTypeJG:
                JPushInterface.setAlias(context, -1, setAlias);// 第二个参数时用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性
                break;
            case PushTypeXM:
                MiPushClient.setAlias(context, setAlias, null);// 第三参数时扩展参数，暂时没有用途，直接填null
                break;
            case PushTypeHW:
                printLogAndMsg("华为推送暂不支持别名设置,请后台按token推送");
                break;
            case PushTypeVIVO:
                PushClient.getInstance(context).bindAlias(setAlias, new IPushActionListener() {
                    @Override
                    public void onStateChanged(int code) {
                        System.out.println("=====com.stephen.push===外部设置当前VivoPush=别名==code=>"+code);
                    }
                });
                break;
            case PushTypeOPPO:
                List<String> aliasList = new ArrayList<>();
                aliasList.add(setAlias);
                PushManager.getInstance().setAliases(aliasList);
                break;
        }// end of switch
    }

    public void delStephenPushAlias(String delAlias){
        if(isDebugOpen)return;
        System.out.println("=====com.stephen.push===外部删除当前Push=别名=>"+delAlias);
        switch(getStephenPushType()){
            case PushTypeJG:
                JPushInterface.deleteAlias(context, -1);// 第二个参数时用户自定义的操作序列号，同操作结果一起返回，用来标识一次操作的唯一性
                break;
            case PushTypeXM:
                MiPushClient.unsetAlias(context, delAlias, null);// 第三参数时扩展参数，暂时没有用途，直接填null
                break;
            case PushTypeHW:
                printLogAndMsg("华为推送暂不支持别名清除,请后台按token推送");
                break;
            case PushTypeVIVO:
                PushClient.getInstance(context).unBindAlias(delAlias, new IPushActionListener() {
                    @Override
                    public void onStateChanged(int code) {
                        System.out.println("=====com.stephen.push===外部删除当前VivoPush=别名==code=>"+code);
                    }
                });
                break;
            case PushTypeOPPO:
                PushManager.getInstance().unsetAlias(delAlias);
                break;
        }// end of switch
    }

    /*********************************以下为辅助方法*************************************/

    //是否主线程
    public boolean shouldInit(Context context) {
        List<ActivityManager.RunningAppProcessInfo> processInfos = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningAppProcesses();
        if(null != processInfos && processInfos.size() > 0) for(ActivityManager.RunningAppProcessInfo info : processInfos) if(null != info && info.pid == Process.myPid() && context.getPackageName().equals(info.processName)) return true;
        return false;
    }

    public boolean isAppRunning(Context context, String checkPackageName){
        if(null == context || TextUtils.isEmpty(checkPackageName))return false;
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        /*List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        for(ActivityManager.RunningAppProcessInfo appProcess : appProcesses)if(appProcess.processName.equals(context.getPackageName()))return true;
        return false;*/

        boolean isRunning = false;
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if(null != list && list.size() > 0){//100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表示系统后台有此进程在运行
            for(ActivityManager.RunningTaskInfo info : list) {
                if(null != info && ((null != info.topActivity && checkPackageName.equals(info.topActivity.getPackageName())) || (null != info.baseActivity && checkPackageName.equals(info.baseActivity.getPackageName())))){
                    isRunning = true;
                    break;
                }// end of if
            }// end of for
        }// end of if
        return isRunning;
    }

    public boolean isActivityRunning(Context context, String checkActivityName){
        if(null == context || TextUtils.isEmpty(checkActivityName))return false;
        boolean isRunning = false;
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if(null != list && list.size() > 0){//100表示取的最大的任务数，info.topActivity表示当前正在运行的Activity，info.baseActivity表示系统后台有此进程在运行
            for(ActivityManager.RunningTaskInfo info : list) {
                if(null != info && ((null != info.topActivity && checkActivityName.equals(info.topActivity.getClassName())) || (null != info.baseActivity && checkActivityName.equals(info.baseActivity.getClassName())))){
                    isRunning = true;
                    break;
                }// end of if
            }// end of for
        }// end of if
        return isRunning;
    }

    // 运行时从配置清单文件获取字段
    private String getMetaDataVal(Context context, String dataKey, String subStr){
        String metaDataVal = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            metaDataVal = appInfo.metaData.getString(dataKey);
            if(!TextUtils.isEmpty(metaDataVal) && !TextUtils.isEmpty(subStr))metaDataVal = metaDataVal.substring(metaDataVal.indexOf(subStr)+subStr.length());
        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        return metaDataVal;
    }
}

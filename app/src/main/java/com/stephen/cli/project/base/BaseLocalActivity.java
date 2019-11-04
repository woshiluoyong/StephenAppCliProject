package com.stephen.cli.project.base;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.stephen.cli.project.activity.LoginActivity;
import com.stephen.cli.project.activity.MainActivity;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.entity.WsResBaseBean;
import com.stephen.cli.project.library.BaseActivity;
import com.stephen.cli.project.library.BaseFragment;
import com.stephen.cli.project.library.HomeKeyListenerHelper;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.stephen.cli.project.library.StephenCommonNoDataView;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.ApiRequestTool;
import com.stephen.cli.project.utils.Constants;
import com.stephen.cli.project.utils.StephenCommonNoDataTool;

import org.json.JSONObject;

import java.util.List;

//BaseLocalActivity继承基类完成和业务相关的东西
public class BaseLocalActivity extends BaseActivity implements HomeKeyListenerHelper.HomeKeyListener{
    protected StephenCommonNoDataTool stephenCommonNoDataTool;
    protected HomeKeyListenerHelper homeKeyListenerHelper;

    public ResUserInfoBean.Data loginUserBean = null;

    @Override
    protected void mainInitMethod(BaseActivity activity) {
        super.mainInitMethod(activity);
        homeKeyListenerHelper = new HomeKeyListenerHelper(activity);
    }

    @Override
    public void setActivityContentView() {}

    @Override
    public void initializeActivityFunction() {}

    @Override
    public void getActivityContentData(Object... objects) {}

    @Override
    public boolean needExitActivity() {
        return (null != activity && (activity instanceof MainActivity));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case Constants.Req_EnterLoginPage:
                onNoDataCallRefresh(true);
                //StephenUtil.checkPushRegIdReport(activity);
                break;
        }//end of switch
    }

    //游客模式检查登录
    public boolean checkCurrentIsLogin(boolean isJumpLogin){
        if(!SharedUtil.contains(activity, Constants.Key_UserInfo)){
            if(isJumpLogin)StephenToolUtils.startActivityNoFinish(activity, LoginActivity.class,null, Constants.Req_EnterLoginPage);
            return false;
        }// end of if
        return true;
    }

    //获取用户信息实体
    public ResUserInfoBean.Data getLoginUserInfoBean(){
        loginUserBean = (null != activity && SharedUtil.contains(activity,Constants.Key_UserInfo)) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(activity, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
        return loginUserBean;
    }

    protected void initSetStephenCommonNoData(StephenCommonNoDataView stephenCommonNoDataView){
        if(null == stephenCommonNoDataView)return;
        stephenCommonNoDataView.setOnNoDataViewClickListener(true, new StephenCommonNoDataView.OnNoDataViewClickListener() {
            @Override
            public void onNoDataViewClick(int responseClickFlag) {
                if (responseClickFlag == ApiRequestTool.NoDataForNoLogin || responseClickFlag == ApiRequestTool.NoDataForLoginTimeOut) {
                    if (checkCurrentIsLogin(true))onNoDataCallRefresh(true);
                } else {
                    onNoDataCallRefresh(true);
                }
            }
        });
        stephenCommonNoDataTool = new StephenCommonNoDataTool(activity, stephenCommonNoDataView);
    }

    protected void onNoDataCallRefresh(boolean isHand){}

    protected void callFragmentActivityResult(List<BaseFragment> mainFragments, int curIndex, int requestCode, int resultCode, @Nullable Intent data){
        if(null == mainFragments || mainFragments.size() <= 0)return;
        if (curIndex >= 0 && curIndex < mainFragments.size()) {
            BaseFragment baseFragment = mainFragments.get(curIndex);
            if(null != baseFragment && baseFragment.isAdded())baseFragment.onActivityResult(requestCode, resultCode, data);
        }// end of if
    }

    /*@Override
    public void onResume() {
        if(null != homeKeyListenerHelper)homeKeyListenerHelper.registerHomeKeyListener(this);
        super.onResume();
        if(activity instanceof SplashActivity){}else{ JWebSocketTool.getInstance().startWebSocket(((MainApplication)getApplication()).getMainActivity(), JsonUtil.toJson(new WsReqBaseBean("isOnline"))); }//排除启动页
        if(isSubscribeWebSocket())JWebSocketTool.getInstance().subscribeOneActivity(activity.getClass().getName(),this);
    }

    @Override
    public void onPause() {
        if(null != homeKeyListenerHelper)homeKeyListenerHelper.unregisterHomeKeyListener();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(null != homeKeyListenerHelper)homeKeyListenerHelper.unregisterHomeKeyListener();
        super.onDestroy();
        if(isSubscribeWebSocket())JWebSocketTool.getInstance().unsubscribeOneActivity(activity.getClass().getName());
    }*/

    @Override
    public void onHomeKeyLongPressed() {
        System.out.println("========onHomeKeyLongPressed=====>");
        //JWebSocketTool.getInstance().closeWebSocket();
    }

    @Override
    public void onHomeKeyShortPressed() {
        System.out.println("========onHomeKeyShortPressed=====>");
        //JWebSocketTool.getInstance().closeWebSocket();
    }

    //是否订阅webSocket消息
    public boolean isSubscribeWebSocket(){
        return false;
    }

    //webSocket连接成功
    public void onWebSocketConnected(boolean isFirstConnected){
        System.out.println("========WebSocket=====Activity===onWebSocketConnected====>"+isFirstConnected);
    }

    //webSocket消息到达
    public void onWebSocketMsgArrived(boolean isSuccess, WsResBaseBean wsResBaseBean, JSONObject dataJsonObj, String msgStr){
        System.out.println("========WebSocket=====Activity===onWebSocketMsgArrived====>"+msgStr);
    }
}

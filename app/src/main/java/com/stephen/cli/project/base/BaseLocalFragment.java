package com.stephen.cli.project.base;


import android.content.Intent;

import com.stephen.cli.project.entity.WsResBaseBean;
import com.stephen.cli.project.library.BaseFragment;
import com.stephen.cli.project.library.StephenCommonNoDataView;
import com.stephen.cli.project.utils.ApiRequestTool;
import com.stephen.cli.project.utils.StephenCommonNoDataTool;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.annotation.Nullable;

//BaseLocalFragment继承基类完成和业务相关的东西
public abstract class BaseLocalFragment extends BaseFragment {
    protected StephenCommonNoDataTool stephenCommonNoDataTool;

    public Map<String, String> reqParamMap = new HashMap();//请求参数map

    protected void initSetStephenCommonNoData(StephenCommonNoDataView stephenCommonNoDataView){
        initSetStephenCommonNoData(stephenCommonNoDataView,true);
    }

    protected void initSetStephenCommonNoData(StephenCommonNoDataView stephenCommonNoDataView, boolean isNeedCheckLoginCode){
        if(null == stephenCommonNoDataView)return;
        stephenCommonNoDataView.setOnNoDataViewClickListener(true, new StephenCommonNoDataView.OnNoDataViewClickListener() {
            @Override
            public void onNoDataViewClick(int responseClickFlag) {
                if (responseClickFlag == ApiRequestTool.NoDataForNoLogin || responseClickFlag == ApiRequestTool.NoDataForLoginTimeOut) {
                    if (((BaseLocalActivity)activity).checkCurrentIsLogin(true))onHeaderRefresh(true);
                } else {
                    onHeaderRefresh(true);
                }
            }
        });
        stephenCommonNoDataTool = new StephenCommonNoDataTool(activity, stephenCommonNoDataView, isNeedCheckLoginCode);
    }

    protected void onHeaderRefresh(boolean isHand){}

    protected void callFragmentActivityResult(List<BaseLocalFragment> mainFragments, int curIndex, int requestCode, int resultCode, @Nullable Intent data){
        if(null == mainFragments || mainFragments.size() <= 0)return;
        if (curIndex >= 0 && curIndex < mainFragments.size()) {
            BaseLocalFragment baseFragment = mainFragments.get(curIndex);
            if(null != baseFragment && baseFragment.isAdded())baseFragment.onActivityResult(requestCode, resultCode, data);
        }// end of if
    }

    //专为二级fragment获取参数使用
    public Map<String, String> getSecondLevelParamMap(){
        return null;
    }

    public Object getFragmentResultData(){
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if(isSubscribeWebSocket())JWebSocketTool.getInstance().subscribeOneFragment(this.getClass().getName(),this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //if(isSubscribeWebSocket())JWebSocketTool.getInstance().unsubscribeOneFragment(this.getClass().getName());
    }

    //是否订阅webSocket消息
    public boolean isSubscribeWebSocket(){
        return false;
    }

    //webSocket连接成功
    public void onWebSocketConnected(boolean isFirstConnected){
        System.out.println("========WebSocket=====Fragment===onWebSocketConnected====>"+isFirstConnected);
    }

    //webSocket消息到达
    public void onWebSocketMsgArrived(boolean isSuccess, WsResBaseBean wsResBaseBean, JSONObject dataJsonObj, String msgStr){
        System.out.println("========WebSocket=====Fragment===onWebSocketMsgArrived====>"+msgStr);
    }
}

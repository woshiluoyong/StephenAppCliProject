package com.stephen.cli.project.utils;

import android.text.TextUtils;
import android.view.View;

import com.stephen.car.hailing.R;
import com.stephen.cli.project.library.BaseActivity;
import com.stephen.cli.project.library.NetworkUtil;
import com.stephen.cli.project.library.StephenCommonNoDataView;
import com.stephen.cli.project.library.StephenToolUtils;

import android.support.v4.content.res.ResourcesCompat;

public class StephenCommonNoDataTool {
    private BaseActivity activity;
    private StephenCommonNoDataView stephenCommonNoDataView;
    private boolean isNeedCheckLoginCode = false;

    public StephenCommonNoDataTool(BaseActivity activity, StephenCommonNoDataView stephenCommonNoDataView) {
        this(activity, stephenCommonNoDataView,true);
    }

    public StephenCommonNoDataTool(BaseActivity activity, StephenCommonNoDataView stephenCommonNoDataView, boolean isNeedCheckLoginCode) {
        this.activity = activity;
        this.stephenCommonNoDataView = stephenCommonNoDataView;
        this.isNeedCheckLoginCode = isNeedCheckLoginCode;
    }

    public int commonNoDataViewCheck(int serverCode, boolean serverNotData) {//,NoDataForNotwork没网提示,NoDataForLogin没登录提示,NoDataForServer服务器没数据提
        if (!NetworkUtil.isConnected(activity)) return ApiRequestTool.NoDataForNotwork;
        int rspCode = serverCode;
        if ((isNeedCheckLoginCode ? (rspCode == ApiRequestTool.NoDataForNoLogin || rspCode == ApiRequestTool.NoDataForLoginTimeOut) : false) || rspCode == ApiRequestTool.NoDataForReqFail
                || rspCode == ApiRequestTool.NoDataForReqError || rspCode == ApiRequestTool.NoDataForNeedVip || rspCode == ApiRequestTool.NoDataForParamError) {//重要的code优先判断
            return rspCode;
        } else {
            if (serverNotData) rspCode = ApiRequestTool.NoDataForServer;
        }
        return rspCode;
    }

    public boolean commonNoDataViewShow(boolean serverNotData) {
        return commonNoDataViewShow(StephenCommonNoDataView.defaultCode, serverNotData);
    }

    public boolean commonNoDataViewShow(boolean serverNotData, boolean isResponseClick) {
        return commonNoDataViewShow(StephenCommonNoDataView.defaultCode, serverNotData, null, isResponseClick);
    }

    public boolean commonNoDataViewShow(boolean serverNotData, String hintMsg) {
        return commonNoDataViewShow(StephenCommonNoDataView.defaultCode, serverNotData, hintMsg);
    }

    public boolean commonNoDataViewShow(boolean serverNotData, String hintMsg, boolean isResponseClick) {
        return commonNoDataViewShow(StephenCommonNoDataView.defaultCode, serverNotData, hintMsg, isResponseClick);
    }

    public boolean commonNoDataViewShow(int serverCode, boolean serverNotData) {
        return commonNoDataViewShow(serverCode, serverNotData,null, true);
    }

    public boolean commonNoDataViewShow(int serverCode, boolean serverNotData, String hintMsg) {
        return commonNoDataViewShow(serverCode, serverNotData,hintMsg, true);
    }

    public boolean commonNoDataViewShow(int serverCode, boolean serverNotData, String hintMsg, boolean isResponseClick) {
        if (null == stephenCommonNoDataView) return false;
        int rspCode = commonNoDataViewCheck(serverCode, serverNotData);
        stephenCommonNoDataView.setResponseClickFlag(rspCode);
        stephenCommonNoDataView.removeCenterTextBottomBtn();
        switch (rspCode) {
            case ApiRequestTool.NoDataForNotwork:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, activity.getString(R.string.net_work_connect_fail));
                return true;
            case ApiRequestTool.NoDataForServer:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.no_data_hint) : hintMsg)+(isResponseClick ? " 点击刷新!" : ""));
                return true;
            case ApiRequestTool.NoDataForParamError:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.param_error_hint) : hintMsg));
                return true;
            case ApiRequestTool.NoDataForNoLogin:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.not_login_hint) : hintMsg)+(isResponseClick ? "" : ""));
                return true;
            case ApiRequestTool.NoDataForLoginTimeOut:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.token_expires_hint) : hintMsg)+(isResponseClick ? "" : ""));
                return true;
            case ApiRequestTool.NoDataForReqFail:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.request_data_exception) : hintMsg)+(isResponseClick ? " 点击刷新!" : ""));
                return true;
            case ApiRequestTool.NoDataForReqError:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, (TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.request_data_error) : hintMsg)+(isResponseClick ? " 点击刷新!" : ""));
                return true;
            case ApiRequestTool.NoDataForNeedVip:
                stephenCommonNoDataView.setNoDataViewShow(isResponseClick, "仅对VIP会员开放"/*(TextUtils.isEmpty(hintMsg) ? activity.getString(R.string.need_permission_hint) : hintMsg)+"点击刷新!"*/);
                stephenCommonNoDataView.setCenterTextBottomBtn("邀请注册免费获得会员", 16, ResourcesCompat.getColor(activity.getResources(), R.color.white, null), ResourcesCompat.getDrawable(activity.getResources(), R.drawable.reg_vip_hint_btn_shape_selector, null),
                    200, 45, 10, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            StephenToolUtils.showShortHintInfo(activity,"跳转邀请页面");
                        }
                    });
                return true;
            default:
                stephenCommonNoDataView.setNoDataViewHide();
                return false;
        }//end of switch
    }
}

package com.stephen.cli.project.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stephen.cli.project.MainApplication;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalActivity;
import com.stephen.cli.project.base.BaseLocalFragment;
import com.stephen.cli.project.bean.ResBaseBean;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.library.DateUtil;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.stephen.cli.project.library.StephenCommonTopTitleView;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.ApiOnMyRequestListener;
import com.stephen.cli.project.utils.ApiRequestTool;
import com.stephen.cli.project.utils.Constants;
import com.stephen.cli.project.utils.StephenUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONObject;

import android.support.v4.content.res.ResourcesCompat;
import okhttp3.Call;

public class TabMineFragment extends BaseLocalFragment {
    private StephenCommonTopTitleView stephenCommonTopTitleView;
    private QMUIGroupListView mGroupListView;
    private QMUIGroupListView.Section section;
    private SmartRefreshLayout mPullRefreshLayout;
    private TextView userNameT, vipDateT;
    private ImageView headImgV, vipImgV;

    private int itemHeightDp = 40;
    private ResUserInfoBean.Data loginUserBean;

    @Override
    public View getFragmentContentView(LayoutInflater inflater) {
        stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, -30);
        //stephenCommonTopTitleView.setParentFrameBgColorHex("#ff0000");
        stephenCommonTopTitleView.setTitleBgColor(Color.parseColor("#098480"));
        stephenCommonTopTitleView.setTitleCenterText("我的", 16, "#ffffff", false);
        stephenCommonTopTitleView.setTitleRightIcon(R.drawable.icon_share_btn, stephenCommonTopTitleView.getTitleRightLp(22,22,5));

        stephenCommonTopTitleView.setTitleRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenUtil.showShareDialog(activity, null);
            }
        });
        return stephenCommonTopTitleView.injectCommTitleViewToAllViewReturnView(R.layout.fragment_main_mine);
    }

    @Override
    public void initializeFragmentData() {
        mPullRefreshLayout = findUiViewToInstantiation(R.id.mainRefreshLy);
        mGroupListView = findUiViewToInstantiation(R.id.groupListView);
        headImgV = findUiViewToInstantiation(R.id.headImgV);
        vipImgV = findUiViewToInstantiation(R.id.vipImgV);
        userNameT = findUiViewToInstantiation(R.id.userNameT);
        vipDateT = findUiViewToInstantiation(R.id.vipDateT);

        mPullRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onHeaderRefresh(false);
            }
        });

        findUiViewToInstantiation(R.id.mainRy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseLocalActivity)activity).checkCurrentIsLogin(true);
            }
        });
        /*headImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PictureSelector.create(activity, PictureSelector.SELECT_REQUEST_CODE).selectPicture();
            }
        });*/

        createMainGroupList();
    }

    @Override
    public void getFragmentContentData(Object... objects) {
        showMainContent(null);
        mPullRefreshLayout.autoRefresh();
    }

    @Override
    protected void onHeaderRefresh(boolean isHand) {
        initGetMainInfoData();
    }

    private void createMainGroupList(){
        if(null != section)section.removeFrom(mGroupListView);
        section = QMUIGroupListView.newSection(activity);
        section.setLeftIconSize(StephenToolUtils.dip2px(activity, 18), StephenToolUtils.dip2px(activity, 18));

        QMUICommonListItemView item0View = mGroupListView.createItemView(null,"VIP会员",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item0View.getTextView().setTextSize(14);
        section.addItemView(item0View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageVipMember", Constants.Req_EnterVipLevel);
            }
        });

        QMUICommonListItemView item1View = mGroupListView.createItemView(null,"个人资料",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item1View.getTextView().setTextSize(14);
        section.addItemView(item1View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageUserInfoView", Constants.Req_EnterUserInfo);
            }
        });

        QMUICommonListItemView item2View = mGroupListView.createItemView(null,"分享",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item2View.getTextView().setTextSize(14);
        item2View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item2View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageShareAppInfo");
            }
        });

        QMUICommonListItemView item3View = mGroupListView.createItemView(null,"自选赛事设置",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item3View.getTextView().setTextSize(14);
        item3View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item3View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageSettingSelfContest");
            }
        });

        QMUICommonListItemView item4View = mGroupListView.createItemView(null,"意见反馈",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item4View.getTextView().setTextSize(14);
        item4View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item4View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageComplaintsInput");
            }
        });

        QMUICommonListItemView item5View = mGroupListView.createItemView(null,"关于我们",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item5View.getTextView().setTextSize(14);
        item5View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item5View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //StephenUtil.startReactNativePage(activity,"PageAboutUs");
            }
        });

        QMUICommonListItemView item6View = mGroupListView.createItemView(null,"设置",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item6View.getTextView().setTextSize(14);
        item6View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item6View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((BaseLocalActivity)activity).checkCurrentIsLogin(true))return;
                //StephenUtil.startReactNativePage(activity,"PageSettingMain",Constants.Req_EnterUserSetting);
            }
        });

        QMUICommonListItemView item7View = mGroupListView.createItemView(null, SharedUtil.contains(activity,Constants.Key_UserInfo) ? "退出登录" : "立即登录",null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON, StephenToolUtils.dip2px(activity, itemHeightDp));
        item7View.getTextView().setTextSize(14);
        item7View.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        section.addItemView(item7View, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedUtil.contains(activity,Constants.Key_UserInfo)){
                    new QMUIDialog.MessageDialogBuilder(activity).setTitle("确认退出?").setMessage("确定将立即退出登录状态!")
                            .addAction("放弃", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                }
                            })
                            .addAction("退出登录", new QMUIDialogAction.ActionListener() {
                                @Override
                                public void onClick(QMUIDialog dialog, int index) {
                                    dialog.dismiss();
                                    StephenUtil.curUserLogoutInfo(activity,true);
                                    createMainGroupList();
                                    resetUserLogoutStatus();
                                }
                            })
                            .create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
                }else{
                    //StephenUtil.startReactNativePage(activity,"PageLogin",Constants.Req_EnterLoginPage);
                }
            }
        });

        section.addTo(mGroupListView);
    }

    private void showMainContent(ResUserInfoBean.Data newLoginUserBean){
        if(null != newLoginUserBean) loginUserBean = newLoginUserBean;
        if(null == loginUserBean) loginUserBean = (null != activity && SharedUtil.contains(activity,Constants.Key_UserInfo)) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(activity, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
        if(null != loginUserBean){
            if(null != newLoginUserBean){
                SharedUtil.putString(activity, Constants.Key_UserInfo, JsonUtil.toJson(newLoginUserBean));
                MainApplication.instance.updateAppHintSetting();
            }// end of if
            if(!TextUtils.isEmpty(loginUserBean.getHeaderIconUrl())){
                OkHttpUtils.get().url(loginUserBean.getHeaderIconUrl()).tag(this).build().execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        headImgV.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pic_default_head, null));
                    }

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        if(null != bitmap){
                            headImgV.setImageBitmap(bitmap);
                        }else{
                            headImgV.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pic_default_head, null));
                        }
                    }
                });
            }else{
                headImgV.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pic_default_head, null));
            }
            vipImgV.setVisibility((1 == loginUserBean.getIsVip()) ? View.VISIBLE : View.GONE);
            userNameT.setText(!TextUtils.isEmpty(loginUserBean.getTrueName()) ? loginUserBean.getTrueName() : (!TextUtils.isEmpty(loginUserBean.getMblNo()) ? loginUserBean.getMblNo() : "--"));
            vipDateT.setText((1 == loginUserBean.getIsVip()) ? (loginUserBean.getVipEndTime() > 0 ? ("VIP会员到期时间:" + DateUtil.convertToTime(loginUserBean.getVipEndTime())) : "--") : "您还不是VIP会员");
        }else{
            createMainGroupList();
            resetUserLogoutStatus();
        }
    }

    private void resetUserLogoutStatus(){
        headImgV.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.pic_default_head, null));
        vipImgV.setVisibility(View.GONE);
        userNameT.setText("登录");
        vipDateT.setText("");
    }

    private void initGetMainInfoData() {
        if(SharedUtil.contains(activity,Constants.Key_UserInfo)){
            reqParamMap.put(Constants.Flag_UseToken,"YES");//需要token
            ApiRequestTool.getInstance().postJson(activity, false, "/customer/detail", reqParamMap, new ApiOnMyRequestListener() {
                @Override
                public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                    super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                    mPullRefreshLayout.finishRefresh();
                    ResUserInfoBean newLoginUserInfo = (ResUserInfoBean)JsonUtil.fromJson(responseJson, ResUserInfoBean.class);
                    if(null != newLoginUserInfo){
                        ResUserInfoBean.Data tmpLoginUserBean = (null != activity && SharedUtil.contains(activity,Constants.Key_UserInfo)) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(activity, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
                        if(null != tmpLoginUserBean && null != newLoginUserInfo.getBody())newLoginUserInfo.getBody().setToken(tmpLoginUserBean.getToken());
                        showMainContent(newLoginUserInfo.getBody());
                    }else{
                        StephenToolUtils.showShortHintInfo(activity,"用户信息转换异常!");
                    }
                }

                @Override
                public void requestCallFail(boolean isError, int errorCode, String errorMsg) {
                    super.requestCallFail(isError, errorCode, errorMsg);
                    mPullRefreshLayout.finishRefresh();
                }
            });
        }else{
            mPullRefreshLayout.finishRefresh();
            showMainContent(null);
        }
    }

    /*private void updateUserInfoData(String headUrl) {
        ApiRequestTool.getInstance().postJson(activity, true, "/customer/update", "headerIconUrl", headUrl, new ApiOnMyRequestListener() {
            @Override
            public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                StephenToolUtils.showShortHintInfo(activity, "更新用户信息成功!");
                if(null != loginUserBean)loginUserBean.setHeaderIconUrl(headUrl);
                showMainContent(loginUserBean);
            }
        });
    }*/

    @Override
    public boolean isSubscribeWebSocket() {
        return true;//需要订阅WebSocketMsg
    }

    @Override
    public void onWebSocketConnected(boolean isFirstConnected) {
        onHeaderRefresh(false);
    }

    /*@Override
    public void onWebSocketMsgArrived(boolean isSuccess, WsResBaseBean wsResBaseBean, JSONObject dataJsonObj, String msgStr) {
        if(null == wsResBaseBean)return;
        if("otherLogin".equals(wsResBaseBean.getMethod()) && -2 == wsResBaseBean.getErrorCode()){//下线
            onHeaderRefresh(false);
        }// end of if
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode){
            case Constants.Req_EnterLoginPage:
                if(SharedUtil.contains(activity, Constants.Key_UserInfo)){
                    createMainGroupList();
                    mPullRefreshLayout.autoRefresh();
                }// end of if
                break;
            case Constants.Req_EnterVipLevel:
            case Constants.Req_EnterUserInfo:
            case Constants.Req_EnterUserSetting:
                if(resultCode == Activity.RESULT_OK)mPullRefreshLayout.autoRefresh();
                break;
            /*case PictureSelector.SELECT_REQUEST_CODE:
                if(null != intent){
                    String picturePath = intent.getStringExtra(PictureSelector.PICTURE_PATH);
                    if(!TextUtils.isEmpty(picturePath)){
                        List<String> filePathList = new ArrayList<>();
                        filePathList.add(picturePath);
                        Map<String, String> paramMap = new HashMap<>();
                        paramMap.put("type", "customer");
                        ApiRequestTool.getInstance().postUpload(activity, true, "/uploadImg", "file", filePathList, paramMap, new ApiOnMyRequestListener() {
                            @Override
                            public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                                super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                                if(dataJsonObj.has("fileName")){
                                    String headUrlStr = null;
                                    try { headUrlStr = dataJsonObj.getString("fileName"); } catch (JSONException e) {}
                                    if(!TextUtils.isEmpty(headUrlStr)){
                                        StephenToolUtils.showShortHintInfo(activity, "上传拍摄/选择图片成功!");
                                        updateUserInfoData(headUrlStr);
                                    }else{
                                        StephenToolUtils.showShortHintInfo(activity, "获取上传返回数据异常!");
                                    }
                                }else{
                                    StephenToolUtils.showShortHintInfo(activity, "上传返回数据异常!");
                                }
                            }
                        });
                    }else{
                        StephenToolUtils.showShortHintInfo(activity, "获取拍摄/选择图片为空!");
                    }
                }else{
                    StephenToolUtils.showShortHintInfo(activity, "未拍摄/选择图片!");
                }
                break;*/
        }//end of switch
    }
}

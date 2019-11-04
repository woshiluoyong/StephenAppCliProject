package com.stephen.cli.project.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.stephen.cli.project.MainApplication;
import com.stephen.cli.project.PushTranslateActivity;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalActivity;
import com.stephen.cli.project.bean.ResBaseBean;
import com.stephen.cli.project.bean.ResConfigBean;
import com.stephen.cli.project.fragment.TabDataFragment;
import com.stephen.cli.project.fragment.TabHomeFragment;
import com.stephen.cli.project.fragment.TabMineFragment;
import com.stephen.cli.project.library.BaseFragment;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.stephen.cli.project.utils.ApiOnMyRequestListener;
import com.stephen.cli.project.utils.ApiRequestTool;
import com.stephen.cli.project.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.content.res.ResourcesCompat;

public class MainActivity extends BaseLocalActivity {
    private QMUIViewPager mainViewPager;
    private QMUITabSegment mainTabSegment;

    private long dataJumpParamId = -1;
    private List<BaseFragment> mainFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainInitMethod(this);
    }

    @Override
    public void setActivityContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initializeActivityFunction() {
        ((MainApplication)getApplication()).setMainActivity(this);//core init

        mainViewPager = findUiViewToInstantiation(R.id.mainViewPager);
        mainTabSegment = findUiViewToInstantiation(R.id.mainTabSegment);

        mainTabSegment.setDefaultNormalColor(ResourcesCompat.getColor(getResources(), R.color.colorTextG3, null));
        mainTabSegment.setDefaultSelectedColor(ResourcesCompat.getColor(getResources(), R.color.colorTheme, null));
        mainTabSegment.setTabTextSize(QMUIDisplayHelper.dp2px(activity, 14));
        mainTabSegment.setOnTabClickListener(new QMUITabSegment.OnTabClickListener() {
            @Override
            public void onTabClick(int index) {
                //StephenUtil.checkPushRegIdReport(activity);
            }
        });

        Drawable homeNormalDrawable = ContextCompat.getDrawable(this, R.drawable.tab_home);
        homeNormalDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));
        Drawable homeSelectDrawable = ContextCompat.getDrawable(this, R.drawable.tab_home_focus);
        homeSelectDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));

        Drawable dataNormalDrawable = ContextCompat.getDrawable(this, R.drawable.tab_order);
        dataNormalDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));
        Drawable dataSelectDrawable = ContextCompat.getDrawable(this, R.drawable.tab_order_focus);
        dataSelectDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));

        Drawable mineNormalDrawable = ContextCompat.getDrawable(this, R.drawable.tab_mine);
        mineNormalDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));
        Drawable mineSelectDrawable = ContextCompat.getDrawable(this, R.drawable.tab_mine_focus);
        mineSelectDrawable.setBounds(0, 0, QMUIDisplayHelper.dp2px(this, 20), QMUIDisplayHelper.dp2px(this, 20));

        QMUITabSegment.Tab tabHome = new QMUITabSegment.Tab(homeNormalDrawable, homeSelectDrawable,"首页",false,false);
        QMUITabSegment.Tab tabData = new QMUITabSegment.Tab(dataNormalDrawable, dataSelectDrawable,"数据",false,false);
        QMUITabSegment.Tab tabMine = new QMUITabSegment.Tab(mineNormalDrawable, mineSelectDrawable,"我的",false,false);
        mainTabSegment.addTab(tabHome).addTab(tabData).addTab(tabMine);

        mainFragments.add(new TabHomeFragment());
        mainFragments.add(new TabDataFragment());
        mainFragments.add(new TabMineFragment());
    }

    @Override
    public void getActivityContentData(Object... objects) {
        mainViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mainFragments.get(position);
            }

            @Override
            public int getCount() {
                return mainFragments.size();
            }
        });
        //mainViewPager.setNoScroll(true); //禁止手动滑动
        mainViewPager.setOffscreenPageLimit(mainFragments.size());
        mainTabSegment.setupWithViewPager(mainViewPager, false);
        mainTabSegment.selectTab(0);

        initGetConfigInfoData();
        //StephenUtil.checkPushRegIdReport(activity);
        receiveFromPush(false, getCurIntent().getStringExtra(PushTranslateActivity.PushParamKey), getCurIntent().getStringExtra(PushTranslateActivity.PushParamVal));
    }

    private void initGetConfigInfoData() {
        ApiRequestTool.getInstance().postJson(activity, false, "/system/config/detail", null, new ApiOnMyRequestListener() {
            @Override
            public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                ResConfigBean resConfigBean = (ResConfigBean) JsonUtil.fromJson(responseJson, ResConfigBean.class);
                if(null != resConfigBean && null != resConfigBean.getBody()) SharedUtil.putString(activity, Constants.Key_ConfigInfo, JsonUtil.toJson(resConfigBean.getBody()));
            }

            @Override
            public boolean isFailShowMsg() {
                return false;//是否显示错误toast
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callFragmentActivityResult(mainFragments, mainTabSegment.getSelectedIndex(), requestCode, resultCode, data);
    }

    public void jumpToTabDataFragment(long paramId){
        resetTabDataParamId();
        this.dataJumpParamId = paramId;
        if (null != mainFragments && mainFragments.size() > 1) {
            BaseFragment baseFragment = mainFragments.get(1);
            if(null != baseFragment){
                mainTabSegment.selectTab(1);
                if(baseFragment.isAdded())baseFragment.getFragmentFilterContent(paramId);
            }// end of if
        }// end of if
    }

    // data fragment初始化完后调用
    public long getTabDataInitParamId(){
        return dataJumpParamId;
    }

    public void resetTabDataParamId(){
        dataJumpParamId = -1;
    }

    //处理推送
    public void receiveFromPush(boolean isRunning, String pushAction, String pushParam){
        System.out.println("===com.stephen.push====接收统一操作的Push参数=====pushAction===>"+pushAction+"======pushParam====>"+pushParam);
        if(!TextUtils.isEmpty(pushAction)){
            if("jumpToMatch".equals(pushAction)){
                try {
                    /*Bundle bundle = new Bundle();
                    bundle.putLong(BaseActivity.ParamBase, Long.parseLong(pushParam));
                    StephenToolUtils.startActivityNoFinish(activity, MatchInfoActivity.class, bundle);*/
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }else if("jumpToNotice".equals(pushAction)){
                //StephenUtil.startReactNativePage(activity,"PageMessageNotify",!isRunning);
            }
        }// end of if
    }
}

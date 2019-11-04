package com.stephen.cli.project.activity;

import android.graphics.Color;
import android.os.Bundle;

import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalActivity;
import com.stephen.cli.project.library.StephenCommonTopTitleView;

public class LoginActivity extends BaseLocalActivity {
    private StephenCommonTopTitleView stephenCommonTopTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainInitMethod(this);
    }

    @Override
    public void setActivityContentView() {
        stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, -30);
        //stephenCommonTopTitleView.setParentFrameBgColorHex("#ff0000");
        stephenCommonTopTitleView.setTitleBgColor(Color.parseColor("#098480"));
        stephenCommonTopTitleView.setTitleCenterText("登录", 16, "#ffffff", false);
        stephenCommonTopTitleView.setTitleLeftIcon(R.drawable.icon_back_btn, stephenCommonTopTitleView.getTitleLeftLp(18,18,5));

        setDefaultTopTitleLeftBack(stephenCommonTopTitleView);
        stephenCommonTopTitleView.injectCommTitleViewToAllViewWithActivity(R.layout.activity_login);
    }
}

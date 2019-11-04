package com.stephen.cli.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.stephen.car.hailing.BuildConfig;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.activity.MainActivity;
import com.stephen.cli.project.base.BaseLocalActivity;
import com.stephen.cli.project.library.StephenCommonTopTitleView;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.Constants;

import android.support.v4.content.res.ResourcesCompat;

public class SplashActivity extends BaseLocalActivity {
    private StephenCommonTopTitleView stephenCommonTopTitleView;
    private TextView versionT;

    public static final boolean isDebug = BuildConfig.DEBUG ? false : false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isTaskRoot()){//为了解决按下home键之后,点击桌面图标从启动页打开
            Intent localIntent = getIntent();
            if(localIntent != null) {
                String str = localIntent.getAction();
                if(localIntent.hasCategory("android.intent.category.LAUNCHER") && !TextUtils.isEmpty(str) && ("android.intent.action.MAIN".equals(str))) {
                    finish();
                }// end of if
            }// end of if
            return;
        }// end of if
        mainInitMethod(this);
    }

    @Override
    public void setActivityContentView() {
        stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, -30);
        stephenCommonTopTitleView.setTitleBgColor(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_transparent, null));

        stephenCommonTopTitleView.injectCommTitleViewToAllViewWithActivity(R.layout.activity_splash);
    }

    @Override
    public void initializeActivityFunction() {
        versionT = findUiViewToInstantiation(R.id.versionT);
    }

    @Override
    public void getActivityContentData(Object... objects) {
        versionT.setText(Constants.TestVersionStr + "版本号：v"+ QMUIPackageHelper.getAppVersion(activity));
        if(isDebug){
            StephenToolUtils.startActivityAndFinish(activity, MainActivity.class);
        }else{
            mainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    StephenToolUtils.startActivityAndFinish(activity, MainActivity.class);
                }
            }, 3 * 1000);
        }
    }
}

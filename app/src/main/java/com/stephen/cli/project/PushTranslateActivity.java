package com.stephen.cli.project;

import android.app.Activity;
import android.os.Bundle;

public class PushTranslateActivity extends Activity {
    public static final String PushParamKey = "PushParamKey", PushParamVal = "PushParamVal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*String pushAction = null, pushParam = null;
        try {
            pushAction = getIntent().getData().getQueryParameter("action");
            pushParam = getIntent().getData().getQueryParameter("param");
        } catch (Exception e){
            e.printStackTrace();
        }
        boolean isRunning = StephenPushUtils.getInstance().isAppRunning(this,"com.stephen.rn.ballscore");//手动写包名,避免获取不对,判断app是否被杀死
        System.out.println("===com.stephen.push====PushTranslateActivity===isRunning===>"+isRunning+"==pushAction==>"+pushAction+"====pushParam===>"+pushParam);
        if(isRunning && null != ((MainApplication)getApplication()).getMainActivity()){
            ((MainApplication)getApplication()).getMainActivity().receiveFromPush(true, pushAction, pushParam);
        }else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(PushParamKey, pushAction);
            intent.putExtra(PushParamVal, pushParam);
            startActivity(intent);
        }
        finish();*/
    }
}
package com.stephen.cli.project.library;

import android.app.Activity;
import android.os.CountDownTimer;

import com.stephen.car.hailing.R;

public class StephenCountDownTimer {
    private Activity activity;
    private CountDownTimer countDownTimer;//倒计时
    private CountDownTimerListener countDownTimerListener;

    public StephenCountDownTimer(Activity activity, CountDownTimerListener countDownTimerListener) {
        this.activity = activity;
        this.countDownTimerListener = countDownTimerListener;
    }

    //开始倒计时
    public void startCountDownTimer(long countDownTime,long interval){
        if (null == countDownTimer){
            countDownTimer = new CountDownTimer(countDownTime,interval) {
                @Override
                public void onTick(long l) {
                    if(null != countDownTimerListener)countDownTimerListener.countDownTimerTick(l);//倒计时跟踪
                }
                @Override
                public void onFinish() {
                    if(null != countDownTimerListener)countDownTimerListener.countDownTimerFinish();
                }
            }.start();
            if(null != countDownTimerListener)countDownTimerListener.countDownTimerStart();//开始倒计时后
        }else{
            StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.count_downing_hint));
        }
    }

    //取消/停止倒计时
    public void cancelCountDownTimer(){
        if(null != countDownTimer){
            countDownTimer.cancel();
            countDownTimer = null;
        }//end of if
        if(null != countDownTimerListener)countDownTimerListener.countDownTimerCancel();
    }

    public interface CountDownTimerListener{
        void countDownTimerStart();
        void countDownTimerTick(long l);//参数是剩余时间(毫秒数),需要自己转格式
        void countDownTimerFinish();
        void countDownTimerCancel();
    }
}

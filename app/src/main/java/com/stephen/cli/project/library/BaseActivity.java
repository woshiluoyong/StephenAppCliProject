package com.stephen.cli.project.library;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.stephen.car.hailing.R;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String ParamIndex = "ParamIndex",ParamBundle = "ParamBundle",ParamBase = "ParamBase",ParamObj1 = "ParamObj1"
            ,ParamObj2 = "ParamObj2",ParamObj3 = "ParamObj3",ParamObj4 = "ParamObj4",ParamObj5 = "ParamObj5",ParamObj6 = "ParamObj6";
    protected BaseActivity activity;
    private long firstKeyTime = 0;//第一次按键时间
    public MainHandler mainHandler;
    public int width = 0, height = 0;
    public InputMethodManager imm;
    private Dialog loadingDialog;
    public static final int msg_showInfo = -1,msg_showLoading = -2,msg_returnJson = -3,msg_closeLoading = -4,msg_showInput = -5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;//宽度
        height = dm.heightPixels;//高度
    }

    public void initStatusBarBg() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public void initStatusBarTextColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    //初始化
    protected void mainInitMethod(BaseActivity activity) {
        this.activity = activity;
        this.mainHandler = new MainHandler(activity.getMainLooper());
        imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        setActivityContentView();
        QMUIStatusBarHelper.translucent(this);
        initializeActivityData();//初始化数据
        initializeActivityFunction();//初始化界面
        getActivityContentData();//获取设置界面数据
    }

    public abstract void setActivityContentView();//会自动调用,设置activity布局显示

    public void initializeActivityData() {
    }//会自动调用,可用可不用,比如getIntent

    //其实是initializeFunction主要完成初始化界面相关的事情
    public abstract void initializeActivityFunction();//会自动调用,初始化布局后初始化控件等

    public abstract void getActivityContentData(Object... objects);//会自动调用

    public void getActivityFilterContent() {}//手动根据需要调用

    // 自定义头默认左边返回操作
    public void setDefaultTopTitleLeftBack(StephenCommonTopTitleView stephenCommonTopTitleView){
        if(null != stephenCommonTopTitleView)stephenCommonTopTitleView.setTitleLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtnClickResponse();
            }
        });
    }

    //判空处理
    public Intent getCurIntent() {
        return (null == getIntent()) ? (new Intent()) : getIntent();
    }

    //切换显示系统输入法
    public void toggleSystemInputMethod() {
        if (null != imm) imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //显示系统输入法
    public void showSystemInputMethod(EditText view) {
        /*if (null != imm && null != view) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }//end of if*/
        if(null != view) QMUIKeyboardHelper.showKeyboard(view, false);
    }

    //隐藏系统输入法
    public void hideSystemInputMethod(View view) {
        /*if (null != imm && null != view && null != view.getWindowToken()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }//end of if*/
        if(null != view) QMUIKeyboardHelper.hideKeyboard(view);
    }

    public void setEditTextFocus(EditText editText){
        if(null == editText)return;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }

    //得到view实例
    public <T extends View> T findUiViewToInstantiation(int viewId) {
        return (T) findViewById(viewId);
    }

    public <T extends View> T findUiViewToInstantiation(View mainV, int viewId) {
        return (T) mainV.findViewById(viewId);
    }

    //设置监听事件,得到id
    public void setUiViewClickListener(int... viewIds) {
        for (int viewId : viewIds) setUiViewClickListener(findUiViewToInstantiation(viewId));
    }

    //设置监听事件,得到view
    public void setUiViewClickListener(View... views) {
        if (null != views && views.length > 0) {
            if (views.length > 1) {
                for (View view : views) if (null != view) view.setOnClickListener(this);
            } else {
                if (null != views[0]) views[0].setOnClickListener(this);
            }
        }//end of if
    }

    @Override
    public void onClick(View view) {
        hideSystemInputMethod(view);
        onViewClick(view, view.getId());
    }

    protected void onViewClick(View view, int viewId){}

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {//点击空白区域,隐藏输入法
            View view = getCurrentFocus();
            if (null != view && StephenToolUtils.isShouldHideKeyboard(view, ev)) hideSystemInputMethod(view);
        }//end of if
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (null != loadingDialog) loadingDialog.dismiss();
            if (null != mainHandler) mainHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (backCheckOperation()) {
                    if (needExitActivity()) {
                        long secondTime = System.currentTimeMillis();
                        if (secondTime - firstKeyTime > 2000) { //如果两次按键时间间隔大于2秒
                            StephenToolUtils.showShortHintInfo(BaseActivity.this, getString(R.string.fast_press_exit));

                            firstKeyTime = secondTime;//更新firstTime
                            return true;
                        } else {//两次按键小于2秒时
                            minimizationProgram();
                        }
                    } else {
                        backToPrevActivity();
                    }
                }//end of if
                break;
        }//end of switch_
        return true;
        //return super.onKeyUp(keyCode, event);
    }

    //需要直接退出的activity
    public boolean needExitActivity() {
        return false;//(null != activity && (activity instanceof MainActivity));
    }

    //返回上一步
    public void backToPrevActivity() {
        //if(!DubKeyboardUtils.isFastClick())return;
        if (null != activity) activity.finish();
        //overridePendingTransition(0, R.anim.slide_translate_left);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    //返回时检查操作
    public boolean backCheckOperation() {
        return true;//default
    }

    //菜单左边按键响应
    public void backBtnClickResponse() {
        if (backCheckOperation()) backToPrevActivity();//default
    }

    //关闭程序之前的操作
    public void beforeFinishProgram() {}

    //最小化程序
    public void minimizationProgram() {
        beforeFinishProgram();
        moveTaskToBack(true);
        //System.exit(0);
    }

    //kill程序
    public void killSelfProgram() {
        minimizationProgram();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    //handler
    public class MainHandler extends Handler {
        public MainHandler(Looper looper) {}

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case msg_showInfo:
                    StephenToolUtils.showShortHintInfo(BaseActivity.this, String.valueOf(msg.obj));
                    break;
                case msg_showLoading:
                    /*String hintStr = "正在加载...";
                    if (null != msg.obj && msg.obj instanceof String) hintStr = msg.obj.toString();
                    if (null != loadingDialog) loadingDialog.dismiss();
                    loadingDialog = DubDialogUtils.loadingDialog(activity, hintStr);
                    try {
                        if (null != loadingDialog) loadingDialog.show();
                    } catch (Exception e) {
                        e.toString();
                    }*/
                    break;
                case msg_closeLoading:
                    if (null != loadingDialog) loadingDialog.dismiss();
                    break;
                default:
                    disposeMainHandlerCallMethod(msg);
                    break;
            }//end of switch
        }
    }

    //显示loading对话框
    public void showLoadingDialog(String hintStr) {
        if (null != mainHandler) {
            Message msg = Message.obtain();
            msg.what = msg_showLoading;
            msg.obj = hintStr;
            mainHandler.sendMessage(msg);
        }//end of if
    }

    //关闭loading对话框
    public void closeLoadingDialog() {
        if (null != mainHandler) mainHandler.sendEmptyMessage(msg_closeLoading);
    }

    public void showShortMsgInfo(String hintStr) {
        if (null != mainHandler) {
            Message msg = Message.obtain();
            msg.what = msg_showInfo;
            msg.obj = hintStr;
            mainHandler.sendMessage(msg);
        }//end of if
    }

    protected void disposeMainHandlerCallMethod(Message msg) {//处理handler消息
        //System.out.println("subclass override method operation!switch_ msg.what");
    }

    public Object disposeRequestResultMethod(String restJson, int resultFlag){
        System.out.println("======="+resultFlag+"========disposeRequestResultMethod====>"+restJson);
        /*if(!TextUtils.isEmpty(restJson) && (-1 != restJson.indexOf("{")) && (-1 != restJson.indexOf("{"))){
            switch(resultFlag){
                case 1:
                    BeanRidingUser beanRidingUser = ((BeanRidingUser)JsonUtil.fromJson(restJson, BeanRidingUser.class));
                    if(validateRequestResultMethod(beanRidingUser))return beanRidingUser;
                    break;
                default:
                    BeanBase beanBase = ((BeanBase)JsonUtil.fromJson(restJson, BeanBase.class));
                    if(validateRequestResultMethod(beanBase))return beanBase;
                    break;
            }//end of switch
        }else{
            //StephenToolUtils.showLongHintInfo(activity, null != e ? e.getMessage() : Config.CommErrorMsg);
            new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("抱歉")
                    .setContentText("获取数据异常,请重试!").show();
        }*/
        return null;
    }

    private boolean validateRequestResultMethod(Object beanObj){
        /*if(null != beanObj){
            BeanBase beanBase = (BeanBase)beanObj;
            if(null != beanBase && beanBase.isSuccess()){
                return true;
            }else{
                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("抱歉")
                        .setContentText("获取数据失败,请重试!").show();
            }
        }else{
            new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE).setTitleText("抱歉")
                    .setContentText("获取数据转换为空,请重试!").show();
        }*/
        return false;
    }
}

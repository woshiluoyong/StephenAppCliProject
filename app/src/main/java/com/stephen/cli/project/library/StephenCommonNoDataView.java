package com.stephen.cli.project.library;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen on 2016/3/3.
 * 动态注入不影响原布局的无内容提示界面
 */
public class StephenCommonNoDataView {
    public static final int defaultCode = -888;
    private Activity activity;
    private View mainContentView,finalCreateView;
    private FrameLayout mainContainerFy;
    private RelativeLayout mainNoDataRy;
    private TextView centerTV, center2TV;
    private int imgWidthDp = 80,imgHeightDp = 80,imgPaddingDp = 20;
    private int containerBgColorVal = Color.parseColor("#100f0f");
    private int mainNoDataBgColorVal = Color.TRANSPARENT;
    private int textSizeSpVal = 15,textColorVal = Color.WHITE;
    private int text2SizeSpVal = 13,text2ColorVal = Color.GRAY;
    private int textPositionRule = RelativeLayout.CENTER_IN_PARENT, textMarginLeftDp = 0
            ,textMarginRightDp = 0,textMarginTopDp = 0,textMarginBottomDp = 0, bothTxtValDp = 10;
    private int noDataMarginLeftPx = 0, noDataMarginRightPx = 0, noDataMarginTopPx = 0, noDataMarginBottomPx = 0, responseClickFlag = defaultCode;
    private String hintStr = "没有数据诶!", hint2Str = null;
    private boolean isInitShowEmpty = false,isResponseClick = false;
    private List<Integer> bottomBtnIds;
    private Button bottomBtn;
    private OnShowHideEventListener onShowHideEventListener;

    public StephenCommonNoDataView(Activity activity) {
        this(activity,false);
    }

    public StephenCommonNoDataView(Activity activity, boolean isResponseClick) {
        this.activity = activity;
        mainContainerFy = new FrameLayout(activity);//主容器
        mainContainerFy.setBackgroundColor(containerBgColorVal);
        //create no data view
        mainNoDataRy = new RelativeLayout(activity);
        mainNoDataRy.setBackgroundColor(mainNoDataBgColorVal);
        centerTV = new TextView(activity);
        centerTV.setId(StephenToolUtils.generateViewId());
        this.isResponseClick = isResponseClick;
    }

    public View initAndInjectNoDataViewForAllViewReturnView(int mainLayoutId,int replaceViewId) {//注入全部,返回view
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null != mainView)return initAndInjectNoDataViewForAllView(mainView.findViewById(replaceViewId));
        return null;
    }

    public void initAndInjectNoDataViewForAllViewWithActivity(int mainLayoutId,int replaceViewId){//注入全部,直接用于activity
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null == mainView){
            activity.setContentView(mainLayoutId);
        }else{
            activity.setContentView(initAndInjectNoDataViewForAllView(mainView.findViewById(replaceViewId)));
        }
    }

    public View initAndInjectNoDataViewForPartViewReturnView(int mainLayoutId,int replaceViewId,int replaceParentViewId,ViewGroup.LayoutParams layoutParams) {//注入部分,可直接用于fragment
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null != mainView)return initAndInjectNoDataViewForPartView(mainView.findViewById(replaceViewId),mainView.findViewById(replaceViewId),mainView.findViewById(replaceParentViewId),layoutParams);
        return null;
    }

    public View initAndInjectNoDataViewForPartViewReturnView(int mainLayoutId,int replaceViewId,ViewGroup.LayoutParams layoutParams) {//注入部分,返回view
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null != mainView)return initAndInjectNoDataViewForPartView(mainView.findViewById(replaceViewId),mainView,layoutParams);
        return null;
    }

    public void initAndInjectNoDataViewForPartViewWithActivity(int mainLayoutId,int replaceViewId,ViewGroup.LayoutParams layoutParams){//注入部分,直接用于activity
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null == mainView){
            activity.setContentView(mainLayoutId);
        }else{
            activity.setContentView(initAndInjectNoDataViewForPartView(mainView.findViewById(replaceViewId),mainView,layoutParams));
        }
    }

    public View initAndInjectNoDataViewForAllView(int mainLayoutId){
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId,null);
        if(null != mainView)return initAndInjectNoDataViewForAllView(mainView);
        return null;
    }

    public View initAndInjectNoDataViewForAllView(View mainContentView){//注入全部,可直接用于fragment
        finalCreateView = initInjectSpecificView(mainContentView,null);
        return finalCreateView;
    }

    //注：replaceParentViewId必须是replaceViewId的最近一级父布局
    public View initAndInjectNoDataViewForPartView(View rootView,int replaceViewId,int replaceParentViewId,ViewGroup.LayoutParams layoutParams) {//注入部分,可直接用于fragment
        return initAndInjectNoDataViewForPartView(rootView,rootView.findViewById(replaceViewId),rootView.findViewById(replaceParentViewId),layoutParams);
    }

    public View initAndInjectNoDataViewForPartView(View replaceView,View rootView,ViewGroup.LayoutParams layoutParams) {//注入部分,可直接用于fragment
        return initAndInjectNoDataViewForPartView(rootView,replaceView,rootView,layoutParams);
    }

    ////////////////////////////////////////
    public View initAndInjectNoDataViewForPartView(View rootView,View replaceView,View replaceParentView,ViewGroup.LayoutParams layoutParams){//注入部分,可直接用于fragment
        ViewGroup parentView = (ViewGroup)replaceParentView;
        int replaceIndex = StephenToolUtils.getChildViewInParentViewIndex(parentView,replaceView.getId());
        if(-1 != replaceIndex){
            parentView.addView(initInjectSpecificView(replaceView,parentView),replaceIndex,layoutParams);
        }else{
            System.out.println("===============>在注入无数据提示有控件没找到,请确认传入的控件层级无误(replaceParentViewId必须是replaceViewId的最近一级父布局)!");
        }
        finalCreateView = rootView;
        return finalCreateView;
    }

    private View initInjectSpecificView(View mainContentView,ViewGroup rootView){
        this.mainContentView = mainContentView;
        if(null != rootView)rootView.removeView(mainContentView);//rootView主要用于只替换布局中部分的显示内容,如果有必须在下步之前移除
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mainContainerFy.addView(mainContentView, null != mainContentView.getLayoutParams() ? mainContentView.getLayoutParams() : flp);

        centerTV.setText(hintStr);
        centerTV.setGravity(Gravity.CENTER);
        centerTV.setTextSize(textSizeSpVal);//sp
        centerTV.setTextColor(textColorVal);
        if (null != centerTV.getPaint()) centerTV.getPaint().setFakeBoldText(true);
        centerTV.setSingleLine(false);
        RelativeLayout.LayoutParams centerTvLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        centerTvLp.addRule(textPositionRule);
        centerTvLp.setMargins(StephenToolUtils.dip2px(activity,textMarginLeftDp),StephenToolUtils.dip2px(activity,textMarginTopDp),StephenToolUtils.dip2px(activity,textMarginRightDp),StephenToolUtils.dip2px(activity,textMarginBottomDp));

        if(TextUtils.isEmpty(hint2Str)){
            mainNoDataRy.addView(centerTV,centerTvLp);
        }else{
            LinearLayout mainLy = new LinearLayout(activity);
            mainLy.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams txt1lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txt1lp.gravity = Gravity.CENTER;
            mainLy.addView(centerTV, txt1lp);

            center2TV = new TextView(activity);
            center2TV.setId(StephenToolUtils.generateViewId());
            center2TV.setText(hint2Str);
            center2TV.setGravity(Gravity.CENTER);
            center2TV.setTextSize(text2SizeSpVal);//sp
            center2TV.setLineSpacing(text2SizeSpVal * 3 / 2, 1);
            center2TV.setTextColor(text2ColorVal);
            center2TV.setSingleLine(false);

            LinearLayout.LayoutParams txt2lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            txt2lp.gravity = Gravity.CENTER;
            txt2lp.topMargin = StephenToolUtils.dip2px(activity, bothTxtValDp);
            txt2lp.leftMargin = StephenToolUtils.dip2px(activity, textMarginLeftDp);
            txt2lp.rightMargin = StephenToolUtils.dip2px(activity, textMarginRightDp);
            mainLy.addView(center2TV, txt2lp);

            mainNoDataRy.addView(mainLy,centerTvLp);
        }

        flp.leftMargin = noDataMarginLeftPx;
        flp.topMargin = noDataMarginTopPx;
        flp.rightMargin = noDataMarginRightPx;
        flp.bottomMargin = noDataMarginBottomPx;
        mainContainerFy.addView(mainNoDataRy, flp);

        if(isInitShowEmpty){//默认
            setNoDataViewInit();
        }else{
            setNoDataViewHide();
        }
        return mainContainerFy;
    }
    ///////////////////////////////

    public void setInitShowEmpty(boolean initShowEmpty) {
        isInitShowEmpty = initShowEmpty;
    }

    //更改提示文本
    public void setCenterTextViewStr(String hintStr){
        setCenterTextViewStr(hintStr,-1,-1,-1,-1,-1);
    }

    public void setCenterTextViewStr(String hintStr,int textPositionRule,int textMarginLeftDp,int textMarginRightDp,int textMarginTopDp,int textMarginBottomDp){
        this.hintStr = hintStr;
        if(textPositionRule >= 0)this.textPositionRule = textPositionRule;
        if(textMarginLeftDp >= 0)this.textMarginLeftDp = textMarginLeftDp;
        if(textMarginRightDp >= 0)this.textMarginRightDp = textMarginRightDp;
        if(textMarginTopDp >= 0)this.textMarginTopDp = textMarginTopDp;
        if(textMarginBottomDp >= 0)this.textMarginBottomDp = textMarginBottomDp;
        if(null != centerTV)centerTV.setText(hintStr);
    }

    public void setCenterText2ViewStr(String hintStr, int bothTxtValDp){
        this.hint2Str = hintStr;
        this.bothTxtValDp = bothTxtValDp;//两个文本间距
        if(null != center2TV)center2TV.setText(hintStr);
    }

    //提示文本上面的提示图
    public void setCenterTextTopHintImg(int imgResId){
        setCenterTextTopHintImg(imgResId,imgWidthDp,imgHeightDp,imgPaddingDp);
    }

    public void setCenterTextTopHintImg(int imgResId,int imgWidthDp,int imgHeightDp,int imgPaddingDp){
        StephenToolUtils.setTextViewAroundDrawable(activity,centerTV,imgResId,imgWidthDp,imgHeightDp,imgPaddingDp, Gravity.TOP);
    }

    public void setCenterTextTopHintImg(Drawable imgDrawable){
        setCenterTextTopHintImg(imgDrawable,imgWidthDp,imgHeightDp,imgPaddingDp);
    }

    public void setCenterTextTopHintImg(Drawable imgDrawable, int imgWidthDp, int imgHeightDp, int imgPaddingDp){
        StephenToolUtils.setTextViewAroundDrawable(activity,centerTV,imgDrawable,imgWidthDp,imgHeightDp,imgPaddingDp, Gravity.TOP);
    }

    //提示文本和提示图一起设置
    public void setCenterTextStrAndHintImg(String hintStr,int imgResId,int imgWidthDp,int imgHeightDp,int imgPaddingDp){
        setCenterTextViewStr(hintStr);
        setCenterTextTopHintImg(imgResId,imgWidthDp,imgHeightDp,imgPaddingDp);
    }

    //提示文本下面的按钮,支持多次设置
    public void setCenterTextBottomBtn(String btnText,int btnSpSize,int btnColor,Drawable btnBgSelector,int btnWidthDp,int btnHeightDp,int btnMarginDp,View.OnClickListener onClickListener){
        if(null != mainNoDataRy){
            bottomBtn = new Button(activity);
            bottomBtn.setId(StephenToolUtils.generateViewId());
            bottomBtn.setGravity(Gravity.CENTER);
            bottomBtn.setText(btnText);
            bottomBtn.setTextSize(btnSpSize);
            bottomBtn.setTextColor(btnColor);
            bottomBtn.setSingleLine(true);
            StephenToolUtils.setBackgroundAllVersion(bottomBtn,btnBgSelector);
            RelativeLayout.LayoutParams bottomBtnLp = new RelativeLayout.LayoutParams(StephenToolUtils.dip2px(activity,btnWidthDp), StephenToolUtils.dip2px(activity,btnHeightDp));
            bottomBtnLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            bottomBtnLp.setMargins(0,StephenToolUtils.dip2px(activity,btnMarginDp),0,0);
            if(null != bottomBtnIds && bottomBtnIds.size() > 0){
                bottomBtnLp.addRule(RelativeLayout.BELOW,bottomBtnIds.get(bottomBtnIds.size()-1));
            }else{
                if(null != centerTV)bottomBtnLp.addRule(RelativeLayout.BELOW,centerTV.getId());
                bottomBtnIds = new ArrayList<Integer>();
            }
            bottomBtnIds.add(bottomBtn.getId());
            mainNoDataRy.addView(bottomBtn,bottomBtnLp);
            bottomBtn.setOnClickListener(onClickListener);
        }//end of if
    }

    public void removeCenterTextBottomBtn() {
        if(null != mainNoDataRy && null != bottomBtn)mainNoDataRy.removeView(bottomBtn);
        if(null != bottomBtnIds && bottomBtnIds.size() <= 1)bottomBtnIds = null;
    }

    public void setCenterTextBottomBtnVisibility(int visibility) {
        if(null != bottomBtn)bottomBtn.setVisibility(visibility);
    }

    public void setCenterTextBottomBtnText(String hintStr){
        if(null != bottomBtn)bottomBtn.setText(hintStr);
    }

    public void setMainContainerBgColorVal(int bgColorVal) {
        this.containerBgColorVal = bgColorVal;
        mainContainerFy.setBackgroundColor(containerBgColorVal);
    }

    public void setMainNoDataBgColorVal(int bgColorVal) {
        this.mainNoDataBgColorVal = bgColorVal;
        mainNoDataRy.setBackgroundColor(mainNoDataBgColorVal);
    }

    public void setCenterTextSizeSpAndColorVal(int textSizeSpVal, int textColorVal) {
        this.textSizeSpVal = textSizeSpVal;
        this.textColorVal = textColorVal;
    }

    public void setCenterText2SizeSpAndColorVal(int textSizeSpVal, int textColorVal) {
        this.text2SizeSpVal = textSizeSpVal;
        this.text2ColorVal = textColorVal;
    }

    public void setNoDataViewShow(){
        setNoDataViewShow(true,hintStr);
    }

    public void setNoDataViewShow(String hintStr){
        setNoDataViewShow(true, hintStr);
    }

    public void setNoDataViewShow(boolean isResponseClick,String hintStr){
        this.isResponseClick = isResponseClick;
        setCenterTextViewStr(hintStr);
        if(null != mainContentView)if(mainContentView.getVisibility() != View.GONE)mainContentView.setVisibility(View.GONE);
        if(null != mainNoDataRy)if(mainNoDataRy.getVisibility() != View.VISIBLE)mainNoDataRy.setVisibility(View.VISIBLE);
        if(null != onShowHideEventListener)onShowHideEventListener.onShowHideEvent(true, false);
    }

    public void setNoDataViewHide(){
        setResponseClick(true);
        if(null != mainNoDataRy)if(mainNoDataRy.getVisibility() != View.GONE)mainNoDataRy.setVisibility(View.GONE);
        if(null != mainContentView)if(mainContentView.getVisibility() != View.VISIBLE)mainContentView.setVisibility(View.VISIBLE);
        if(null != onShowHideEventListener)onShowHideEventListener.onShowHideEvent(false, false);
    }

    public void setNoDataViewInit(){
        setResponseClick(true);
        if(null != mainNoDataRy)mainNoDataRy.setVisibility(View.GONE);
        if(null != mainContentView)mainContentView.setVisibility(View.GONE);
        if(null != onShowHideEventListener)onShowHideEventListener.onShowHideEvent(false, true);
    }

    public void setNoDataUiPadding(int leftDp, int topDp, int rightDp, int bottomDp){
        if(null != mainNoDataRy)mainNoDataRy.setPadding(StephenToolUtils.dip2px(activity, leftDp), StephenToolUtils.dip2px(activity, topDp), StephenToolUtils.dip2px(activity, rightDp), StephenToolUtils.dip2px(activity, bottomDp));
    }

    public void setNoDataUiMargin(int leftDp, int topDp, int rightDp, int bottomDp){
        noDataMarginLeftPx = StephenToolUtils.dip2px(activity, leftDp);
        noDataMarginTopPx = StephenToolUtils.dip2px(activity, topDp);
        noDataMarginRightPx = StephenToolUtils.dip2px(activity, rightDp);
        noDataMarginBottomPx = StephenToolUtils.dip2px(activity, bottomDp);
    }

    public void setResponseClick(boolean responseClick) {
        isResponseClick = responseClick;
    }

    public boolean isResponseClick() {
        return isResponseClick;
    }

    public FrameLayout getMainContainerView() {
        return mainContainerFy;
    }

    public RelativeLayout getNoDataMainView() {
        return mainNoDataRy;
    }

    public View getFinalCreateView(){
        return finalCreateView;
    }

    public void setResponseClickFlag(int responseClickFlag) {
        this.responseClickFlag = responseClickFlag;
    }

    public void setOnNoDataViewClickListener(final OnNoDataViewClickListener onClickListener){
        setOnNoDataViewClickListener(false,onClickListener);
    }

    public void setOnNoDataViewClickListener(final boolean isNoDataViewHide,final OnNoDataViewClickListener onClickListener){
        if(null != mainNoDataRy)mainNoDataRy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isResponseClick()){
                    if(isNoDataViewHide)setNoDataViewHide();
                    if(null != onClickListener)onClickListener.onNoDataViewClick(responseClickFlag);
                }//end of if
            }
        });
    }

    public void setOnShowHideEventDefaulter(final View seizeSeatV){
        setOnShowHideEventListener(new OnShowHideEventListener() {
            @Override
            public void onShowHideEvent(boolean isShow, boolean isInit) {
                if(null != seizeSeatV)seizeSeatV.setVisibility(View.GONE);//因为是占位视图,所以实质是不希望它展示的
            }
        });
    }

    public void setOnShowHideEventListener(OnShowHideEventListener onShowHideEventListener) {
        this.onShowHideEventListener = onShowHideEventListener;
    }

    public interface OnNoDataViewClickListener{
        void onNoDataViewClick(int responseClickFlag);
    }

    public interface OnShowHideEventListener{
        void onShowHideEvent(boolean isShow, boolean isInit);
    }
}
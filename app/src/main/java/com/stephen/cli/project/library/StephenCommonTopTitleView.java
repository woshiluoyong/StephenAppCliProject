package com.stephen.cli.project.library;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StephenCommonTopTitleView {
    public static int DefaultPt = -1;
    private int TitleHeightForDp = 50, ContentHeightForDp = -1, titlePositionDpVal = 0;// titlePositionDpVal为0时Gravity.CENTER,为-x时Gravity.TOP,为+x时Gravity.BOTTOM
    private String TitleBottomLineColorHex = "#20000000", ParentFrameBgColorHex = "#ffffff";//cp
    private Activity activity;
    //private ImageView titleBgImgV;
    private View titleBottomLineV, leftView, centerView, rightView, contentView;
    private FrameLayout titleBgFy, titleLeftFy, titleRightFy, titleCenterFy;
    private boolean contentAboveTitle = true; // 当parentIsFrame时title和content的重叠顺序

    public StephenCommonTopTitleView(Activity activity) {
        this.activity = activity;
        initDefaultCommTitleView();
    }

    public StephenCommonTopTitleView(Activity activity, int titleHeightForDp) {
        this.activity = activity;
        this.TitleHeightForDp = titleHeightForDp;
        initDefaultCommTitleView();
    }

    public StephenCommonTopTitleView(Activity activity, int titleHeightForDp, boolean contentAboveTitle) {
        this.activity = activity;
        this.TitleHeightForDp = titleHeightForDp;
        this.contentAboveTitle = contentAboveTitle;// contentAboveTitle内容和标题在Frame布局上下是否对换
        initDefaultCommTitleView();
    }

    public StephenCommonTopTitleView(Activity activity, int titleHeightForDp, boolean contentAboveTitle, int titlePositionDpVal) {
        this.activity = activity;
        this.TitleHeightForDp = titleHeightForDp;
        this.contentAboveTitle = contentAboveTitle;
        this.titlePositionDpVal = titlePositionDpVal;// titlePositionVal设置请看上面解释
        initDefaultCommTitleView();
    }

    public StephenCommonTopTitleView(Activity activity, int titleHeightForDp, int contentHeightForDp, boolean contentAboveTitle, int titlePositionDpVal) {// titlePositionVal设置请看上面解释
        this.activity = activity;
        this.TitleHeightForDp = titleHeightForDp; // 整个标题栏高度
        this.ContentHeightForDp = contentHeightForDp;// 真正内容区高度(当内容区侵入标题栏区域时就需要注意这个值了,默认为TitleHeightForDp的1/3)
        this.contentAboveTitle = contentAboveTitle;
        this.titlePositionDpVal = titlePositionDpVal;
        initDefaultCommTitleView();
    }

    private void initDefaultCommTitleView() {
        titleBgFy = new FrameLayout(activity);
        titleBgFy.setBackgroundColor(Color.parseColor(StephenToolUtils.MasterColorHex));

        /*titleBgImgV = new ImageView(activity);
        titleBgImgV.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        titleBgFy.addView(titleBgImgV);*/

        FrameLayout.LayoutParams titleFyLp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, ContentHeightForDp > 0 ? ContentHeightForDp : (TitleHeightForDp / 3)));
        //titleFyLp.gravity = Gravity.CENTER;
        titleFyLp.gravity = (0 == titlePositionDpVal ? Gravity.CENTER : (titlePositionDpVal < 0 ? Gravity.TOP : Gravity.BOTTOM));
        if(0 != titlePositionDpVal){
            if(titlePositionDpVal < 0)titleFyLp.topMargin = StephenToolUtils.dip2px(activity, Math.abs(titlePositionDpVal));
            if(titlePositionDpVal > 0)titleFyLp.bottomMargin = StephenToolUtils.dip2px(activity, Math.abs(titlePositionDpVal));
        }//end of if

        titleCenterFy = new FrameLayout(activity);//中间内容父布局
        //titleCenterFy.setBackgroundResource(R.color.qmui_config_color_red);
        titleBgFy.addView(titleCenterFy, titleFyLp);

        RelativeLayout titleCenterRy = new RelativeLayout(activity);//左右按钮父布局
        //titleCenterRy.setBackgroundResource(R.color.qmui_config_color_blue);
        titleCenterRy.setClipChildren(false);//ripple parent set
        titleCenterRy.setPadding(StephenToolUtils.dip2px(activity, TitleHeightForDp / 9), 0, StephenToolUtils.dip2px(activity, TitleHeightForDp / 9), 0);
        titleBgFy.addView(titleCenterRy, titleFyLp);

        titleLeftFy = new FrameLayout(activity);
        RelativeLayout.LayoutParams titleLeftFyLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleLeftFyLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        titleLeftFyLp.addRule(RelativeLayout.CENTER_VERTICAL);
        titleCenterRy.addView(titleLeftFy, titleLeftFyLp);
        titleRightFy = new FrameLayout(activity);
        //titleRightFy.setBackgroundResource(R.color.qmui_config_color_link);
        RelativeLayout.LayoutParams titleRightFyLp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        titleRightFyLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        titleRightFyLp.addRule(RelativeLayout.CENTER_VERTICAL);
        titleCenterRy.addView(titleRightFy, titleRightFyLp);

        titleBottomLineV = new View(activity);
        titleBottomLineV.setBackgroundColor(Color.parseColor(TitleBottomLineColorHex));
        titleBgFy.addView(titleBottomLineV, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, 1), Gravity.BOTTOM));

        setTitleBottomLineVisibility(View.GONE);
        setTitleCenterVisibility(View.GONE);
        setTitleLeftVisibility(View.GONE);
        setTitleRightVisibility(View.GONE);
    }

    public View injectCommTitleViewToAllViewReturnView(int mainLayoutId) {
        return injectCommTitleViewToAllViewReturnView(mainLayoutId, false, -1);
    }

    public View injectCommTitleViewToAllViewReturnView(int mainLayoutId, boolean parentIsFrame, int topPaddingDp) {//注入全部,返回view
        return injectCommTitleViewToAllViewReturnView(LayoutInflater.from(activity).inflate(mainLayoutId, null), parentIsFrame, topPaddingDp);
    }

    public View injectCommTitleViewToAllViewReturnView(View mainView) {
        return injectCommTitleViewToAllViewReturnView(mainView, false, -1);
    }

    public View injectCommTitleViewToAllViewReturnView(View mainView, boolean parentIsFrame, int topPaddingDp) {//注入全部,返回view
        contentView = mainView;
        if (parentIsFrame) {
            FrameLayout mainFy = new FrameLayout(activity);
            mainFy.setBackgroundColor(Color.parseColor(ParentFrameBgColorHex));

            if (topPaddingDp > 0)mainView.setPadding(0, StephenToolUtils.dip2px(activity, topPaddingDp), 0, 0);
            if (contentAboveTitle) {
                mainFy.addView(getTopTitleView(), FrameLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp));
                //mainView.setBackgroundColor(ResourcesCompat.getColor(activity.getResources(), R.color.qmui_config_color_red, null));
                if (null != mainView)mainFy.addView(mainView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            } else {
                if (null != mainView)mainFy.addView(mainView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                mainFy.addView(getTopTitleView(), FrameLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp));
            }
            return mainFy;
        } else {
            LinearLayout mainLy = new LinearLayout(activity);
            mainLy.setBackgroundColor(Color.parseColor(ParentFrameBgColorHex));
            mainLy.setOrientation(LinearLayout.VERTICAL);

            mainLy.addView(getTopTitleView(), LinearLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp));
            if (null != mainView)mainLy.addView(mainView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            return mainLy;
        }
    }

    //注入全部,直接用于activity
    public void injectCommTitleViewToAllViewWithActivity(int mainLayoutId) {
        injectCommTitleViewToAllViewWithActivity(mainLayoutId, false, -1);
    }

    public void injectCommTitleViewToAllViewWithActivity(int mainLayoutId, boolean parentIsFrame, int topPaddingDp) {
        View mainView = LayoutInflater.from(activity).inflate(mainLayoutId, null);
        if (null == mainView) {
            activity.setContentView(mainLayoutId);
        } else {
            injectCommTitleViewToAllViewWithActivity(mainView, parentIsFrame, topPaddingDp);
        }
    }

    //注入全部,直接用于activity
    public void injectCommTitleViewToAllViewWithActivity(View mainView) {
        injectCommTitleViewToAllViewWithActivity(mainView, false, -1);
    }

    public void injectCommTitleViewToAllViewWithActivity(View mainView, boolean parentIsFrame, int topPaddingDp) {
        contentView = mainView;
        if (parentIsFrame) {
            FrameLayout mainFy = new FrameLayout(activity);
            mainFy.setBackgroundColor(Color.parseColor(ParentFrameBgColorHex));

            if (topPaddingDp > 0) mainView.setPadding(0, StephenToolUtils.dip2px(activity, topPaddingDp), 0, 0);
            if (contentAboveTitle) {
                mainFy.addView(getTopTitleView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp)));
                mainFy.addView(mainView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            } else {
                mainFy.addView(mainView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                mainFy.addView(getTopTitleView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp)));
            }
            activity.setContentView(mainFy);
        } else {
            LinearLayout mainLy = new LinearLayout(activity);
            mainLy.setBackgroundColor(Color.parseColor(ParentFrameBgColorHex));
            mainLy.setOrientation(LinearLayout.VERTICAL);

            mainLy.addView(getTopTitleView(), LinearLayout.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, TitleHeightForDp));
            mainLy.addView(mainView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            activity.setContentView(mainLy);
        }
    }

    public void setPaddingTopVal(int ptDpVal) {// 解决沉浸式带来的问题
        titleBgFy.setPadding(0, StephenToolUtils.dip2px(activity, DefaultPt == ptDpVal ? (TitleHeightForDp / 3) : ptDpVal), 0, 0);
    }

    public void setParentFrameBgColorHex(String parentFrameBgColorHex) {
        ParentFrameBgColorHex = parentFrameBgColorHex;
    }

    public FrameLayout getTopTitleView() {
        return titleBgFy;
    }

    public View getContentView() {
        return contentView;
    }

    public int getTitleHeightForDp() {
        return TitleHeightForDp;
    }

    public void setTitleBottomLineVisibility(int visibility) {
        titleBottomLineV.setVisibility(visibility);
    }

    public int getTitleBottomLineVisibility() {
        return titleBottomLineV.getVisibility();
    }

    public View getTitleBottomLine(){
        return titleBottomLineV;
    }

    public void setTitleBgColor(int color) {
        titleBgFy.setBackgroundColor(color);
    }

    public void setTitleBgDrawable(Drawable bgDrawable) {
        StephenToolUtils.setBackgroundAllVersion(titleBgFy, bgDrawable);
        //titleBgImgV.setImageDrawable(bgDrawable);
    }
    ///////////////////////////////////center

    public void setTitleCenterVisibility(int visibility) {
        titleCenterFy.setVisibility(visibility);
    }

    public int getTitleCenterVisibility() {
        return titleCenterFy.getVisibility();
    }

    public void setTitleCenterText(String text) {
        setTitleCenterText(text, null);
    }

    public void setTitleCenterText(String text, FrameLayout.LayoutParams lp) {
        setTitleCenterText(null, text, -1, null, false, lp);
    }

    public void setTitleCenterText(String text, boolean isBold, FrameLayout.LayoutParams lp) {
        setTitleCenterText(null, text, -1, null, isBold, lp);
    }

    public void setTitleCenterText(String text, int textSizeSp, String colorHex, boolean isBold) {
        setTitleCenterText(null, text, textSizeSp, colorHex, isBold, null);
    }
    public void setTitleCenterText(String text, String colorHex, boolean isBold, FrameLayout.LayoutParams lp) {
        setTitleCenterText(null, text, -1, colorHex, isBold, lp);
    }

    public void setTitleCenterText(String text, int textSizeSp, String colorHex, boolean isBold, FrameLayout.LayoutParams lp) {
        setTitleCenterText(null, text, textSizeSp, colorHex, isBold, lp);
    }

    public void setTitleCenterText(TextView textView, String text, int textSizeSp, String colorHex, boolean isBold, FrameLayout.LayoutParams lp) {
        if (null == textView) {
            textView = new TextView(activity);
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
        }//end of if
        if (textSizeSp <= 0) {
            textView.setTextSize(/*TitleHeightForDp / 3*/20);//sp
        } else {
            textView.setTextSize(textSizeSp);//sp
        }
        if (TextUtils.isEmpty(colorHex)) {
            textView.setTextColor(Color.parseColor(StephenToolUtils.MasterFontColorHex));
        } else {
            textView.setTextColor(Color.parseColor(colorHex));
        }
        if (isBold && null != textView.getPaint()) textView.getPaint().setFakeBoldText(true);
        textView.setText(text);
        setTitleCenterView(textView, (null == lp) ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER) : lp);
    }

    public void setTitleCenterIcon(int iconId) {
        setTitleCenterIcon(iconId, new FrameLayout.LayoutParams(StephenToolUtils.dip2px(activity, TitleHeightForDp / 3 * 2), StephenToolUtils.dip2px(activity, TitleHeightForDp / 3 * 2), Gravity.CENTER));
    }

    public void setTitleCenterIcon(int iconId, FrameLayout.LayoutParams flParams) {
        setTitleCenterIcon(null, iconId, flParams);
    }

    public void setTitleCenterIcon(ImageView imageView, int iconId, FrameLayout.LayoutParams flParams) {
        if (null == imageView) imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(iconId));
        setTitleCenterView(imageView, flParams);
    }

    public void setTitleCenterView(View curView, FrameLayout.LayoutParams flParams) {
        if (getTitleCenterVisibility() != View.VISIBLE) setTitleCenterVisibility(View.VISIBLE);
        if (null != centerView) {
            titleCenterFy.removeView(centerView);
            centerView = null;
        }//end of if
        if (null == centerView) {
            centerView = curView;
            centerView.setId(StephenToolUtils.generateViewId());
            titleCenterFy.addView(centerView, flParams);
        }//end of if
    }

    public View getTitleCenterView() {
        return centerView;
    }

    public void setTitleCenterClickListener(View.OnClickListener onClickListener) {
        titleCenterFy.setOnClickListener(onClickListener);
    }
    ///////////////////////////////////left

    public void setTitleLeftVisibility(int visibility) {
        titleLeftFy.setVisibility(visibility);
    }

    public int getTitleLeftVisibility() {
        return titleLeftFy.getVisibility();
    }

    public void setTitleLeftText(String text) {
        setTitleLeftText(text, null);
    }

    public void setTitleLeftText(String text, FrameLayout.LayoutParams lp) {
        setTitleLeftText(null, text, -1, null, lp);
    }

    public void setTitleLeftText(String text, int textSizeSp, String colorHex) {
        setTitleLeftText(null, text, textSizeSp, colorHex, null);
    }

    public void setTitleLeftText(String text, int textSizeSp, String colorHex, FrameLayout.LayoutParams lp) {
        setTitleLeftText(null, text, textSizeSp, colorHex, lp);
    }

    public void setTitleLeftText(TextView textView, String text, int textSizeSp, String colorHex, FrameLayout.LayoutParams lp) {
        if (null == textView) {
            textView = new TextView(activity);
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
        }//end of if
        if (textSizeSp <= 0) {
            textView.setTextSize(TitleHeightForDp / 3);//sp
        } else {
            textView.setTextSize(textSizeSp);//sp
        }
        if (TextUtils.isEmpty(colorHex)) {
            textView.setTextColor(Color.parseColor(StephenToolUtils.MasterFontColorHex));
        } else {
            textView.setTextColor(Color.parseColor(colorHex));
        }
        textView.setText(text);
        setTitleLeftView(textView, (null == lp) ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER) : lp);
    }

    public void setTitleLeftIcon(int iconId) {
        setTitleLeftIcon(iconId, new FrameLayout.LayoutParams(StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 4), StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 4), Gravity.CENTER));
    }

    public void setTitleLeftIcon(int iconId, FrameLayout.LayoutParams flParams) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(iconId));
        setTitleLeftView(imageView, flParams);
    }

    public void setTitleLeftIcon(Bitmap iconBmp, FrameLayout.LayoutParams flParams) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(iconBmp);
        setTitleLeftView(imageView, flParams);
    }

    public void setTitleLeftView(View curView, FrameLayout.LayoutParams flParams) {
        if (getTitleLeftVisibility() != View.VISIBLE) setTitleLeftVisibility(View.VISIBLE);
        if (null != leftView) {
            titleLeftFy.removeView(leftView);
            leftView = null;
        }//end of if
        if (null == leftView) {
            leftView = curView;
            leftView.setId(StephenToolUtils.generateViewId());
            titleLeftFy.addView(leftView, flParams);
        }//end of if
    }

    /*public void setTitleLeftIconAndText(int iconId,String text){
        setTitleLeftIconAndText(iconId,text,null);
    }
    public void setTitleLeftIconAndText(int iconId,String text,String colorHex){
        if(getTitleLeftVisibility() != View.VISIBLE)setTitleLeftVisibility(View.VISIBLE);
        if(null == leftTV){
            leftTV = new StephenHintPointTextView(activity);
            leftTV.setGravity(Gravity.CENTER);
            leftTV.setTextSize(TitleHeightForDp/3);//sp
            if(TextUtils.isEmpty(colorHex)){
                leftTV.setTextColor(Color.parseColor(StephenToolUtils.MasterFontColorHex));
            }else{
                leftTV.setTextColor(Color.parseColor(colorHex));
            }
            leftTV.setSingleLine(true);
            titleLeftFy.addView(leftTV,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.MATCH_PARENT,Gravity.CENTER));
        }//end of if

        leftTV.setText(text);
        Drawable iconDrawable = activity.getResources().getDrawable(iconId);
        iconDrawable.setBounds(0,0, StephenToolUtils.dip2px(activity,TitleHeightForDp/9 * 5), StephenToolUtils.dip2px(activity,TitleHeightForDp/9 * 5));
        leftTV.setCompoundDrawablePadding(-StephenToolUtils.dip2px(activity,TitleHeightForDp/8));
        leftTV.setCompoundDrawables(iconDrawable, null, null, null);
    }*/

    public View getTitleLeftView() {
        return leftView;
    }

    public void setTitleLeftClickListener(View.OnClickListener onClickListener) {
        titleLeftFy.setOnClickListener(onClickListener);
    }

    ///////////////////////////////////right

    public void setTitleRightVisibility(int visibility) {
        titleRightFy.setVisibility(visibility);
    }

    public int getTitleRightVisibility() {
        return titleRightFy.getVisibility();
    }

    public void setTitleRightText(String text,boolean isBold) {
        setTitleRightText(text, isBold,null);
    }

    public void setTitleRightText(String text,boolean isBold, FrameLayout.LayoutParams lp) {
        setTitleRightText(null, text, -1, null,isBold, lp);
    }

    public void setTitleRightText(String text, int textSizeSp, String colorHex, boolean isBold) {
        setTitleRightText(null, text, textSizeSp, colorHex, isBold,null);
    }

    public void setTitleRightText(String text, int textSizeSp, String colorHex, boolean isBold, FrameLayout.LayoutParams lp) {
        setTitleRightText(null, text, textSizeSp, colorHex, isBold,lp);
    }

    public void setTitleRightText(TextView textView, String text, int textSizeSp, String colorHex, boolean isBold, FrameLayout.LayoutParams lp) {
        if (null == textView) {
            textView = new TextView(activity);
            textView.setGravity(Gravity.CENTER);
            textView.setSingleLine(true);
        }//end of if
        if (textSizeSp <= 0) {
            textView.setTextSize(/*TitleHeightForDp / 3*/17);//sp
        } else {
            textView.setTextSize(textSizeSp);//sp
        }
        if (TextUtils.isEmpty(colorHex)) {
            textView.setTextColor(Color.parseColor(StephenToolUtils.MasterFontColorHex));
        } else {
            textView.setTextColor(Color.parseColor(colorHex));
        }
        if (isBold && null != textView.getPaint()) textView.getPaint().setFakeBoldText(true);
        textView.setText(text);
        setTitleRightView(textView, (null == lp) ? new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER) : lp);
    }

    public void setTitleRightIcon(int iconId) {
        setTitleRightIcon(iconId, new FrameLayout.LayoutParams(StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 4), StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 4), Gravity.CENTER));
    }

    public void setTitleRightIcon(int iconId, FrameLayout.LayoutParams flParams) {
        setTitleRightIcon(null, iconId, flParams);
    }

    public void setTitleRightIcon(ImageView imageView, int iconId, FrameLayout.LayoutParams flParams) {
        if (null == imageView) imageView = new ImageView(activity);
        imageView.setImageDrawable(activity.getResources().getDrawable(iconId));
        setTitleRightView(imageView, flParams);
    }

    public void setTitleRightIcon(Bitmap iconBmp) {
        setTitleRightIcon(iconBmp, new FrameLayout.LayoutParams(StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 5), StephenToolUtils.dip2px(activity, TitleHeightForDp / 9 * 5), Gravity.CENTER));
    }

    public void setTitleRightIcon(Bitmap iconBmp, FrameLayout.LayoutParams flParams) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageBitmap(iconBmp);
        setTitleRightView(imageView, flParams);
    }

    public void setTitleRightView(View curView, FrameLayout.LayoutParams flParams) {
        if (getTitleRightVisibility() != View.VISIBLE) setTitleRightVisibility(View.VISIBLE);
        if (null != rightView) {
            titleRightFy.removeView(rightView);
            rightView = null;
        }//end of if
        if (null == rightView) {
            rightView = curView;
            rightView.setId(StephenToolUtils.generateViewId());
            titleRightFy.addView(rightView, flParams);
        }//end of if
    }

    public View getTitleRightView() {
        return rightView;
    }

    public void setTitleRightClickListener(View.OnClickListener onClickListener) {
        titleRightFy.setOnClickListener(onClickListener);
    }

    ////////////////////////////////////////////////////////////////////
    public void setVisibility(int visibility) {
        getTopTitleView().setVisibility(visibility);
    }

    public void setTitleClickListener(View.OnClickListener onClickListener) {
        titleBgFy.setOnClickListener(onClickListener);
    }

    ////////////////////////////////////////////////////////////////////

    public FrameLayout.LayoutParams getTitleLeftLp() {
        return getTitleLeftLp(20, 20, 12);
    }

    public FrameLayout.LayoutParams getTitleLeftLp(int widthForDp, int heightForDp, int leftMarginDp) {
        FrameLayout.LayoutParams leftLp = new FrameLayout.LayoutParams(isSystemLayoutParams(widthForDp) ? widthForDp : StephenToolUtils.dip2px(activity, widthForDp),
                isSystemLayoutParams(heightForDp) ? heightForDp : StephenToolUtils.dip2px(activity, heightForDp));
        if (leftMarginDp > -1) leftLp.leftMargin = StephenToolUtils.dip2px(activity, leftMarginDp);
        leftLp.gravity = Gravity.CENTER_VERTICAL;
        return leftLp;
    }

    public FrameLayout.LayoutParams getTitleRightLp() {
        return getTitleRightLp(20, 20, 12);
    }

    public FrameLayout.LayoutParams getTitleRightLp(int widthForDp, int heightForDp, int rightMarginDp) {
        FrameLayout.LayoutParams rightLp = new FrameLayout.LayoutParams(isSystemLayoutParams(widthForDp) ? widthForDp : StephenToolUtils.dip2px(activity, widthForDp),
                isSystemLayoutParams(heightForDp) ? heightForDp : StephenToolUtils.dip2px(activity, heightForDp));
        if (rightMarginDp > -1)
            rightLp.rightMargin = StephenToolUtils.dip2px(activity, rightMarginDp);
        rightLp.gravity = Gravity.CENTER_VERTICAL;
        return rightLp;
    }

    public FrameLayout.LayoutParams getTitleCenterLp() {
        return getTitleCenterLp(-1, -1, TitleHeightForDp / 6);
    }

    public FrameLayout.LayoutParams getTitleCenterLp(int widthForDp, int heightForDp, int topMarginDp) {
        FrameLayout.LayoutParams centerLp = new FrameLayout.LayoutParams((-1 == widthForDp) ?
                ViewGroup.LayoutParams.WRAP_CONTENT :
                (isSystemLayoutParams(widthForDp) ? widthForDp : StephenToolUtils.dip2px(activity, widthForDp)),
                (-1 == heightForDp) ? ViewGroup.LayoutParams.MATCH_PARENT :
                        (isSystemLayoutParams(heightForDp) ? heightForDp : StephenToolUtils.dip2px(activity, heightForDp)));
        centerLp.gravity = Gravity.CENTER;
        if (topMarginDp > -1) centerLp.topMargin = StephenToolUtils.dip2px(activity, topMarginDp);
        return centerLp;
    }

    private boolean isSystemLayoutParams(int val) {
        return (ViewGroup.LayoutParams.WRAP_CONTENT == val || ViewGroup.LayoutParams.MATCH_PARENT == val || ViewGroup.LayoutParams.FILL_PARENT == val);
    }
}
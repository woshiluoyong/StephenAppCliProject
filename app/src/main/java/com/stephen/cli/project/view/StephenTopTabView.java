package com.stephen.cli.project.view;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stephen.car.hailing.R;
import com.stephen.cli.project.library.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.content.res.ResourcesCompat;

// 首页标题栏tab
public class StephenTopTabView {
    private BaseActivity activity;
    private LinearLayout topTabLy;

    private String[] tabStrAry;
    private List<TextView> tabViewList;
    private int lastSelectIndex = -1, selectIndex = -1;
    private OnChangeSelectCallBack onChangeSelectCallBack;
    private int curFlag = -1;

    public StephenTopTabView(BaseActivity activity, LinearLayout tabLy) {
        this(activity, tabLy, null);
    }

    public StephenTopTabView(BaseActivity activity, LinearLayout tabLy, String[] tabNameAry) {
        this.activity = activity;
        if(null == tabLy){
            topTabLy = new LinearLayout(activity);
            topTabLy.setOrientation(LinearLayout.HORIZONTAL);
            topTabLy.setGravity(Gravity.CENTER);
        }else{
            topTabLy = tabLy;
        }
        this.tabStrAry = tabNameAry;
        this.tabViewList = new ArrayList<>();
    }

    public void createMainTabView(){
        if(null == topTabLy || null == tabStrAry)return;
        curFlag = 1;
        topTabLy.removeAllViews();
        tabViewList.clear();
        for(int i=0;i<tabStrAry.length;i++){
            if(!TextUtils.isEmpty(tabStrAry[i])){
                TextView tabT = new TextView(activity);
                tabT.setText(tabStrAry[i]);
                tabT.setTextSize(12);
                tabT.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabT.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.white, null));
                tabT.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.home_top_tab_unselect_shape, null));
                final int index = i;
                tabT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeSelect(index, false);
                    }
                });
                topTabLy.addView(tabT);
                tabViewList.add(tabT);
            }// end of if
        }// end of for
    }

    public void createMatchTabView(String[] tabNameAry, int tmpFlag){
        if(null != tabNameAry)tabStrAry = tabNameAry;
        if(null == topTabLy || null == tabStrAry)return;
        curFlag = tmpFlag;
        topTabLy.removeAllViews();
        tabViewList.clear();
        for(int i=0;i<tabStrAry.length;i++){
            if(!TextUtils.isEmpty(tabStrAry[i])){
                FrameLayout itemFy = new FrameLayout(activity);

                TextView tabT = new TextView(activity);
                tabT.setText(tabStrAry[i]);
                tabT.setTextSize(14);
                tabT.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                tabT.setTextColor(ResourcesCompat.getColor(activity.getResources(), R.color.white, null));
                tabT.setBackground(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.home_top_tab_unselect_shape, null));
                tabT.setGravity(Gravity.CENTER);
                final int index = i;
                tabT.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changeSelect(index, false);
                    }
                });

                itemFy.addView(tabT, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
                topTabLy.addView(itemFy, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                tabViewList.add(tabT);
            }// end of if
        }// end of for
    }

    public void changeSelect(int selectIndex, boolean isHand){
        if(null == tabViewList || tabViewList.size() <= selectIndex)return;
        this.lastSelectIndex = this.selectIndex;
        this.selectIndex = selectIndex;
        for(int i=0;i<tabViewList.size();i++){
            TextView tabT = tabViewList.get(i);
            if(null != tabT){
                tabT.setTextColor(ResourcesCompat.getColor(activity.getResources(), i == selectIndex ? R.color.colorTheme : R.color.white, null));
                if(1 == curFlag){
                    tabT.setBackground(ResourcesCompat.getDrawable(activity.getResources(), i == selectIndex ? R.drawable.home_top_tab_selected_shape : R.drawable.home_top_tab_unselect_shape, null));
                }else if(2 == curFlag){
                    tabT.setBackground(ResourcesCompat.getDrawable(activity.getResources(), i == selectIndex ? R.drawable.home_top_tab_selected_2shape : R.drawable.home_top_tab_unselect_2shape, null));
                }else if(3 == curFlag){
                    tabT.setBackground(ResourcesCompat.getDrawable(activity.getResources(), i == selectIndex ? R.drawable.home_top_tab_selected_3shape : R.drawable.home_top_tab_unselect_3shape, null));
                }
            }// end of if
        }// end of for
        if(!isHand && null != onChangeSelectCallBack)onChangeSelectCallBack.changeSelectCallBack(selectIndex);
    }

    public void setOnChangeSelectCallBack(OnChangeSelectCallBack onChangeSelectCallBack) {
        this.onChangeSelectCallBack = onChangeSelectCallBack;
    }

    public LinearLayout getTopTabLy() {
        return topTabLy;
    }

    public int getLastSelectIndex() {
        return lastSelectIndex;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public interface OnChangeSelectCallBack{
        void changeSelectCallBack(int selectIndex);
    }
}

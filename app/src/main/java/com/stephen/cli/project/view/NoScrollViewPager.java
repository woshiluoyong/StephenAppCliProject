package com.stephen.cli.project.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.qmuiteam.qmui.widget.QMUIViewPager;

/**
 * 手动控制viewpager是否可以左右滑动
 * 去除滑动动画
 * setNoScroll(boolean noScroll)
 */
public class NoScrollViewPager extends QMUIViewPager {
    private boolean noScroll = false;
 
    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public NoScrollViewPager(Context context) {
        super(context);
    }
 
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
 
    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }
 
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return !noScroll && super.onTouchEvent(arg0);
    }
 
    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return !noScroll && super.onInterceptTouchEvent(arg0);
    }
 
    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
 
    @Override
    public void setCurrentItem(int item) {
        //false 去除滚动效果
        super.setCurrentItem(item,false);
    }
}
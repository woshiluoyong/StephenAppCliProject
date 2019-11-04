package com.stephen.cli.project.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.stephen.car.hailing.R;

/**
 * 支持圆角的TextView
 * Created by stephen on 2017/12/18.
 */
public class DataRoundTextView extends AppCompatTextView {
    private int rtvBorderWidth = 0;

    public DataRoundTextView(Context context) {
        this(context, null);
    }

    public DataRoundTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DataRoundTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWidth(QMUIDisplayHelper.getScreenWidth(context) / 4);
        GradientDrawable gd = new GradientDrawable();//创建drawable
        gd.setColor(ResourcesCompat.getColor(getResources(), R.color.bgGrayColor, null));
        gd.setCornerRadius(10);
        if (rtvBorderWidth > 0)gd.setStroke(rtvBorderWidth, ResourcesCompat.getColor(getResources(), R.color.white, null));
        this.setBackground(gd);
    }

    public void setBackgroungColor(@ColorInt int color) {
        GradientDrawable myGrad = (GradientDrawable) getBackground();
        myGrad.setColor(color);
    }
}

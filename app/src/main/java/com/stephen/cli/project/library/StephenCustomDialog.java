package com.stephen.cli.project.library;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class StephenCustomDialog extends Dialog{
	private Context context;
    
    public StephenCustomDialog(Context context, Integer layout, Integer style) {
    	super(context, style);
    	init(context);
        this.setContentView(layout);
    }
    
    public StephenCustomDialog(Context context, View layout, Integer style) {
        super(context, style);
        init(context);
        this.setContentView(layout);
    }
    
    private void init(Context context) {
    	this.context = context;
        //this.getWindow().getDecorView().setSystemUiVisibility(View.GONE);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setCanceledOnTouchOutside(false);
        //this.setCancelable(false);
	}

    public void setCustomDialogWidthHeightWithDp(Integer widthDp, Integer heightDp) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if(null != widthDp)params.width = StephenToolUtils.dip2px(context,widthDp);
        if(null != heightDp)params.height = StephenToolUtils.dip2px(context,heightDp);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    //本方法支持LinearLayout.LayoutParams这种
    public void setCustomDialogWidthHeightWithPx(Integer widthPx, Integer heightPx) {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if(null != widthPx)params.width = widthPx;
        if(null != heightPx)params.height = heightPx;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void setDialogWidthHeightFillParent() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = LinearLayout.LayoutParams.FILL_PARENT;
        params.height = LinearLayout.LayoutParams.FILL_PARENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
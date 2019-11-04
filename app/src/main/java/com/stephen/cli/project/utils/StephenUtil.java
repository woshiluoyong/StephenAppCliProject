package com.stephen.cli.project.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.bean.ResBaseBean;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.library.BaseActivity;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.stephen.cli.project.library.StephenCustomDialog;
import com.stephen.cli.project.library.StephenToolUtils;

import org.json.JSONObject;

import me.shaohui.shareutil.ShareUtil;
import me.shaohui.shareutil.share.ShareListener;
import me.shaohui.shareutil.share.SharePlatform;

public class StephenUtil {
    public static boolean reportPushRegIdOk = false;

    //获取当前进程名
    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) if (process.pid == pid) processName = process.processName;
        return processName;
    }

    //包名判断是否为主进程
    public static boolean isAppMainProcess(Context context) {
        return context.getPackageName().equals(getCurrentProcessName(context));
    }

    //显示提示说明对话框
    public static void showHintInfoDialog(BaseActivity activity, String title, String content){
        final View contentV = LayoutInflater.from(activity).inflate(R.layout.dialog_question_info_content, null);
        final StephenCustomDialog shareDialog = new StephenCustomDialog(activity, contentV, R.style.stephenCustomDialog);
        shareDialog.setDialogWidthHeightFillParent();
        //shareDialog.setCancelable(false);
        shareDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                QMUIViewHelper.fadeIn(contentV, 500, null,true);
            }
        });
        TextView titleT = activity.findUiViewToInstantiation(contentV, R.id.titleT);
        TextView contentT = activity.findUiViewToInstantiation(contentV, R.id.contentT);
        contentT.setMovementMethod(ScrollingMovementMethod.getInstance());

        titleT.setText(!TextUtils.isEmpty(title) ? title : "标题");
        contentT.setText(!TextUtils.isEmpty(content) ? content : "内容");

        activity.findUiViewToInstantiation(contentV, R.id.closeImgV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });

        shareDialog.show();
    }

    //刷新推送标识
    /*public static void checkPushRegIdReport(BaseActivity activity){
        if(reportPushRegIdOk)return;
        ResUserInfoBean.Data loginUserBean = (null != activity) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(activity, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
        if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getMblNo())){
            StephenPushUtils.getInstance().setStephenPushAlias(loginUserBean.getMblNo());
            String pushIdenti = (1 != StephenPushUtils.getInstance().getStephenPushType() ? loginUserBean.getMblNo() : StephenPushUtils.getInstance().getStephenPushTokenOrRegId());// 推送标识(其他传别名,华为传华为token)
            if(!TextUtils.isEmpty(pushIdenti)){
                Map<String, String> reqParamMap = new HashMap<>();
                reqParamMap.put("pushType", ""+StephenPushUtils.getInstance().getStephenPushType());// 推送通道类型 0:其他（极光） 1:华为 2:小米 3:vivo
                reqParamMap.put("pushIdenti", pushIdenti);
                reqParamMap.put(Constants.Flag_UseToken,"YES");//需要token
                ApiRequestTool.getInstance().postJson(activity, false, "/customer/pushIdenti/update", reqParamMap, new ApiOnMyRequestListener() {
                    @Override
                    public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                        super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                        reportPushRegIdOk = true;
                        System.out.println("===================>推送标识更新成功!");
                    }

                    @Override
                    public boolean isFailShowMsg() {
                        return false;
                    }

                    @Override
                    public void requestCallFail(boolean isError, int errorCode, String errorMsg) {
                        super.requestCallFail(isError, errorCode, errorMsg);
                        reportPushRegIdOk = false;
                        System.out.println("===================>推送标识更新失败!");
                    }
                });
            }else{
                reportPushRegIdOk = false;
                System.out.println("===================>推送标识更新失败,获取为空!");
            }
        }else{
            reportPushRegIdOk = false;
            System.out.println("===================>推送标识更新取消,未登录!");
        }
    }*/

    //用户退出登录
    public static void curUserLogoutInfo(final Context context, boolean isLoading){
        final ResUserInfoBean.Data loginUserBean = (null != context) ? (ResUserInfoBean.Data)JsonUtil.fromJson(SharedUtil.getString(context, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
        if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getToken())){
            if(null != context)SharedUtil.remove(context, Constants.Key_UserInfo);
            ApiRequestTool.getInstance().getJson(context, isLoading, "/auth/logout/"+loginUserBean.getToken()+"/app", null, new ApiOnMyRequestListener() {
                @Override
                public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                    super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
                    coreUserLogoutInfo(context, loginUserBean);
                }

                @Override
                public boolean isFailShowMsg() {
                    return false;
                }

                @Override
                public void requestCallFail(boolean isError, int errorCode, String errorMsg) {
                    super.requestCallFail(isError, errorCode, errorMsg);
                    coreUserLogoutInfo(context, loginUserBean);
                }
            });
        }else{
            if(null != context)SharedUtil.remove(context, Constants.Key_UserInfo);
            coreUserLogoutInfo(context, loginUserBean);
        }
    }
    private static void coreUserLogoutInfo(Context context, ResUserInfoBean.Data loginUserBean){
        //if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getMblNo()))StephenPushUtils.getInstance().delStephenPushAlias(loginUserBean.getMblNo());//删除push别名
        if(null != context)SharedUtil.remove(context, Constants.Key_UserInfo);
    }

    //显示推送弹出对话框
    public static void showPushMatchInfoDialog(BaseActivity activity, int curFlag, long matchId, String... matchInfoAry){
        /*if(null == activity)return;
        if(MainApplication.instance.isPlayVibrator)StephenToolUtils.playVibrator(activity,false);//震动
        if(MainApplication.instance.isPlaySound)StephenToolUtils.playSound(activity, R.raw.sound_whistle);//声音
        if(!MainApplication.instance.isShowDialog)return;
        View pushV = LayoutInflater.from(activity).inflate(R.layout.dialog_push_info_content, null);

        activity.findUiViewToInstantiation(pushV, R.id.contentLy).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Bundle bundle = new Bundle();
                bundle.putLong(BaseActivity.ParamBase, matchId);
                StephenToolUtils.startActivityNoFinish(activity, MatchInfoActivity.class, bundle);
                return false;
            }
        });

        TextView titleT = activity.findUiViewToInstantiation(pushV, R.id.titleT);
        TextView timeT = activity.findUiViewToInstantiation(pushV, R.id.timeT);
        ImageView iconImgV = activity.findUiViewToInstantiation(pushV, R.id.iconImgV);
        TextView hTeamNameT = activity.findUiViewToInstantiation(pushV, R.id.hTeamNameT);
        TextView aTeamNameT = activity.findUiViewToInstantiation(pushV, R.id.aTeamNameT);
        TextView hTeamNumT = activity.findUiViewToInstantiation(pushV, R.id.hTeamNumT);
        TextView aTeamNumT = activity.findUiViewToInstantiation(pushV, R.id.aTeamNumT);

        switch (curFlag) {
            case 0:
                titleT.setText("自选赛事");
                iconImgV.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.icon_push_flag_chart, null));
                break;
            case 1:
                titleT.setText("系统赛事");
                iconImgV.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.icon_push_flag_chart, null));
                break;
            case 2:
                titleT.setText("球队进球");
                iconImgV.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.icon_push_flag_score, null));
                break;
            case 3:
                titleT.setText("球队红牌");
                iconImgV.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.icon_push_flag_penalize, null));
                activity.findUiViewToInstantiation(pushV, R.id.panHintT).setVisibility(View.VISIBLE);
                StephenToolUtils.setTextViewAroundDrawable(activity, hTeamNameT, R.drawable.icon_penalize_red, 9, 16, 2, Gravity.RIGHT);
                break;
        }// end of switch
        timeT.setText(((null != matchInfoAry && matchInfoAry.length > 0) ? matchInfoAry[0] : "-") + "'");
        hTeamNameT.setText((null != matchInfoAry && matchInfoAry.length > 1) ? matchInfoAry[1] : "-");
        aTeamNameT.setText((null != matchInfoAry && matchInfoAry.length > 2) ? matchInfoAry[2] : "-");
        hTeamNumT.setText((null != matchInfoAry && matchInfoAry.length > 3) ? matchInfoAry[3] : "-");
        aTeamNumT.setText((null != matchInfoAry && matchInfoAry.length > 4) ? matchInfoAry[4] : "-");

        MainApplication.instance.showViewToAppTop(pushV);
        activity.mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MainApplication.instance.hideFromAppTopView(pushV);
            }
        }, 5000);*/
    }

    //添加图片水印
    public static Bitmap addSharePicWaterMark(Context context, Bitmap mBitmap) {
        try {
            int mBitmapWidth = mBitmap.getWidth(),mBitmapHeight = mBitmap.getHeight();//获取原始图片与水印图片的宽与高
            Bitmap mNewBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
            Canvas mCanvas = new Canvas(mNewBitmap);
            mCanvas.drawBitmap(mBitmap,0,0,null);//向位图中开始画入MBitmap原始图片

            Bitmap iconBmp = StephenToolUtils.getBitmapFromResId(context, R.drawable.icon_share_water_logo);

            mCanvas.drawBitmap(iconBmp, QMUIDisplayHelper.dp2px(context, 10),mBitmapHeight - iconBmp.getHeight() - QMUIDisplayHelper.dp2px(context, 15), null);
            return mNewBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //分享弹框
    public static void showShareDialog(final Activity activity,final Bitmap picBitmap){
        View shareV = LayoutInflater.from(activity).inflate(R.layout.dialog_share_content, null);
        final StephenCustomDialog shareDialog = new StephenCustomDialog(activity, shareV, R.style.stephenCustomDialog);
        shareDialog.setDialogWidthHeightFillParent();

        shareDialog.show();
        shareDialog.setCanceledOnTouchOutside(true);
        shareV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(null != shareDialog)shareDialog.dismiss();
            }
        });
        shareV.findViewById(R.id.shareLinkLy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenToolUtils.copyTextToSystemClipboard(activity, Constants.ShareLinkUrl);
                if(null != shareDialog)shareDialog.dismiss();
            }
        });
        shareV.findViewById(R.id.shareWbLy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenToolUtils.showShortHintInfo(activity, "调起微博分享中...");
                startShareContent(activity, 2, activity.getString(R.string.app_name), Constants.ShareDescription, picBitmap, Constants.ShareLinkUrl);
                if(null != shareDialog)shareDialog.dismiss();
            }
        });
        shareV.findViewById(R.id.shareWxLy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenToolUtils.showShortHintInfo(activity, "调起微信分享中...");
                startShareContent(activity, 0, activity.getString(R.string.app_name), Constants.ShareDescription, picBitmap, Constants.ShareLinkUrl);
                if(null != shareDialog)shareDialog.dismiss();
            }
        });
        shareV.findViewById(R.id.shareMomentLy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenToolUtils.showShortHintInfo(activity, "调起朋友圈分享中...");
                startShareContent(activity, 1, activity.getString(R.string.app_name), Constants.ShareDescription, picBitmap, Constants.ShareLinkUrl);
                if(null != shareDialog)shareDialog.dismiss();
            }
        });
        shareV.findViewById(R.id.shareQqLy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenToolUtils.showShortHintInfo(activity, "调起QQ分享中...");
                startShareContent(activity, 3, activity.getString(R.string.app_name), Constants.ShareDescription, picBitmap, Constants.ShareLinkUrl);
                if(null != shareDialog)shareDialog.dismiss();

            }
        });
        shareV.findViewById(R.id.cancelT).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareDialog.dismiss();
            }
        });
    }

    //分享
    public static void startShareContent(Activity activity, int shareFlag, String shareTitle, String shareText, Bitmap shareImageBmp, String shareUrl) {// shareFlag:0(微信好友)/1(朋友圈)/2(新浪微博)
        Bitmap logo = StephenToolUtils.getBitmapFromResId(activity, R.mipmap.ic_launcher);
        if (shareFlag == 0) {//微信
            if (!ShareUtil.isInstalled(SharePlatform.WX, activity)) {
                StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.share_wx_not_hint));
                return;
            }//end of if
            if(null != shareImageBmp){
                ShareUtil.shareImage(activity, SharePlatform.WX, shareImageBmp, new MyShareListener(activity));
            }else{
                ShareUtil.shareMedia(activity, SharePlatform.WX, shareTitle, shareText, shareUrl, logo, new MyShareListener(activity));
            }
        } else if (shareFlag == 1) {//朋友圈
            if (!ShareUtil.isInstalled(SharePlatform.WX_TIMELINE, activity)) {
                StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.share_moment_not_hint));
                return;
            }//end of if
            if(null != shareImageBmp){
                ShareUtil.shareImage(activity, SharePlatform.WX_TIMELINE, shareImageBmp, new MyShareListener(activity));
            }else{
                ShareUtil.shareMedia(activity, SharePlatform.WX_TIMELINE, shareTitle, shareText, shareUrl, logo, new MyShareListener(activity));
            }
        } else if (shareFlag == 2) {//微博
            if (!ShareUtil.isInstalled(SharePlatform.WEIBO, activity)) {
                StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.share_sina_not_hint));
                return;
            }//end of if
            if(null != shareImageBmp){
                ShareUtil.shareImage(activity, SharePlatform.WEIBO, shareImageBmp, new MyShareListener(activity));
            }else{
                ShareUtil.shareMedia(activity, SharePlatform.WEIBO, shareText, shareText, shareUrl, logo, new MyShareListener(activity));
            }
        } else if (shareFlag == 3) {//QQ
            if (!ShareUtil.isInstalled(SharePlatform.QQ, activity)) {
                StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.share_qq_not_hint));
                return;
            }//end of if
            if(null != shareImageBmp){
                ShareUtil.shareImage(activity, SharePlatform.QQ, shareImageBmp, new MyShareListener(activity));
            }else{
                ShareUtil.shareMedia(activity, SharePlatform.QQ, shareTitle, shareText, shareUrl, logo, new MyShareListener(activity));
            }
        } else {
            StephenToolUtils.showShortHintInfo(activity, activity.getString(R.string.share_flag_not_match));
        }
    }

    public static class MyShareListener extends ShareListener{
        private Activity activity;

        public MyShareListener(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void shareSuccess() {
            StephenToolUtils.showShortHintInfo(activity, "分享成功!");
        }

        @Override
        public void shareFailure(Exception e) {
            StephenToolUtils.showShortHintInfo(activity, "分享失败!");
        }

        @Override
        public void shareCancel() {
            StephenToolUtils.showShortHintInfo(activity, "分享取消!");
        }
    }
}

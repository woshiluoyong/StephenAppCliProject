package com.stephen.cli.project.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.bean.ResBaseBean;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.library.BaseActivity;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiRequestTool {
    public static final int NoDataForNotwork = -1, NoDataForServer = -2, NoDataForParamError = -3, NoDataForNoLogin = 409, NoDataForLoginTimeOut = 405, NoDataForNeedVip = 407, NoDataForReqFail = -5, NoDataForReqError = -6;
    private static volatile ApiRequestTool singleton;
    private int oncePageNum = 20, onceMaxNum = 1000;//oncePageNum每页分页数量,onceMaxNum页码传递错误默认取数的最大条数
    private QMUITipDialog loadingDialog = null;

    private ApiRequestTool() {}

    public static ApiRequestTool getInstance() {
        if (singleton == null) {
            synchronized (ApiRequestTool.class) {
                if (singleton == null) singleton = new ApiRequestTool();
            }
        }//end of if
        return singleton;
    }

    //一个参数时(Get,带分页)
    public void getJson(Context context, boolean isLoading, String apiUrl, String onceParamName, String onceParamContent, int curPageNum, onRequestListener onRequestListener){
        Map paramMap = new HashMap();
        paramMap.put(onceParamName, onceParamContent);
        paramMap.put("offset", curPageNum <= 0 ? "0" : String.valueOf((curPageNum - 1) * oncePageNum));
        paramMap.put("limit", curPageNum <= 0 ? ""+onceMaxNum : (paramMap.containsKey(Constants.Flag_SelfPageNum) ? paramMap.get(Constants.Flag_SelfPageNum) : String.valueOf(oncePageNum)));
        paramMap.remove(Constants.Flag_SelfPageNum);
        getJson(context, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //一个参数时(Get)
    public void getJson(Context context, boolean isLoading, String apiUrl, String onceParamName, String onceParamContent, onRequestListener onRequestListener){
        Map paramMap = new HashMap();
        paramMap.put(onceParamName, onceParamContent);
        getJson(context, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //多个参数时(Get,带分页)
    public void getJson(Context context, boolean isLoading, String apiUrl, Map<String, String> paramMap, int curPageNum, onRequestListener onRequestListener){
        if(null == paramMap)paramMap = new HashMap<>();
        paramMap.put("offset", curPageNum <= 0 ? "0" : String.valueOf((curPageNum - 1) * oncePageNum));
        paramMap.put("limit", curPageNum <= 0 ? ""+onceMaxNum : (paramMap.containsKey(Constants.Flag_SelfPageNum) ? paramMap.get(Constants.Flag_SelfPageNum) : String.valueOf(oncePageNum)));
        paramMap.remove(Constants.Flag_SelfPageNum);
        getJson(context, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //多个参数时(Get)
    public void getJson(final Context context, final boolean isLoading, final String apiUrl, Map<String, String> paramMap, final onRequestListener onRequestListener){
        if(null == paramMap)paramMap = new HashMap<>();
        paramMap.put("clientFlag", "app");//客户端标识类型 app:手机 pc:电脑
        if(paramMap.containsKey(Constants.Flag_UseToken)){
            ResUserInfoBean.Data loginUserBean = (null != context) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(context, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
            if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getToken()))paramMap.put("token", loginUserBean.getToken());
        }// end of if
        paramMap.remove(Constants.Flag_UseToken);
        String curApiUrl = apiUrl.contains(Constants.ServerProtocol) ? apiUrl : (Constants.DefaultServer + apiUrl);
        GetBuilder okHttpRequestBuilder = OkHttpUtils.get().url(curApiUrl);
        StringBuffer paramStr = new StringBuffer();
        for (String key : paramMap.keySet()){
            paramStr.append("\""+key+"\"").append(":").append("\""+paramMap.get(key)+"\",");
            okHttpRequestBuilder.addParams(key, paramMap.get(key));
        }//end of for
        paramStr.deleteCharAt(paramStr.length()-1);
        System.out.println("========当前请求Get==========>Url:"+curApiUrl+";参数:"+(TextUtils.isEmpty(paramStr.toString()) ? "暂无" : paramStr.toString()));
        okHttpRequestBuilder.build().execute(new myStringCallback(context, isLoading, apiUrl, onRequestListener));
    }

    //一个参数时(Post,带分页)
    public void postJson(BaseActivity activity, boolean isLoading, String apiUrl, String onceParamName, String onceParamContent, int curPageNum, onRequestListener onRequestListener){
        Map paramMap = new HashMap();
        paramMap.put(onceParamName, onceParamContent);
        paramMap.put("offset", curPageNum <= 0 ? "0" : String.valueOf((curPageNum - 1) * oncePageNum));
        paramMap.put("limit", curPageNum <= 0 ? ""+onceMaxNum : (paramMap.containsKey(Constants.Flag_SelfPageNum) ? paramMap.get(Constants.Flag_SelfPageNum) : String.valueOf(oncePageNum)));
        paramMap.remove(Constants.Flag_SelfPageNum);
        postJson(activity, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //一个参数时(Post)
    public void postJson(BaseActivity activity, boolean isLoading, String apiUrl, String onceParamName, String onceParamContent, onRequestListener onRequestListener){
        Map paramMap = new HashMap();
        paramMap.put(onceParamName, onceParamContent);
        postJson(activity, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //多个参数时(Post,带分页)
    public void postJson(final BaseActivity activity, final boolean isLoading, final String apiUrl, Map<String, String> paramMap, int curPageNum, final onRequestListener onRequestListener){
        if(null == paramMap)paramMap = new HashMap<>();
        paramMap.put("offset", curPageNum <= 0 ? "0" : String.valueOf((curPageNum - 1) * oncePageNum));
        paramMap.put("limit", curPageNum <= 0 ? ""+onceMaxNum : (paramMap.containsKey(Constants.Flag_SelfPageNum) ? paramMap.get(Constants.Flag_SelfPageNum) : String.valueOf(oncePageNum)));
        paramMap.remove(Constants.Flag_SelfPageNum);
        postJson(activity, isLoading, apiUrl, paramMap, onRequestListener);
    }

    //多个参数时(Post)
    public void postJson(final BaseActivity activity, final boolean isLoading, final String apiUrl, Map<String, String> paramMap, final onRequestListener onRequestListener){
        if(null == paramMap)paramMap = new HashMap<>();
        StringBuffer paramStr = new StringBuffer();
        paramMap.put("clientFlag", "app");//客户端标识类型 app:手机 pc:电脑
        if(paramMap.containsKey(Constants.Flag_UseToken)){
            ResUserInfoBean.Data loginUserBean = (null != activity) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(activity, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
            if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getToken()))paramMap.put("token", loginUserBean.getToken());
        }// end of if
        paramMap.remove(Constants.Flag_UseToken);
        String curApiUrl = apiUrl.contains(Constants.ServerProtocol) ? apiUrl : (Constants.DefaultServer + apiUrl);
        FormBody.Builder postFormBuilder = new FormBody.Builder();
        for (String key : paramMap.keySet()) {
            postFormBuilder.add(key, paramMap.get(key));
            paramStr.append("\""+key+"\"").append(":").append("\""+paramMap.get(key)+"\",");
        }//end of for
        paramStr.deleteCharAt(paramStr.length()-1);
        System.out.println("========当前请求Post==========>Url:"+curApiUrl+";参数:"+(TextUtils.isEmpty(paramStr.toString()) ? "暂无" : paramStr.toString()));
        //PostStringBuilder postStringBuilder = OkHttpUtils.postString().url(curApiUrl).content(paramStr.toString()).mediaType(MediaType.parse("application/json; charset=utf-8"));
        OkHttpClient client = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.post(postFormBuilder.build());
        builder.url(curApiUrl);
        Request request = builder.build();
        Call call = client.newCall(request);
        call.enqueue(new myOkHttpCallback(activity, isLoading, apiUrl, onRequestListener));
    }

    //上传
    public void postUpload(final Context context, final boolean isLoading, final String apiUrl, String filesKey, List<String> filePathList, Map<String, String> paramMap, final onRequestListener onRequestListener){
        if(null == paramMap)paramMap = new HashMap<>();
        paramMap.put("clientFlag", "app");//客户端标识类型 app:手机 pc:电脑
        StringBuffer paramStr = new StringBuffer();
        HashMap<String, File> fileMap = new HashMap<String,File>();
        for(String filePath : filePathList){
            File file = new File(filePath);
            fileMap.put(filePath.substring(filePath.lastIndexOf("/")+1), file);
        }//end of for
        String curApiUrl = apiUrl.contains(Constants.ServerProtocol) ? apiUrl : (Constants.DefaultServer + apiUrl);
        if(paramMap.containsKey(Constants.Flag_UseToken)){
            ResUserInfoBean.Data loginUserBean = (null != context) ? (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(context, Constants.Key_UserInfo), ResUserInfoBean.Data.class) : null;
            if(null != loginUserBean && !TextUtils.isEmpty(loginUserBean.getToken()))paramMap.put("token", loginUserBean.getToken());
        }// end of if
        paramMap.remove(Constants.Flag_UseToken);
        PostFormBuilder postFormBuilder = OkHttpUtils.post().url(curApiUrl).files(filesKey, fileMap);
        for (String key : paramMap.keySet()){
            paramStr.append("\""+key+"\"").append(":").append("\""+paramMap.get(key)+"\",");
            postFormBuilder.addParams(key, paramMap.get(key));
        }//end of for
        paramStr.deleteCharAt(paramStr.length()-1);
        System.out.println("========当前上传文件请求Post==========>Url:"+curApiUrl+";上传参数:"+(TextUtils.isEmpty(paramStr.toString()) ? "暂无" : paramStr.toString())+";上传文件数:"+filePathList.size());
        postFormBuilder.id(100).tag(context).build().execute(new myStringCallback(context, isLoading, apiUrl, onRequestListener));
    }

    public class myOkHttpCallback implements Callback {
        private BaseActivity activity;
        private boolean isLoading;
        private String apiUrl;
        private onRequestListener onRequestListener;
        private MyHandler myHandler;

        public myOkHttpCallback(BaseActivity activity, boolean isLoading, String apiUrl, ApiRequestTool.onRequestListener onRequestListener) {
            this.activity = activity;
            this.isLoading = isLoading;
            this.apiUrl = apiUrl;
            this.onRequestListener = onRequestListener;
            this.myHandler = new MyHandler();
            myHandler.sendEmptyMessage(555);
            if(isLoading && null != activity)myHandler.sendEmptyMessage(666);
        }

        @Override
        public void onResponse(Call call,final Response response) throws IOException {
            Message msg = Message.obtain();
            try {
                msg.what = 888;
                msg.obj = (null != response && null != response.body()) ? response.body().string() : null;
            } catch (IOException e) {
                msg.what = 999;
                msg.obj = activity.getString(R.string.request_data_error);
                msg.arg1 = NoDataForReqError;
                e.printStackTrace();
            }
            myHandler.sendMessage(msg);
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Message msg = Message.obtain();
            msg.what = 999;
            msg.obj = e.getMessage();
            msg.arg1 = NoDataForReqFail;
            myHandler.sendMessage(msg);
        }

        class MyHandler extends Handler {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 555:
                        try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
                        break;
                    case 666:
                        loadingDialog = new QMUITipDialog.Builder(activity)
                                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                                .setTipWord("正在加载")
                                .create();
                        try{if(null != loadingDialog)loadingDialog.show();}catch(Exception e){}
                        break;
                    case 888:
                        if(isLoading)try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
                        operationSuccess(activity, apiUrl, null != msg.obj ? String.valueOf(msg.obj) : null, onRequestListener);
                        break;
                    case 999:
                        if(isLoading)try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
                        operationFailure(activity, apiUrl, msg.arg1, null != msg.obj ? String.valueOf(msg.obj) : null, onRequestListener);
                        break;
                }// end of switch
            }
        }
    }

    public class myStringCallback extends StringCallback {
        private Context context;
        private boolean isLoading;
        private String apiUrl;
        private onRequestListener onRequestListener;

        public myStringCallback(Context context, boolean isLoading, String apiUrl, ApiRequestTool.onRequestListener onRequestListener) {
            this.context = context;
            this.isLoading = isLoading;
            this.apiUrl = apiUrl;
            this.onRequestListener = onRequestListener;
        }

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
            try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
            if(isLoading && null != context){
                loadingDialog = new QMUITipDialog.Builder(context)
                        .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                        .setTipWord("正在加载")
                        .create();
                try{if(null != loadingDialog)loadingDialog.show();}catch(Exception e){}
            }// end of if
        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
            //if(isLoading)try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            if(isLoading)try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e1){}
            operationFailure(context, apiUrl, NoDataForReqFail, e.getMessage(), onRequestListener);
        }

        @Override
        public void onResponse(String responseJson, int id) {
            if(isLoading)try{if(null != loadingDialog)loadingDialog.dismiss();}catch(Exception e){}
            operationSuccess(context, apiUrl, responseJson, onRequestListener);
        }
    }

    private void operationSuccess(Context context, String apiUrl, String responseJson, onRequestListener onRequestListener){
        System.out.println("=========请求成功==========>("+apiUrl+")"+responseJson);
        if(!TextUtils.isEmpty(responseJson)){
            ResBaseBean resBaseBean = (ResBaseBean)JsonUtil.fromJson(responseJson, ResBaseBean.class);
            if(null != resBaseBean){
                switch (resBaseBean.getErrorCode()){
                    case Constants.SuccessCode://成功
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseJson);
                            if(null != jsonObject && jsonObject.has("body"))jsonObject = jsonObject.getJSONObject("body");
                        } catch (Exception e){}
                        if(null != onRequestListener)onRequestListener.requestCallOk(resBaseBean, jsonObject, responseJson);
                        break;
                    case Constants.LoginOutCode://请先登录
                        if(null != context)StephenUtil.curUserLogoutInfo(context,false);
                    default:
                        if(null != onRequestListener){
                            if(onRequestListener.isFailShowMsg())if(null != context)((BaseActivity)context).showShortMsgInfo(!TextUtils.isEmpty(resBaseBean.getErrMsg()) ? resBaseBean.getErrMsg() : context.getString(R.string.request_fail_info));
                            onRequestListener.requestCallFail(false, resBaseBean.getErrorCode(), !TextUtils.isEmpty(resBaseBean.getErrMsg()) ? resBaseBean.getErrMsg() : context.getString(R.string.request_fail_info));
                        }else{
                            if(null != context)((BaseActivity)context).showShortMsgInfo(!TextUtils.isEmpty(resBaseBean.getErrMsg()) ? resBaseBean.getErrMsg() : context.getString(R.string.request_fail_info));
                        }
                        break;
                }//end of switch
            }else{
                if(null != onRequestListener){
                    if(onRequestListener.isFailShowMsg())if(null != context)((BaseActivity)context).showShortMsgInfo(context.getString(R.string.net_work_connect_fail));
                    onRequestListener.requestCallFail(false, NoDataForReqError, (null != context) ? context.getString(R.string.net_work_connect_fail) : "请求异常啦!");
                }else{
                    if(null != context)((BaseActivity)context).showShortMsgInfo(context.getString(R.string.net_work_connect_fail));
                }
            }
        }else{
            if(null != onRequestListener){
                if(onRequestListener.isFailShowMsg())if(null != context)((BaseActivity)context).showShortMsgInfo(context.getString(R.string.net_work_connect_fail));
                onRequestListener.requestCallFail(false, NoDataForReqError, (null != context) ? context.getString(R.string.net_work_connect_fail) : "请求异常啦!");
            }else{
                if(null != context)((BaseActivity)context).showShortMsgInfo(context.getString(R.string.net_work_connect_fail));
            }
        }
    }

    private void operationFailure(Context context, String apiUrl, int errorCode, String errMsg, onRequestListener onRequestListener){
        System.out.println("=========请求失败==========>("+apiUrl+")"+errMsg);
        errMsg = !TextUtils.isEmpty(errMsg) ? (errMsg.contains("Failed to connect to") ? context.getString(R.string.net_work_connect_fail) : errMsg) : context.getString(R.string.request_exception_info);
        if(null != onRequestListener){
            if(onRequestListener.isFailShowMsg())if(null != context)((BaseActivity)context).showShortMsgInfo(errMsg);
            onRequestListener.requestCallFail(true, errorCode, errMsg);
        }else{
            if(null != context)((BaseActivity)context).showShortMsgInfo(errMsg);
        }
    }

    public interface onRequestListener{
        void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson);
        boolean isFailShowMsg();
        void requestCallFail(boolean isError, int errorCode, String errorMsg);
    }
}

package com.stephen.cli.project.library;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//可以懒加载的Fragment
public abstract class BaseFragment extends Fragment implements View.OnClickListener{
    public static final String ParamIndex = "ParamIndex",ParamBundle = "ParamBundle",ParamBase = "ParamBase",ParamObj1 = "ParamObj1",ParamObj2 = "ParamObj2"
                               ,ParamObj3 = "ParamObj3",ParamObj4 = "ParamObj4",ParamObj5 = "ParamObj5",ParamObj6 = "ParamObj6",ParamObj7 = "ParamObj7";
    protected BaseActivity activity;
    private boolean isInitData = false, isLoadData = false;
    protected View mainV = null;
    private Object tagParam = null;//附带参数

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (BaseActivity)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainV = getFragmentContentView(null != activity ? LayoutInflater.from(activity) : inflater);//初始化
        return mainV;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isInitData){
            initializeFragmentData();
            isCanLoadData();//初始化的时候去加载数据
            isInitData = true;
        }// end of if
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//视图是否已经对用户可见，系统的方法
        super.setUserVisibleHint(isVisibleToUser);
        if(isInitData)isCanLoadData();
    }

    //可以加载数据的条件：1.视图已经初始化 2.视图对用户可见
    private void isCanLoadData() {
        if(getUserVisibleHint() && !isLoadData){
            getFragmentContentData();//lazyLoad
            isLoadData = true;
        }else{
            if(isLoadData)stopLoadContentData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInitData = false;
        isLoadData = false;
    }

    public abstract View getFragmentContentView(LayoutInflater inflater);//获取fragment布局

    public void initializeFragmentData(){}//会自动调用

    public abstract void getFragmentContentData(Object... objects);//会自动调用,当视图初始化并且对用户可见的时候去真正的加载数据

    //当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以调用此方法
    protected void stopLoadContentData() {}

    public void reInitializationRefreshData(Object... objects){//有需要手动调用
        initializeFragmentData();
        getFragmentContentData(objects);
    }

    public void getFragmentFilterContent(Object... objects){}//有需要手动调用--> Fragment里调用

    //得到view实例,用于fragment
    public <T extends View>T findUiViewToInstantiation(int viewId){
        if(null == mainV)return null;
        return (T)mainV.findViewById(viewId);
    }

    public <T extends View>T findUiViewToInstantiation(View mainV, int viewId){
        if(null == mainV)return null;
        return (T)mainV.findViewById(viewId);
    }

    //设置监听事件
    public void setUiViewClickListener(View... views){
        if(null != views && views.length > 0){
            if(views.length > 1){
                for(View view : views)if(null != view)view.setOnClickListener(this);
            }else{
                if(null != views[0])views[0].setOnClickListener(this);
            }
        }//end of if
    }

    //设置监听事件,用于fragment
    public void setUiViewClickListener(int... viewIds){
        if(null == mainV)return;
        if(null != viewIds && viewIds.length > 0){
            if(viewIds.length > 1){
                for(int viewId : viewIds)if(null != findUiViewToInstantiation(viewId))findUiViewToInstantiation(viewId).setOnClickListener(this);
            }else{
                if(null != findUiViewToInstantiation(viewIds[0]))findUiViewToInstantiation(viewIds[0]).setOnClickListener(this);
            }
        }//end of if
    }

    //判空处理
    public Bundle getCurArguments() {
        return (null == getArguments()) ? (new Bundle()) : getArguments();
    }

    //适配器里面传来的,判空处理
    public Bundle getParamBundle(){
        if(null != getArguments()){
            Bundle paramBundle = getArguments().getBundle(ParamBundle);
            if(null != paramBundle)return paramBundle;
        }//end of if
        return new Bundle();
    }

    //返回时检查操作
    public Boolean backCheckOperation() {
        return null;//default
    }

    public void execDisposeMainHandlerCallMethod(Message msg){}

    public Object getTagParam() {
        return tagParam;
    }

    public void setTagParam(Object tagParam) {
        this.tagParam = tagParam;
    }

    @Override
    public void onClick(View view) {
        if(null != activity)activity.hideSystemInputMethod(view);
    }

}

package com.stephen.cli.project.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalFragment;
import com.stephen.cli.project.bean.ResBaseBean;
import com.stephen.cli.project.bean.ResMainBean;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.StephenCommonNoDataView;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.ApiOnMyRequestListener;
import com.stephen.cli.project.utils.ApiRequestTool;
import com.stephen.cli.project.utils.Constants;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.content.res.ResourcesCompat;
import okhttp3.Call;

public class Home1Fragment extends BaseLocalFragment {
    private StephenCommonNoDataView stephenCommonNoDataView;
    private SmartRefreshLayout mPullRefreshLayout;
    private View seizeSeatV;
    private SwipeRecyclerView mainRecyclerV;

    private int curPageNum = 1;
    private BaseQuickAdapter mainAdapter;
    private List<ResMainBean.Data> mainDataList;

    @Override
    public View getFragmentContentView(LayoutInflater inflater) {
        stephenCommonNoDataView = new StephenCommonNoDataView(getActivity());
        stephenCommonNoDataView.setCenterTextViewStr("暂无数据");
        stephenCommonNoDataView.setCenterTextSizeSpAndColorVal(12, ResourcesCompat.getColor(getResources(), R.color.colorTheme, null));
        stephenCommonNoDataView.setCenterTextTopHintImg(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_empty_data, null), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 3));
        stephenCommonNoDataView.setMainNoDataBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_white, null));
        stephenCommonNoDataView.setMainContainerBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_transparent, null));
        stephenCommonNoDataView.setOnShowHideEventDefaulter(seizeSeatV);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return stephenCommonNoDataView.initAndInjectNoDataViewForPartView(LayoutInflater.from(activity).inflate(R.layout.fragment_list_layout, null), R.id.seizeSeatV, R.id.contentFy, lp);
    }

    @Override
    public void initializeFragmentData() {
        initSetStephenCommonNoData(stephenCommonNoDataView);
        mPullRefreshLayout = findUiViewToInstantiation(R.id.pull_to_refresh);
        mainRecyclerV = findUiViewToInstantiation(R.id.mainRecyclerV);
        seizeSeatV = findUiViewToInstantiation(R.id.seizeSeatV);

        mPullRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                onHeaderRefresh(false);
            }
        });
        mPullRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                curPageNum++;
                initGetMainListData(true);
            }
        });

        mainRecyclerV.setLayoutManager(new LinearLayoutManager(activity));
        mainRecyclerV.addItemDecoration(new DefaultItemDecoration(ResourcesCompat.getColor(getResources(), R.color.bgGrayColor, null), ViewGroup.LayoutParams.MATCH_PARENT, StephenToolUtils.dip2px(activity, 1)));

        mainDataList = new ArrayList<>();
        mainRecyclerV.setAdapter(mainAdapter = new BaseQuickAdapter<ResMainBean.Data, BaseViewHolder>(R.layout.item_main_list_data, mainDataList){
            @Override
            protected void convert(@NonNull final BaseViewHolder helper, final ResMainBean.Data item) {
                helper.setText(R.id.indexT, ""+helper.getAdapterPosition());
                helper.setText(R.id.teamT, item.getTitle());
                helper.setText(R.id.sessionT, item.getType());
                helper.setText(R.id.spfT, item.getAuthor());
                helper.setText(R.id.jsT, ""+item.getSongid());
                OkHttpUtils.get().url(item.getPic()).tag(this).build().execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {}

                    @Override
                    public void onResponse(Bitmap bitmap, int id) {
                        if(null != bitmap)helper.setImageBitmap(R.id.iconImgV, bitmap);
                    }
                });
            }
        });
    }

    @Override
    public void getFragmentContentData(Object... objects) {
        reqParamMap.clear();
        onHeaderRefresh(true);
    }

    @Override
    protected void onHeaderRefresh(boolean isHand) {
        super.onHeaderRefresh(isHand);//必须调用
        curPageNum = 1;
        mainDataList.clear();
        if(isHand){
            mPullRefreshLayout.autoRefresh();
        }else{
            initGetMainListData(false);
        }
    }

    private void initGetMainListData(final boolean isLoadMore) {
        reqParamMap.put("name", "壁虎");
        ApiRequestTool.getInstance().getJson(activity, false, "/searchMusic", reqParamMap, curPageNum, new ApiOnMyRequestListener() {
            @Override
            public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
                if(!isLoadMore)mPullRefreshLayout.finishRefresh();
                ResMainBean resMainBean = (ResMainBean) JsonUtil.fromJson(responseJson, ResMainBean.class);
                if (null != resMainBean && null != resMainBean.getResult() && resMainBean.getResult().size() > 0) {
                    mainDataList.addAll(resMainBean.getResult());
                    if(isLoadMore)mPullRefreshLayout.finishLoadMore(0,true, curPageNum == resMainBean.getTotalPage());//判断是否最后一页，目前totalPage是假的，根据真实接口来
                } else {
                    if(isLoadMore)mPullRefreshLayout.finishLoadMore(false);
                }
                if(null != mainAdapter)mainAdapter.notifyDataSetChanged();
                if(null != stephenCommonNoDataTool)stephenCommonNoDataTool.commonNoDataViewShow(mainDataList.size() <= 0, "该公司指数数据为空!", false);
                if(!isLoadMore)mainRecyclerV.scrollToPosition(0);
            }

            @Override
            public void requestCallFail(boolean isError, int errorCode, String errorMsg) {
                super.requestCallFail(isError, errorCode, errorMsg);
                if(!isLoadMore){
                    mPullRefreshLayout.finishRefresh();
                    if(null != mainAdapter)mainAdapter.notifyDataSetChanged();
                }// end of if
                if(isLoadMore)mPullRefreshLayout.finishLoadMore(false);
                if(null != stephenCommonNoDataTool)stephenCommonNoDataTool.commonNoDataViewShow(errorCode,mainDataList.size() <= 0, errorMsg, false);
                if(!isLoadMore)mainRecyclerV.scrollToPosition(0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constants.Req_EnterLoginPage:
                onHeaderRefresh(true);
                break;
        }// end of switch
    }
}

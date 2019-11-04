package com.stephen.cli.project.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalFragment;
import com.stephen.cli.project.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v4.content.res.ResourcesCompat;

public class Home0Fragment extends BaseLocalFragment {
    private QMUITabSegment tabSegment;
    private QMUIViewPager homeViewPager;

    private List<BaseLocalFragment> homeFragments = new ArrayList<>();

    @Override
    public View getFragmentContentView(LayoutInflater inflater) {
        View mainV = inflater.inflate(R.layout.fragment_main_home_tab, null);
        return mainV;
    }

    @Override
    public void initializeFragmentData() {
        tabSegment = findUiViewToInstantiation(R.id.tabSegment);
        homeViewPager = findUiViewToInstantiation(R.id.homeViewPager);

        tabSegment.setDefaultNormalColor(ResourcesCompat.getColor(getResources(), R.color.colorTextG3, null));
        tabSegment.setDefaultSelectedColor(ResourcesCompat.getColor(getResources(), R.color.colorTheme, null));
        tabSegment.setHasIndicator(true);
        tabSegment.setIndicatorPosition(false);
        tabSegment.setIndicatorWidthAdjustContent(true);
        tabSegment.setTabTextSize(QMUIDisplayHelper.dp2px(activity, 16));
        tabSegment.addTab(new QMUITabSegment.Tab("  子Tab一  "));
        tabSegment.addTab(new QMUITabSegment.Tab("  子Tab二  "));
        tabSegment.addTab(new QMUITabSegment.Tab("  子Tab三  "));

        homeFragments.add(new Home0Tab0Fragment());
        homeFragments.add(new Home0Tab1Fragment());
        homeFragments.add(new Home0Tab2Fragment());
    }

    @Override
    public void getFragmentContentData(Object... objects) {
        homeViewPager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return homeFragments.get(position);
            }

            @Override
            public int getCount() {
                return homeFragments.size();
            }
        });
        //homeViewPager.setNoScroll(true); //禁止手动滑动
        tabSegment.setupWithViewPager(homeViewPager, false);
    }

    @Override
    public Map<String, String> getSecondLevelParamMap() {
        if(null == homeFragments || homeFragments.size() <= 0)return null;
        if (homeViewPager.getCurrentItem() >= 0 && homeViewPager.getCurrentItem() < homeFragments.size()) {
            BaseLocalFragment baseFragment = homeFragments.get(homeViewPager.getCurrentItem());
            if(null != baseFragment){
                Map<String, String> reqParamMap = baseFragment.reqParamMap;
                reqParamMap.remove(Constants.Flag_UseToken);
                return reqParamMap;
            }// end of if
        }// end of if
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callFragmentActivityResult(homeFragments, homeViewPager.getCurrentItem(), requestCode, resultCode, data);
    }
}

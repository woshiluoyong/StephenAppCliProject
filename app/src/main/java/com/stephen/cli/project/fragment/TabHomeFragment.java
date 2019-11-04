package com.stephen.cli.project.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qmuiteam.qmui.util.QMUINotchHelper;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.base.BaseLocalFragment;
import com.stephen.cli.project.library.StephenCommonTopTitleView;
import com.stephen.cli.project.utils.StephenUtil;
import com.stephen.cli.project.view.StephenTopTabView;

import java.util.ArrayList;
import java.util.List;

public class TabHomeFragment extends BaseLocalFragment {
    private StephenCommonTopTitleView stephenCommonTopTitleView;
    private StephenTopTabView stephenTopTabView;
    private ViewPager homeViewPager;

    private List<BaseLocalFragment> homeFragments = new ArrayList<>();

    @Override
    public View getFragmentContentView(LayoutInflater inflater) {
        stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, QMUINotchHelper.hasNotch(activity) ? -35 : -30);
        //stephenCommonTopTitleView.setParentFrameBgColorHex("#ff0000");
        stephenCommonTopTitleView.setTitleBgColor(Color.parseColor("#098480"));
        stephenCommonTopTitleView.setTitleCenterText(getString(R.string.app_name), 16, "#ffffff", false);
        stephenCommonTopTitleView.setTitleLeftIcon(R.drawable.icon_share_btn, stephenCommonTopTitleView.getTitleLeftLp(22, 22,5));
        stephenCommonTopTitleView.setTitleRightIcon(R.drawable.icon_share_btn, stephenCommonTopTitleView.getTitleRightLp(22,22,5));

        stephenTopTabView = new StephenTopTabView(activity,null, new String[]{"父Tab一", "父Tab二", "父Tab三", "父Tab四"});
        stephenTopTabView.createMainTabView();
        stephenTopTabView.setOnChangeSelectCallBack(new StephenTopTabView.OnChangeSelectCallBack() {
            @Override
            public void changeSelectCallBack(int selectIndex) {
                if(null != homeViewPager)homeViewPager.setCurrentItem(selectIndex);
                stephenCommonTopTitleView.setTitleLeftVisibility(3 == selectIndex ? View.GONE : View.VISIBLE);
                stephenCommonTopTitleView.setTitleRightVisibility(3 == selectIndex ? View.GONE : View.VISIBLE);
            }
        });
        stephenCommonTopTitleView.setTitleCenterView(stephenTopTabView.getTopTabLy(), stephenCommonTopTitleView.getTitleCenterLp(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT, -1));

        stephenCommonTopTitleView.setTitleLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Bundle bundle = new Bundle();
                switch (homeViewPager.getCurrentItem()){
                    case 0:
                        if(homeFragments.size() > homeViewPager.getCurrentItem()){
                            BaseLocalFragment baseLocalFragment = homeFragments.get(homeViewPager.getCurrentItem());
                            if(null != baseLocalFragment)bundle.putSerializable(ParamBase, (Serializable)baseLocalFragment.getSecondLevelParamMap());
                        }// end of if
                        break;
                    case 1:
                    case 2:
                    case 3:
                        if(homeFragments.size() > homeViewPager.getCurrentItem()){
                            BaseLocalFragment baseLocalFragment = homeFragments.get(homeViewPager.getCurrentItem());
                            if(null != baseLocalFragment)bundle.putSerializable(ParamBase, (Serializable)baseLocalFragment.reqParamMap);
                        }// end of if
                        break;
                }// end of switch
                StephenToolUtils.startActivityNoFinish(activity, LoginActivity.class, bundle, Constants.Req_EnterLoginPage);*/
                StephenUtil.showShareDialog(activity, null);
            }
        });
        stephenCommonTopTitleView.setTitleRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StephenUtil.showShareDialog(activity, null);
            }
        });

        return stephenCommonTopTitleView.injectCommTitleViewToAllViewReturnView(R.layout.fragment_main_home);
    }

    @Override
    public void initializeFragmentData() {
        homeViewPager = findUiViewToInstantiation(R.id.homeViewPager);

        homeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(null != stephenTopTabView)stephenTopTabView.changeSelect(position,true);
                stephenCommonTopTitleView.setTitleLeftVisibility(3 == position ? View.GONE : View.VISIBLE);
                stephenCommonTopTitleView.setTitleRightVisibility(3 == position ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        homeFragments.add(new Home0Fragment());
        homeFragments.add(new Home1Fragment());
        homeFragments.add(new Home2Fragment());
        homeFragments.add(new Home3Fragment());
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
        if(null != stephenTopTabView)stephenTopTabView.changeSelect(0,true);
        if(null != homeViewPager)homeViewPager.setCurrentItem(0);//test
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callFragmentActivityResult(homeFragments, homeViewPager.getCurrentItem(), requestCode, resultCode, data);
    }
}

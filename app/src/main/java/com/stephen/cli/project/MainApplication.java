package com.stephen.cli.project;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.content.res.ResourcesCompat;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.qmuiteam.qmui.util.QMUIDirection;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.stephen.car.hailing.R;
import com.stephen.cli.project.activity.MainActivity;
import com.stephen.cli.project.bean.ResUserInfoBean;
import com.stephen.cli.project.library.JsonUtil;
import com.stephen.cli.project.library.SharedUtil;
import com.stephen.cli.project.library.StephenToolUtils;
import com.stephen.cli.project.utils.Constants;
import com.stephen.cli.project.utils.StephenUtil;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhy.http.okhttp.OkHttpUtils;

import org.json.JSONObject;
import org.lzh.framework.updatepluginlib.UpdateBuilder;
import org.lzh.framework.updatepluginlib.UpdateConfig;
import org.lzh.framework.updatepluginlib.base.FileChecker;
import org.lzh.framework.updatepluginlib.base.UpdateParser;
import org.lzh.framework.updatepluginlib.base.UpdateStrategy;
import org.lzh.framework.updatepluginlib.model.CheckEntity;
import org.lzh.framework.updatepluginlib.model.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import me.shaohui.shareutil.ShareConfig;
import me.shaohui.shareutil.ShareManager;
import okhttp3.OkHttpClient;

public class MainApplication extends Application implements Application.ActivityLifecycleCallbacks {
  public static MainApplication instance;
  public boolean isPlayVibrator = true, isPlaySound = true, isShowDialog = true;//全局设置
  private Map<String, Activity> curActivityMap = new HashMap<>();//打开的界面
  private Activity curTopActivity;//顶层界面
  private MainActivity mainActivity;//主界面

  @Override
  public void onCreate() {
    super.onCreate();
    MainApplication.instance = this;
    if(StephenUtil.isAppMainProcess(getApplicationContext())){
      //MultiDex.install(this);
      OkHttpUtils.initClient(new OkHttpClient.Builder().connectTimeout(10000L, TimeUnit.MILLISECONDS)
              .readTimeout(10000L, TimeUnit.MILLISECONDS).build());
      initUpdatePlugin();
    }// end of if
    if(null != curActivityMap)curActivityMap.clear();
    this.registerActivityLifecycleCallbacks(this);
  }

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

  @Override
  public void onActivityStarted(Activity activity) {}

  @Override
  public void onActivityResumed(Activity activity) {
    if(null == activity)return;
    this.curTopActivity = activity;
    if(null != curActivityMap){
      curActivityMap.put(activity.getClass().getName(), activity);
      System.out.println("==========onActivityResumed======>当前"+curActivityMap.values().size()+"个Activity");
    }// end of if
  }

  @Override
  public void onActivityPaused(Activity activity) {}

  @Override
  public void onActivityStopped(Activity activity) {}

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

  @Override
  public void onActivityDestroyed(Activity activity) {}

  //显示应用内全局view
  public void showViewToAppTop(View view) {
    if(null == view)return;
    try {
      if (this.curTopActivity != null && !this.curTopActivity.isFinishing()) {//Activity不为空并且没有被释放掉
        ((ViewGroup)this.curTopActivity.getWindow().getDecorView()).addView(view);//获取Activity顶层视图,并添加自定义View
        QMUIViewHelper.slideIn(view, 500, null, true, QMUIDirection.BOTTOM_TO_TOP);
      }// end of if
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //隐藏应用内全局view
  public void hideFromAppTopView(View view) {
    if(null == view)return;
    try {
      if(null != curActivityMap && curActivityMap.values().size() > 0) {
        System.out.println("==========hideFromAppTopView======>遍历"+curActivityMap.values().size()+"个Activity");
        for(Activity activity : curActivityMap.values()){
          if (null != activity && !activity.isFinishing()) {//Activity不为空并且没有被释放掉
            ViewGroup root = ((ViewGroup) activity.getWindow().getDecorView());//获取Activity顶层视图
            if (null != root && root.indexOfChild(view) != -1)root.removeView(view);//如果Activity中存在View对象则删除
          }// end of if
        }// end of for
      }// end of if
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public MainActivity getMainActivity() {
    return mainActivity;
  }

  public void setMainActivity(final MainActivity mainActivity) {
    if(null != this.mainActivity)return;
    this.mainActivity = mainActivity;
    //StephenPushUtils.getInstance().initStephenPush(mainActivity.getApplication(),false, BuildConfig.DEBUG);//push
    mainActivity.mainHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        CrashReport.initCrashReport(getApplicationContext(), "4474f97341", false);
        //StephenPushUtils.getInstance().setActivityForBindHw(mainActivity);
        if(!XXPermissions.isHasPermission(mainActivity, Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE, Permission.CAMERA)) {
          XXPermissions.with(mainActivity).permission(Permission.READ_EXTERNAL_STORAGE, Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE, Permission.CAMERA) //不指定权限则自动获取清单中的危险权限
                  .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {}

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                      StephenToolUtils.showShortHintInfo(mainActivity, "为方便您更好的使用本程序请授予相关权限!");
                    }
                  });
        }//end of if
        UpdateBuilder.create().check();
        ShareManager.init(ShareConfig.instance().qqId("xxxx").wxId("xxxx").weiboId("xxxx"));
      }
    }, 1500);
    updateAppHintSetting();
  }

  //更新app全局提醒设置
  public void updateAppHintSetting(){
    ResUserInfoBean.Data loginUserBean = (ResUserInfoBean.Data) JsonUtil.fromJson(SharedUtil.getString(mainActivity, Constants.Key_UserInfo), ResUserInfoBean.Data.class);
    if(null != loginUserBean){
      isPlayVibrator = 1 == loginUserBean.getOpenShock();
      isPlaySound = 1 == loginUserBean.getOpenVoice();
      isShowDialog = 1 == loginUserBean.getOpenPop();
    }// end of if
  }

  //检查更新初始化配置
  public void initUpdatePlugin(){
    CheckEntity checkEntity = new CheckEntity();
    checkEntity.setUrl(Constants.DefaultServer + "/version/upgradApp");
    checkEntity.setMethod("POST");
    Map<String, String> params = new HashMap<>();
    params.put("type", "android");
    checkEntity.setParams(params);
    UpdateConfig.getConfig().setCheckEntity(checkEntity)
            .setUpdateStrategy(new UpdateStrategy() {
              @Override
              public boolean isShowUpdateDialog(Update update) {
                return true;
              }

              @Override
              public boolean isShowDownloadDialog() {
                return true;
              }

              @Override
              public boolean isAutoInstall() {
                return false;
              }
            })
            .setFileChecker(new FileChecker() {
              @Override
              protected boolean onCheckBeforeDownload() throws Exception {
                return false;
              }

              @Override
              protected void onCheckBeforeInstall() throws Exception {}
            })
            .setUpdateParser(new UpdateParser() {
              @Override
              public Update parse(String response) {
                System.out.println("=====UpdatePluginLog===Json==>" + response);
                Update update = new Update();
                try {
                  if(!TextUtils.isEmpty(response)){
                    JSONObject jsonObject = new JSONObject(response);
                    if(null != jsonObject && jsonObject.has("body")){
                      jsonObject = jsonObject.getJSONObject("body");
                      if(null != jsonObject){
                        if(jsonObject.has("versionContent"))update.setUpdateContent(jsonObject.getString("versionContent"));
                        if(jsonObject.has("versionNo"))update.setVersionName(jsonObject.getString("versionNo"));
                        if(jsonObject.has("upgradNumber") && !TextUtils.isEmpty(jsonObject.getString("upgradNumber")))update.setVersionCode(jsonObject.getInt("upgradNumber"));
                        if(jsonObject.has("versionUrl"))update.setUpdateUrl(Constants.DefaultIpPort + "/" + jsonObject.getString("versionUrl"));
                        if(jsonObject.has("isForce") && !TextUtils.isEmpty(jsonObject.getString("isForce")))update.setForced(1 == jsonObject.getInt("isForce"));
                      }// end of if
                    }// end of if
                  }//end of if
                } catch (Exception e) {
                  e.printStackTrace();
                }
                return update;
              }
            });
  }

  static {//使用static代码段可以防止内存泄漏
    SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {//全局设置默认的 Header
      @Override
      public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
        MaterialHeader materialHeader = new MaterialHeader(context);
        materialHeader.setColorSchemeColors(ResourcesCompat.getColor(context.getResources(), R.color.colorTheme, null));
        return materialHeader;
      }
    });
  }
}

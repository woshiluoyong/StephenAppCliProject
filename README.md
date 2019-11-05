# Stephen的Android框架(脚手架)项目说明
> 本项目是一个成熟的商业项目里面提炼出来的框架项目,替换了之前真实项目接口为[测试接口](https://www.apiopen.top/api.html#780786078b064fbaa278719abedba7bd)里面的免费测试接口
> 项目包含我的常用的公共库
> 我封装的聚合push库(极光/华为/小米/vivo/Oppo)
> 封装的websocket库(基于okhttp底层支持实现)
> 集成强大的智能下拉刷新/上拉加载库
> 腾讯的QMUI控件库
> 还有检查更新逻辑及弹框下载
> 集成了分享弹框及逻辑(包含微信/朋友圈/qq/微博)
> 加入了腾讯的bugly线上崩溃库
> 界面方面我添加了一个登录界面做演示用
> 目前整体支持游客模式
> 具体各组件使用逻辑看哈代码就很清晰了,都是解耦了的
> 当然里面的依赖库可根据需要选择使用!

> 本项目为脚手架项目,直接Clone下载熟悉哈清晰完美的代码流程
> 即可在里面新加页面愉快的开发!!相信稍微看哈本库的代码逻辑
> 绝对会是你所需要的

* 惯例，先上图(如果截图抽风没展示出来，请在screenShot文件夹下查看):
![Demo截图](https://github.com/woshiluoyong/StephenAppCliProject/blob/master/screenShot/1.jpg)
![Demo截图](https://github.com/woshiluoyong/StephenAppCliProject/blob/master/screenShot/2.jpg)
![Demo截图](https://github.com/woshiluoyong/StephenAppCliProject/blob/master/screenShot/3.jpg)
![Demo截图](https://github.com/woshiluoyong/StephenAppCliProject/blob/master/screenShot/4.jpg)
![Demo截图](https://github.com/woshiluoyong/StephenAppCliProject/blob/master/screenShot/5.jpg)

* 项目封装了:
* 集成了网络请求OkhttpUtils框架，封装了请求逻辑，请求时是否显示loading可分别在每个请求控制，分页参数也只需传当前第几页即可，请求参数/回参都做了输出，可在logcat里面过滤标识可见，有多个重载方法，力求在网络请求上只做到关心业务逻辑即可，例如项目演示的例子
```
ApiRequestTool.getInstance().postJson(activity, false, "/system/config/detail", null, new ApiOnMyRequestListener() {
    @Override
    public void requestCallOk(ResBaseBean resBaseBean, JSONObject dataJsonObj, String responseJson) {
        super.requestCallOk(resBaseBean, dataJsonObj, responseJson);
        ResConfigBean resConfigBean = (ResConfigBean) JsonUtil.fromJson(responseJson, ResConfigBean.class);
        if(null != resConfigBean && null != resConfigBean.getBody()) SharedUtil.putString(activity, Constants.Key_ConfigInfo, JsonUtil.toJson(resConfigBean.getBody()));
    }

    @Override
    public boolean isFailShowMsg() {
        return false;//是否显示错误toast
    }
});
```
* 内置本人多年总结完善的通用标题头，无侵入式页面布局，动态注入方式，有多个重载方法，及其容易定制和方便使用，例如项目演示的例子
```
stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, -30);
//stephenCommonTopTitleView.setParentFrameBgColorHex("#ff0000");
stephenCommonTopTitleView.setTitleBgColor(Color.parseColor("#098480"));
stephenCommonTopTitleView.setTitleCenterText("我的", 16, "#ffffff", false);
stephenCommonTopTitleView.setTitleRightIcon(R.drawable.icon_share_btn, stephenCommonTopTitleView.getTitleRightLp(22,22,5));

stephenCommonTopTitleView.setTitleRightClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        StephenUtil.showShareDialog(activity, null);
    }
});
stephenCommonTopTitleView.injectCommTitleViewToAllViewReturnView(R.layout.fragment_main_mine);
```
* 内置本人多年总结完善的通用无数据提示，无侵入式页面布局，动态注入方式，有多个重载方法，及其容易定制和方便使用，例如项目演示的例子
```
stephenCommonNoDataView = new StephenCommonNoDataView(getActivity());
stephenCommonNoDataView.setCenterTextViewStr("暂无数据");
stephenCommonNoDataView.setCenterTextSizeSpAndColorVal(12, ResourcesCompat.getColor(getResources(), R.color.colorTheme, null));
stephenCommonNoDataView.setCenterTextTopHintImg(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_empty_data, null), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 3));
stephenCommonNoDataView.setMainNoDataBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_white, null));
stephenCommonNoDataView.setMainContainerBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_transparent, null));
stephenCommonNoDataView.setOnShowHideEventDefaulter(seizeSeatV);
LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
return stephenCommonNoDataView.initAndInjectNoDataViewForPartView(LayoutInflater.from(activity).inflate(R.layout.fragment_list_layout, null), R.id.seizeSeatV, R.id.contentFy, lp);
```
* 通用标题头可和通用无数据提示搭配使用，完美，例如项目演示的例子

```
stephenCommonTopTitleView = new StephenCommonTopTitleView(activity, 70, false, -30);
//stephenCommonTopTitleView.setParentFrameBgColorHex("#ff0000");
stephenCommonTopTitleView.setTitleBgColor(Color.parseColor("#098480"));
stephenCommonTopTitleView.setTitleCenterText("数据", 16, "#ffffff", false);
stephenCommonTopTitleView.setTitleRightIcon(R.drawable.icon_share_btn, stephenCommonTopTitleView.getTitleRightLp(22,22,5));

stephenCommonTopTitleView.setTitleRightClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        StephenUtil.showShareDialog(activity, null);
    }
});

stephenCommonNoDataView = new StephenCommonNoDataView(getActivity());
stephenCommonNoDataView.setCenterTextViewStr("暂无数据");
stephenCommonNoDataView.setCenterTextSizeSpAndColorVal(12, ResourcesCompat.getColor(getResources(), R.color.colorTheme, null));
stephenCommonNoDataView.setCenterTextTopHintImg(ResourcesCompat.getDrawable(getResources(), R.drawable.icon_empty_data, null), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 20), QMUIDisplayHelper.dp2px(activity, 3));
stephenCommonNoDataView.setMainNoDataBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_white, null));
stephenCommonNoDataView.setMainContainerBgColorVal(ResourcesCompat.getColor(getResources(), R.color.qmui_config_color_transparent, null));
stephenCommonNoDataView.setOnShowHideEventDefaulter(seizeSeatV);
LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
return stephenCommonNoDataView.initAndInjectNoDataViewForPartView(stephenCommonTopTitleView.injectCommTitleViewToAllViewReturnView(R.layout.fragment_list_layout), R.id.seizeSeatV, R.id.contentFy, lp);
```

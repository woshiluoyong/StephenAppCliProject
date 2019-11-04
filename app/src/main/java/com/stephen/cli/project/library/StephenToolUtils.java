package com.stephen.cli.project.library;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;
import com.stephen.car.hailing.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StephenToolUtils {
    public static final String SdkFolderName = "StephenCordova", ImageFolderName = "images";
    public static boolean StephenLogSwitch = true;
    public static String MasterColorHex = "#3883C4", MasterFontColorHex = "#ffffff";//字体颜色
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private static QMUITipDialog loadingDialog = null;

    //判断处理log过长问题(Android系统的单条日志打印长度最长:4*1024)
    private static void outputLogCompatibleMaxLength(String logStr, boolean isError) {
        int maxLength = 3000;
        if (logStr.length() > maxLength) {
            for (int i = 0, j = 1; i < logStr.length(); i += maxLength, j++) {
                if (isError) {
                    if (i + maxLength < logStr.length()) {
                        Log.e("dubLog", (1 == j ? "日志过长分段" + j + "==>" : "") + logStr.substring(i, i + maxLength));
                    } else {
                        Log.e("dubLog", (1 == j ? "日志过长分段" + j + "==>" : "") + logStr.substring(i, logStr.length()));
                    }
                } else {
                    if (i + maxLength < logStr.length()) {
                        System.out.println((1 == j ? "日志过长分段" + j + "==>" : "") + logStr.substring(i, i + maxLength));
                    } else {
                        System.out.println((1 == j ? "日志过长分段" + j + "==>" : "") + logStr.substring(i, logStr.length()));
                    }
                }
            }//end of for
        } else {
            if (isError) {
                Log.e("dubLog", logStr);
            } else {
                System.out.println("<<=====" + logStr);
            }
        }
    }

    public static String unicodeStrToUTF8(String src) {
        if (TextUtils.isEmpty(src)) return null;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < src.length(); ) {
            char c = src.charAt(i);
            if (i + 6 < src.length() && c == '\\' && src.charAt(i + 1) == 'u') {
                String hex = src.substring(i + 2, i + 6);
                try {
                    out.append((char) Integer.parseInt(hex, 16));
                } catch (NumberFormatException nfe) {
                    nfe.fillInStackTrace();
                }
                i = i + 6;
            } else {
                out.append(src.charAt(i));
                ++i;
            }
        }///end of if
        return out.toString();

    }

    public static void LogE(String logMsg) {
        LogE(logMsg, true);
    }

    public static void LogE(String logMsg, boolean covertUtf8) {
        if (StephenLogSwitch)
            outputLogCompatibleMaxLength(covertUtf8 ? unicodeStrToUTF8(logMsg) : logMsg, true);
    }

    public static void LogD(String logMsg) {
        LogD(logMsg, true);
    }

    public static void LogD(String logMsg, boolean covertUtf8) {
        if (StephenLogSwitch)
            outputLogCompatibleMaxLength(covertUtf8 ? unicodeStrToUTF8(logMsg) : logMsg, false);
    }


    public static void showLongHintInfo(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showShortHintInfo(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showHintInfoDialog(Context context, String message) {
        showAlertInfoDialog(context, context.getString(R.string.info), message, context.getString(R.string.i_know), null);
    }

    public static void showHintInfoDialog(Context context, String message, View.OnClickListener centerListener) {
        showAlertInfoDialog(context, context.getString(R.string.info), message, context.getString(R.string.i_know), centerListener);
    }

    public static void showHintInfoDialog(Context context, String title, String message, View.OnClickListener leftListener) {
        showAlertInfoDialog(context, title, message, context.getString(R.string.confirm), context.getString(R.string.cancel), leftListener, null);
    }

    public static void showAlertInfoDialog(Context context, String title, String message, String centerStr, View.OnClickListener centerListener) {
        showAlertInfoDialog(context, title, message, null, centerStr, null, null, centerListener, null);
    }

    public static void showAlertInfoDialog(Context context, String title, String message, String leftStr, String rightStr, View.OnClickListener leftListener, View.OnClickListener rightListener) {
        showAlertInfoDialog(context, title, message, leftStr, null, rightStr, leftListener, null, rightListener);
    }

    public static void showAlertInfoDialog(Context context, String title, String message, String leftStr, String centerStr, String rightStr,
                                           final View.OnClickListener leftListener,final View.OnClickListener centerListener,final View.OnClickListener rightListener) {
        try {
            QMUIDialog.MessageDialogBuilder messageDialogBuilder = new QMUIDialog.MessageDialogBuilder(context).setTitle(title).setMessage(message);
            if(!TextUtils.isEmpty(leftStr))messageDialogBuilder.addAction(leftStr, new QMUIDialogAction.ActionListener() {
                        @Override
                        public void onClick(QMUIDialog dialog, int index) {
                            dialog.dismiss();
                            if(null != leftListener)leftListener.onClick(null);
                        }
                    });
            if(!TextUtils.isEmpty(centerStr))messageDialogBuilder.addAction(centerStr, new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                    if(null != centerListener)centerListener.onClick(null);
                }
            });
            if(!TextUtils.isEmpty(rightStr))messageDialogBuilder.addAction(rightStr, new QMUIDialogAction.ActionListener() {
                @Override
                public void onClick(QMUIDialog dialog, int index) {
                    dialog.dismiss();
                    if(null != rightListener)rightListener.onClick(null);
                }
            });
            messageDialogBuilder.create(com.qmuiteam.qmui.R.style.QMUI_Dialog).show();
        } catch (Exception e) {//避免context先销毁再弹框时报错
            e.printStackTrace();
        }
    }

    //显示loading对话框
    public static void showLoadingDialog(Context context, String showStr) {
        try {
            closeLoadingDialog();
            loadingDialog = new QMUITipDialog.Builder(context)
                    .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord(!TextUtils.isEmpty(showStr) ? showStr : "正在加载")
                    .create();
            if(null != loadingDialog)loadingDialog.show();
        } catch (Exception e) {//避免context先销毁再弹框时报错
            e.printStackTrace();
        }
    }

    //关闭loading对话框
    public static void closeLoadingDialog() {
        try {
            if (null != loadingDialog) loadingDialog.dismiss();
            loadingDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //启动activity并关闭
    public static void startActivityAndFinish(Activity activity, Class<?> cls) {
        startActivityAndFinish(activity, cls, null);
    }

    public static void startActivityAndFinish(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        if (null != bundle) intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
        //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //启动activity并关闭且清除顶层
    public static void startActivityAndClearTopFinish(Context context, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (null != bundle) intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void startActivityAndClearTopFinish(Activity activity, Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (null != bundle) intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
        //activity.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //启动activity不关闭
    public static void startActivityNoFinish(Activity activity, Class<?> cls) {
        startActivityNoFinish(activity, cls, null);
    }

    public static void startActivityNoFinish(Activity activity, Class<?> cls, Bundle bundle) {
        startActivityNoFinish(activity, cls, bundle, 520);
    }

    public static void startActivityNoFinish(Activity activity, Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(activity, cls);
        if (null != bundle) intent.putExtras(bundle);
        activity.startActivityForResult(intent, requestCode);//requestCode不能为0和负数,不然onActivityResult不执行
        //activity.overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public static void startActivityNoFinish(Activity activity,Intent intent) {
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    //启动桌面,将本应用位于后台
    public static void setAppToBackground(Activity activity, boolean isUseSystem){
        if(isUseSystem){
            //nonRoot=false时，只有当当前Activity为root activity根Activity时才会把当前task退回到后台。notRoot=true时，不管当前是否是root activity都会把当前task退回到后台
            activity.moveTaskToBack(true);
        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    //判断本应用是否已经位于最前端
    public static boolean isRunningForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfoList = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfoList) {
            if (appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (appProcessInfo.processName.equals(context.getApplicationInfo().processName))return true;
            }//end of if
        }//end of for
        return false;
    }

    //当本应用位于后台时，则将它切换到最前端
    public static void setAppToForeground(Context context) {
        if(!isRunningForeground(context)){
            ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(100);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                    activityManager.moveTaskToFront(taskInfo.id, 0);//(ActivityManager.MOVE_TASK_WITH_HOME)找到本应用的 task，并将它切换到前台
                    break;
                }//end of if
            }//end of for
        }//end of if
    }

    public static boolean isViewFileOrDirExists(String path) {
        File pathFile = new File(path);
        if (null != pathFile) {
            return pathFile.exists();
        } else {
            return false;
        }
    }

    public static void ViewOrCreateDir(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
    }

    public static void ViewOrCreateFile(String filePath) {
        try {
            File dir = new File(filePath);
            ViewOrCreateDir(dir.getParentFile().getAbsolutePath());
            if (!dir.exists()) dir.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean ViewAndDeleteDirOrFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File dir = new File(filePath);
            if (dir.exists()) return dir.delete();
        }//end of if
        return false;
    }

    public static boolean isHaveSDCard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) return true;
        return false;
    }

    public static String getSD_CardRootPath() {
        if (isHaveSDCard()) return Environment.getExternalStorageDirectory().getAbsolutePath();
        return null;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }//end of if
        return inSampleSize;
    }

    public static Bitmap scaleBitmap(byte[] data, int newWidth, int newHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bitmap;
    }

    public static Bitmap scaleBitmap(Resources res, int resId, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        options.inJustDecodeBounds = true; //将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存

        //解析源图片,返回一个 bitmap 对象,当 options.inJustDecodeBounds = true;
        /*禁止为bitmap分配内存，返回值也不再是一个Bitmap对象，而是null。虽然Bitmap是null了，
         但是BitmapFactory.Options的outWidth、outHeight和outMimeType属性都会被赋值。
         这个技巧让我们可以在加载图片之前就获取到图片的长宽值和MIME类型，从而根据情况对图片进行压缩*/
        BitmapFactory.decodeResource(res, resId, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);// 调用上面定义的方法计算inSampleSize值
        options.inJustDecodeBounds = false;// 使用获取到的inSampleSize值再次解析图片
        /*计算完inSampleSize 的合适大小后,需要把 options.inJustDecodeBounds = false;
        然后把再 BitmapFactory.decodeResource(res,resId,options)
        此时  options.inJustDecodeBounds = false; ,BitmapFactory.decodeResource() 方法返回一个bitmap对象给 imageView.setImageBitmap()方法
        从而显示一个合适大小的图片
        */
        return BitmapFactory.decodeResource(res, resId, options);

    }

    public static Bitmap scaleBitmap(Bitmap org, int newWidth, int newHeight) {
        if (null == org) return null;
        Matrix matrix = new Matrix();
        matrix.postScale((float) newWidth / org.getWidth(), (float) newHeight / org.getHeight());
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    //bitmap转为base64
    public static String bitmapToBase64String(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (null != bitmap) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }//end of if
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != baos) {
                    baos.flush();
                    baos.close();
                }//end of if
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    //以最省内存的方式读取本地资源的图片
    public static Bitmap getBitmapFromResId(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    //以最省内存的方式读取本地资源的图片
    public static Bitmap getBitmapForSDCard(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeFile(imagePath, opt);
    }

    //旋转图片存储
    public static void rotatePictureToSave(String picPath){
        Bitmap bmp = StephenToolUtils.getBitmapForSDCard(picPath);
        if(null == bmp)return;
        Matrix matrix = new Matrix();
        matrix.postRotate(readPictureDegree(picPath));
        bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        saveBitmapToFile(bmp,new File(picPath));
    }

    //旋转图片为bitmap
    public static Bitmap rotatePictureToBitmap(String picPath){
        Bitmap bmp = StephenToolUtils.getBitmapForSDCard(picPath);
        if(null != bmp){
            Matrix matrix = new Matrix();
            matrix.postRotate(readPictureDegree(picPath));
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        }
        return null;
    }

    //获取旋转角度
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    //获取当前界面截图
    public static Bitmap getScreenShotBmp(Activity context) {
        try {
            View decorView = context.getWindow().getDecorView();    //获取当前activity所在的最顶层的view--DecorView
            decorView.setDrawingCacheEnabled(true);         //启用绘图缓存
            decorView.buildDrawingCache();                  //强制构建绘图缓存（防止上面启用绘图缓存的操作失败）
            Bitmap bitmap = decorView.getDrawingCache();     //获取绘图缓存中的 bitmap

            int statusBarHeight = getStatusBarHeight(decorView);
            int newBmpHeight = bitmap.getHeight() - statusBarHeight;    //最终截取的图片的高度（取出状态栏之后的高度）
            bitmap = Bitmap.createBitmap(bitmap, 0, statusBarHeight, bitmap.getWidth(), newBmpHeight);
            decorView.setDrawingCacheEnabled(false);    //createBitmap完成之后一定要置为false，否则短时间内多次截图时内容不会变化！
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取状态栏的高度--方式1
    public static int getStatusBarHeight(Activity context) {
        int statusBarHeight = 0;
        int resourceID = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceID != 0)statusBarHeight = context.getResources().getDimensionPixelSize(resourceID);
        return statusBarHeight;
    }

    //获取状态栏高度方式2(decorView 要获取状态栏高度的页面所在的顶层布局)
    public static int getStatusBarHeight(View decorView) {
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    //把bitmap 保存为png
    public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
        if (bitmap == null) return false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    //以最省内存的方式读取本地资源的图片
    public static Bitmap getBitmapForSDCardOption(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) return null;
        options.inSampleSize = calculateInSampleSize(options, 600, (int) (1 * 1024 * 1024));
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    //以最省内存的方式读取本地资源的图片
    public static Bitmap getBitmapForSDCardOptionUrl(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        return BitmapFactory.decodeFile(imagePath,options);
    }

    public static boolean copyAssetFiles(Context context, String assetFolderName, File outDir) {
        try {
            AssetManager assetManager = context.getAssets();
            String[] fileAry = assetManager.list(assetFolderName);
            if (null != fileAry && fileAry.length > 0)
                for (String file : fileAry) copyAssetFile(context, assetFolderName, file, outDir);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static File copyAssetFile(Context context, String assetFilePath, String assetFileName, File outDir) {
        try {
            File outFile = new File(outDir, assetFileName);
            if (!outFile.exists()) {
                if (!outDir.exists()) outDir.mkdirs();
                InputStream is;
                AssetManager assetManager = context.getAssets();
                if (TextUtils.isEmpty(assetFilePath)) {
                    is = assetManager.open(assetFileName);
                } else {
                    is = assetManager.open(assetFilePath + File.separator + assetFileName);
                }
                FileOutputStream out = new FileOutputStream(outFile);
                byte buf[] = new byte[1024];
                int len;
                while ((len = is.read(buf)) > 0) out.write(buf, 0, len);
                out.close();
                is.close();
            }//end of if
            return outFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String readRawFileContentToStr(Context context, int rawId) {
        try {
            InputStream is = context.getResources().openRawResource(rawId);
            if (null != is) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                return new String(buffer, "UTF-8");
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //播放震动
    public static void playVibrator(Context context,boolean isRepeat) {
        Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);
        long[] patter = {0, 1500};//a[0]表示静止的时间，a[1]代表的是震动的时间,后面依次类推
        vibrator.vibrate(patter, isRepeat ? 0 : -1);
    }

    //播放声音
    public static void playSound(Context context,int pathId) {
        /*MediaPlayer mPlayer = MediaPlayer.create(context, pathId);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(null != mp)mp.release();//释放资源
            }
        });
        mPlayer.start();*/
        SoundPool soundPool= new SoundPool(10, AudioManager.STREAM_MUSIC,5);
        soundPool.load(context , pathId,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(1,1, 1, 0, 0, 1);
            }
        });
    }

    //btn selector
    public static StateListDrawable createSelector(String stateNormal, String statePressed, String stateFocused, String stateDisabled) {
        Drawable normal = stateNormal == null ? null : BitmapDrawable.createFromPath(stateNormal);
        Drawable pressed = statePressed == null ? null : BitmapDrawable.createFromPath(statePressed);
        Drawable focused = stateFocused == null ? null : BitmapDrawable.createFromPath(stateFocused);
        Drawable disabled = stateDisabled == null ? null : BitmapDrawable.createFromPath(stateDisabled);
        return createSelector(normal, pressed, focused, disabled);
    }

    //btn selector
    public static StateListDrawable createSelector(Drawable normal, Drawable pressed, Drawable focused, Drawable disabled) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled}, pressed);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, focused);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, focused);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normal);
        stateListDrawable.addState(new int[]{android.R.attr.state_window_focused}, disabled);
        stateListDrawable.addState(new int[]{}, normal);
        return stateListDrawable;
    }

    //checkbox/radio selector(warning:state_enabled below for state_checked)
    public static StateListDrawable createSelector(Drawable normal, Drawable checked) {
        StateListDrawable stateListDrawable = new StateListDrawable();
        stateListDrawable.addState(new int[]{android.R.attr.state_checked}, checked);
        stateListDrawable.addState(new int[]{android.R.attr.state_enabled}, normal);
        return stateListDrawable;
    }


    /**
     * convert dp to its equivalent px
     *
     * 将dp转换为与之相等的px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    /**
     * convert px to its equivalent dp
     *
     * 将px转换为与之相等的dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert px to its equivalent sp
     *
     * 将px转换为sp
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * convert sp to its equivalent px
     *
     * 将sp转换为px
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static Point getScreenWHToPoint(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return new Point(dm.widthPixels, dm.heightPixels);
    }

    public static boolean isShouldHideKeyboard(View view, MotionEvent event) {
        if (null != view && (view instanceof EditText)) {
            int[] location = {0, 0};
            view.getLocationInWindow(location);
            int left = location[0], top = location[1], bottom = top + view.getHeight(), right = left + view.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }//end of if
        return false;
    }

    //获取视图中根布局并转换成ViewGroup
    public static ViewGroup convertRootViewToViewGroup(View mainView) {
        if (null != mainView.getRootView() && mainView.getRootView() instanceof ViewGroup) {
            return (ViewGroup) mainView.getRootView();//ViewGroup subclass AbsoluteLayout, AdapterView, FrameLayout, LinearLayout, RelativeLayout, SlidingDrawer
        }//end of if
        return null;
    }

    //得到子控件在父布局中的索引
    public static int getChildViewInRootViewIndex(ViewGroup rootView, View mainView, int findViewId) {
        if (null == rootView) rootView = convertRootViewToViewGroup(mainView);
        if (null != rootView) {
            for (int i = 0; i < rootView.getChildCount(); i++) {
                View childView = rootView.getChildAt(i);
                if (null != childView && findViewId == childView.getId()) return i;
            }//end of for
        }//end of if
        return -1;
    }

    //得到子控件在父控件中的索引
    public static int getChildViewInParentViewIndex(ViewGroup parentView, int findViewId) {
        if (null != parentView) {
            for (int i = 0; i < parentView.getChildCount(); i++) {
                View childView = parentView.getChildAt(i);
                if (null != childView && findViewId == childView.getId()) return i;
            }//end of for
        }//end of if
        return -1;
    }

    //这是设置图片的不同状态,设置TextView环绕图片(direction value is 1/left,2/top,3/right,4/bottom)
    public static void setTextViewAroundDrawable(Context context, TextView textView, int imgResId, int imgWidthDp, int imgHeightDp, int imgPaddingDp, int direction) {
        setTextViewAroundDrawable(context, textView, context.getResources().getDrawable(imgResId), imgWidthDp, imgHeightDp, imgPaddingDp, direction);
    }

    public static void setTextViewAroundDrawable(Context context, TextView textView, Drawable aroundDrawable, int imgWidthDp, int imgHeightDp, int imgPaddingDp, int direction) {
        if (null != textView && null != aroundDrawable) {
            aroundDrawable.setBounds(0, 0, dip2px(context, imgWidthDp), dip2px(context, imgHeightDp));
            textView.setCompoundDrawablePadding(dip2px(context, imgPaddingDp));
            switch (direction) {
                case Gravity.LEFT:
                    textView.setCompoundDrawables(aroundDrawable, null, null, null);
                    break;
                case Gravity.TOP:
                    textView.setCompoundDrawables(null, aroundDrawable, null, null);
                    break;
                case Gravity.RIGHT:
                    textView.setCompoundDrawables(null, null, aroundDrawable, null);
                    break;
                case Gravity.BOTTOM:
                    textView.setCompoundDrawables(null, null, null, aroundDrawable);
                    break;
            }//end of switch_
        }//edn of if
    }

    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1;//Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) return result;
        }//end of for
    }

    //兼容低版本的设置View背景方法,避免报错
    public static void setBackgroundAllVersion(View view, Drawable bgDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(bgDrawable);
        } else {
            view.setBackgroundDrawable(bgDrawable);
        }
    }

    //序列化对象
    public static String serializeObject(Object object) {
        try {
            if (null != object) {
                long startTime = System.currentTimeMillis();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(object);
                String serStr = byteArrayOutputStream.toString("ISO-8859-1");
                serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
                objectOutputStream.close();
                byteArrayOutputStream.close();
                //System.out.println("序列化成功,耗时为:"+ (System.currentTimeMillis() - startTime)+"ms");
                return serStr;
            } else {
                System.out.println("序列对象是null,返回null");
                return "";
            }
        } catch (IOException e) {
            System.out.println("序列化异常!");
            e.printStackTrace();
            return "";
        }
    }

    //反序列化对象
    public static Object deSerializationObject(String saveObjStr) {
        try {
            if (!TextUtils.isEmpty(saveObjStr) && saveObjStr.length() > 0) {
                long startTime = System.currentTimeMillis();
                String redStr = java.net.URLDecoder.decode(saveObjStr, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                Object obj = objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
                //System.out.println("反序列化成功,耗时为:" + (System.currentTimeMillis() - startTime)+"ms");
                return obj;
            } else {
                System.out.println("反序列对象是null,返回null");
                return null;
            }
        } catch (Exception e) {
            System.out.println("反序列化异常!");
            e.printStackTrace();
            return null;
        }
    }

    //计算字符串的宽度(像素)
    public static int getStringPixelWidth(Paint strPaint, String str) {
        return ((int) strPaint.measureText(str));
    }

    //计算字符串的高度(像素)
    public static int getStringPixelHeight(Paint strPaint, String str) {
        Rect rect = new Rect();
        strPaint.getTextBounds(str, 0, str.length(), rect);
        return rect.height();
    }

    //格式化字符串
    public static String formatStr(String format, int args) {
        return String.format(format, args);
    }

    public static Point getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point(wm.getDefaultDisplay().getWidth(), wm.getDefaultDisplay().getHeight());
        return point;
    }

    public static void removeViewFromParent(View view) {
        if (view == null) return;
        ViewParent parent = view.getParent();
        if (parent != null) ((ViewGroup) parent).removeView(view);
    }

    public static String getDeviceIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (null != tm) return tm.getDeviceId();
        return null;
    }

    public static void callPhoneNumber(Activity activity, String phone_number) {
        if (!TextUtils.isEmpty(phone_number)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone_number));
            activity.startActivity(intent);//内部类
        } else {
            showShortHintInfo(activity, "抱歉,不能完成拨号,号码为空!");
        }
    }

    public static void callSmsContentView(Activity activity, String smsBody) {
        if (!TextUtils.isEmpty(smsBody)) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
            intent.putExtra("sms_body", smsBody);
            activity.startActivity(intent);
        }//end of if
    }

    public static void copyTextToSystemClipboard(Activity activity, String text) {
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null != cm && !TextUtils.isEmpty(text)) {
            ClipData mClipData = ClipData.newPlainText("Label", text);
            cm.setPrimaryClip(mClipData);
            //cm.setText(text);
            showShortHintInfo(activity, "内容已经成功复制,粘贴即可使用!");
        } else {
            showShortHintInfo(activity, "抱歉,复制异常,不能完成复制!");
        }
    }

    //获取当前版本标识号
    public static int getCurrentVersionCode(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取当前版本号
    public static String getCurrentVersionName(Context mContext) {
        try {
            return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //得到进程名字
    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (null == runningApps) return null;
        for (ActivityManager.RunningAppProcessInfo appInfo : runningApps)
            if (appInfo.pid == pid) return appInfo.processName;
        return null;
    }

    //重新计算ListView或GridView高度
    public static int reSetAbsListViewHeightBasedOnChildren(AbsListView absListView) {
        int totalHeight = 0;
        ListAdapter listAdapter = absListView.getAdapter();
        if (null == listAdapter) return 0;
        if (absListView instanceof ListView) {
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, absListView);
                listItem.measure(0, 0);//计算子项View的宽高
                totalHeight += listItem.getMeasuredHeight();
            }//end of for
            totalHeight += (((ListView) absListView).getDividerHeight() * (listAdapter.getCount() - 1));
        } else if (absListView instanceof GridView) {
            int column = -1;
            try {//getNumColumns一直是-1,mRequestedNumColumns反而有值
                Field field = GridView.class.getDeclaredField("mRequestedNumColumns"); //获得申明的字段
                field.setAccessible(true); //设置访问权限
                column = Integer.valueOf(field.get((absListView)).toString());//获取字段的值
            } catch (Exception e1) {
            }
            if (-1 != column) {
                for (int i = 0; i < listAdapter.getCount(); i += column) {
                    View listItem = listAdapter.getView(i, null, absListView);
                    listItem.measure(0, 0);//计算子项View的宽高
                    totalHeight += listItem.getMeasuredHeight();
                }//end of for
            }//end of if
            totalHeight += (((GridView) absListView).getVerticalSpacing() * (listAdapter.getCount() - 1));
        }//end of else if
        ViewGroup.LayoutParams params = absListView.getLayoutParams();
        if (null != params) {
            params.height = totalHeight;
            absListView.setLayoutParams(params);
        }//end of if
        return totalHeight;
    }

    //设置GridView的宽高
    public static void reSetGridViewHeightBasedOnChildren(Context context, GridView gridView, int dataSize, int numColumns, int rowHeightDp, int verticalSpacingDp) {
        int row = 1;
        if (dataSize > numColumns) row = (int) Math.ceil((double) dataSize / (double) numColumns);
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        if (null != params) {
            params.height = (dip2px(context, rowHeightDp) * row) + ((row - 1) * dip2px(context, verticalSpacingDp));
            gridView.setLayoutParams(params);
        }//end of if
    }

    //安装apk方法
    public static void installApk(Context context, String filename) {
        File file = new File(filename);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void setActivityAlpha(Activity activity, float alpha) {
        WindowManager.LayoutParams params = activity.getWindow().getAttributes();
        params.alpha = alpha;
        activity.getWindow().setAttributes(params);
    }

    public static int strInStringArray(String a, String[] b) {
        if (TextUtils.isEmpty(a) || null == b) return -1;
        for (int i = 0; i < b.length; i++) if (a.equals(b[i])) return i;
        return -1;
    }

    public static int strInStringList(String a, List<String> b) {
        if (TextUtils.isEmpty(a) || null == b) return -1;
        for (int i = 0; i < b.size(); i++) if (a.equals(b.get(i))) return i;
        return -1;
    }

    public static long getNumberInString(String string) {
        if (TextUtils.isEmpty(string)) return Long.MAX_VALUE;
        String retStr = "";
        for (int i = 0; i < string.length(); i++)
            if (string.charAt(i) >= 48 && string.charAt(i) <= 57) retStr += string.charAt(i);
        if (TextUtils.isEmpty(retStr)) return Long.MAX_VALUE;
        return Long.parseLong(retStr);
    }

    //将GridView改成单行横向布局
    public static void changeGridViewToHorizontal(Context context, GridView gridView, int itemWidthForDp, int itemPaddingHForDp, int listSize) {
        int itemWidth = dip2px(context, itemWidthForDp);// item宽度
        int itemPaddingH = dip2px(context, itemPaddingHForDp);// item之间的间隔
        int gridViewWidth = listSize * (itemWidth + itemPaddingH);// 计算GridView宽度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        gridView.setLayoutParams(params);
        gridView.setColumnWidth(itemWidth);
        gridView.setHorizontalSpacing(itemPaddingH);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setNumColumns(listSize);
    }

    //获取状态栏高度
    public static int getSystemStausBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        return statusBarHeight;
    }

    public static boolean isAppRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses)
            if (appProcess.processName.equals(context.getPackageName())) return true;
        return false;
    }

    public static boolean isLocationServiceOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) return true;
        return false;
    }

    //把图片插入到系统图库并刷新相册
    public static void insertSystemPhotoAlbumScan(Context context,boolean insertSystemAlbum,String photoName,String photoPath){
        if(insertSystemAlbum){
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), photoPath, photoName, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }//end of if
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + photoPath)));//通知图库更新
    }

    public static void goToMarketScoreRate(Activity activity){
        try{
            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }catch(Exception e){
            Toast.makeText(activity, "您的手机没有安装一款应用市场", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // 手机号码前三后四脱敏
    public static String mobileEncrypt(String mobile) {
        if(TextUtils.isEmpty(mobile) || (mobile.length() != 11))return mobile;
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    //身份证前三后四脱敏
    public static String idEncrypt(String id) {
        if(TextUtils.isEmpty(id) || (id.length() < 8))return id;
        return id.replaceAll("(?<=\\w{3})\\w(?=\\w{4})", "*");
    }
}

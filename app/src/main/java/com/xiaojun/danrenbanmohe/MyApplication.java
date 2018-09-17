package com.xiaojun.danrenbanmohe;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import android.view.WindowManager;

import com.xiaojun.danrenbanmohe.bean.MyObjectBox;
import com.xiaojun.danrenbanmohe.dialogall.CommonData;
import com.xiaojun.danrenbanmohe.dialogall.CommonDialogService;
import com.xiaojun.danrenbanmohe.dialogall.ToastUtils;
import com.yatoooon.screenadaptation.ScreenAdapterTools;
import io.objectbox.BoxStore;
import megvii.facepass.FacePassHandler;



/**
 * Created by Administrator on 2018/8/3.
 */

public class MyApplication extends Application implements Application.ActivityLifecycleCallbacks {
    public FacePassHandler facePassHandler=null;
    public static BoxStore mBoxStore;
    public static MyApplication myApplication;


    static {
        System.loadLibrary("yuv_utils");
        System.loadLibrary("yuv");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;
        mBoxStore = MyObjectBox.builder().androidContext(this).build();

        //适配
        ScreenAdapterTools.init(this);
//        JPushInterface.setDebugMode(false); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(getApplicationContext());
//        JPushInterface.setAlias(getApplicationContext(),1, FileUtil.getSerialNumber(this)==null?FileUtil.getIMSI():FileUtil.getSerialNumber(this));
//        Log.d("MyApplication","机器码"+ FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this));
//        //全局dialog
        this.registerActivityLifecycleCallbacks(this);//注册
        CommonData.applicationContext = this;
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager mWindowManager  = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.getDefaultDisplay().getMetrics(metric);
        CommonData.ScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
        Intent dialogservice = new Intent(this, CommonDialogService.class);
        startService(dialogservice);




    }

    public BoxStore getBoxStore(){
        return mBoxStore;
    }

    public FacePassHandler getFacePassHandler() {

        return facePassHandler;
    }

    public void setFacePassHandler(FacePassHandler facePassHandler1){
        facePassHandler=facePassHandler1;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if(activity.getParent()!=null){
            CommonData.mNowContext = activity.getParent();
        }else
            CommonData.mNowContext = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity.getParent()!=null){
            CommonData.mNowContext = activity.getParent();
        }else
            CommonData.mNowContext = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ToastUtils.getInstances().cancel();
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}

package com.xiaojun.danrenbanmohe.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.sdsmdg.tastytoast.TastyToast;
import com.xiaojun.danrenbanmohe.MyApplication;
import com.xiaojun.danrenbanmohe.R;
import com.xiaojun.danrenbanmohe.bean.BaoCunBean;

import org.greenrobot.eventbus.EventBus;

import megvii.facepass.FacePassException;
import megvii.facepass.FacePassHandler;
import megvii.facepass.types.FacePassAddFaceResult;
import megvii.facepass.types.FacePassConfig;
import megvii.facepass.types.FacePassModel;
import megvii.facepass.types.FacePassPose;


public class FacePassUtil {
  private FacePassModel trackModel;
  private FacePassModel poseModel;
 private FacePassModel blurModel;
 private FacePassModel livenessModel;
   private FacePassModel searchModel;
   private FacePassModel detectModel;
  private FacePassModel ageGenderModel;
    /* SDK 实例对象 */

 private FacePassHandler mFacePassHandler;  /* 人脸识别Group */
    private  final String group_name = "face-pass-test-x";
    private static final String group_name_mxNan = "face-pass-test-xnan2";
    private static final String group_name_mxNv = "face-pass-test-xnv2";
    private  boolean isLocalGroupExist = false;
    private BaoCunBean baoCunBean=null;
    private int cameraRotation;
    private Context context;

    public FacePassUtil() {

    }

    public  void init(final Activity activity , final Context context, final int cameraRotation, final BaoCunBean baoCunBean){
            this.baoCunBean=baoCunBean;
            this.context=context;
            this.cameraRotation=cameraRotation;
            new Thread() {
                @Override
                public void run() {
                    while (!activity.isFinishing()) {
                        if (FacePassHandler.isAvailable()) {
                            /* FacePass SDK 所需模型， 模型在assets目录下 */
                            trackModel = FacePassModel.initModel(context.getAssets(), "tracker.DT1.4.1.dingding.20180315.megface2.9.bin");
                            poseModel = FacePassModel.initModel(context.getAssets(), "pose.alfa.tiny.170515.bin");
                            blurModel = FacePassModel.initModel(context.getAssets(), "blurness.v5.l2rsmall.bin");
                            livenessModel = FacePassModel.initModel(context.getAssets(), "panorama.facepass.offline.180312.bin");
                            searchModel = FacePassModel.initModel(context.getAssets(), "feat.small.facepass.v2.9.bin");
                            detectModel = FacePassModel.initModel(context.getAssets(), "detector.mobile.v5.fast.bin");
                            ageGenderModel = FacePassModel.initModel(context.getAssets(), "age_gender.bin");
                            /* SDK 配置 */
                            float searchThreshold = baoCunBean.getShibieFaZhi();
                            float livenessThreshold = baoCunBean.getHuoTiFZ();
                            boolean livenessEnabled = baoCunBean.isHuoTi();
                            int faceMinThreshold =baoCunBean.getShibieFaceSize();
                            FacePassPose poseThreshold = new FacePassPose(30f, 30f, 30f);
                            float blurThreshold = 0.3f;
                            float lowBrightnessThreshold = 70f;
                            float highBrightnessThreshold = 210f;
                            float brightnessSTDThreshold = 60f;
                            int retryCount = 0;
                            int rotation = cameraRotation;
                            String fileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            FacePassConfig config;
                            try {

                                /* 填入所需要的配置 */
                                config = new FacePassConfig(searchThreshold, livenessThreshold, livenessEnabled,
                                        faceMinThreshold, poseThreshold, blurThreshold,
                                        lowBrightnessThreshold, highBrightnessThreshold, brightnessSTDThreshold,
                                        retryCount, rotation, fileRootPath,
                                        trackModel, poseModel, blurModel, livenessModel, searchModel, detectModel, ageGenderModel);
                                /* 创建SDK实例 */
                                mFacePassHandler = new FacePassHandler(config);


                                float searchThreshold2 = 75f;
                                float livenessThreshold2 = 48f;
                                boolean livenessEnabled2 = true;
                                int faceMinThreshold2 = baoCunBean.getRuKuFaceSize();
                                float blurThreshold2 = baoCunBean.getRuKuMoHuDu();
                                float lowBrightnessThreshold2 = 50f;
                                float highBrightnessThreshold2 = 250f;
                                float brightnessSTDThreshold2 = 60f;
                                FacePassConfig config1=new FacePassConfig(faceMinThreshold2,40f,40f,40f,blurThreshold2,
                                        lowBrightnessThreshold2,highBrightnessThreshold2,brightnessSTDThreshold2);

                                Log.d("FacePassUtil", "设置入库质量配置" + mFacePassHandler.setAddFaceConfig(config1));

                                MyApplication.myApplication.setFacePassHandler(mFacePassHandler);

                                try {

                                 //   boolean a=  mFacePassHandler.createLocalGroup(group_name);
                                    boolean a1=  mFacePassHandler.createLocalGroup(group_name_mxNan);
                                    boolean a2=  mFacePassHandler.createLocalGroup(group_name_mxNv);

                                    if (a1 && a2){
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                //添加明星库
                                                try {
                                                    int man[]={R.drawable.n1};int wmen[]={R.drawable.m1};

                                                    //男明星库
                                                    for (int i=0;i<1;i++){
                                                        FacePassAddFaceResult result= mFacePassHandler.addFace(BitmapFactory.decodeResource(context.getResources(),man[i]));
                                                        if (result.result==0){
                                                            Log.d("FacePassUtil", "添加男明星" + mFacePassHandler.bindGroup(group_name_mxNan,result.faceToken));
                                                        }
                                                    }
                                                    //女明星库
                                                    for (int i=0;i<1;i++){
                                                        FacePassAddFaceResult result= mFacePassHandler.addFace(BitmapFactory.decodeResource(context.getResources(),wmen[i]));
                                                        if (result.result==0){
                                                            Log.d("FacePassUtil", "添加女明星" + mFacePassHandler.bindGroup(group_name_mxNv,result.faceToken));
                                                        }

                                                    }

                                                } catch (FacePassException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }).start();

                                    }

                                    Log.d("FacePassUtil", "创建组" + a1+a2);
                                } catch (FacePassException e) {
                                  Log.d("FacePassUtil", e.getMessage()+"方法撒旦");
                                }

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast tastyToast= TastyToast.makeText(context,"识别模块初始化成功", TastyToast.LENGTH_LONG, TastyToast.INFO);
                                        tastyToast.setGravity(Gravity.CENTER,0,0);
                                        tastyToast.show();
                                       MyApplication.myApplication.setFacePassHandler(mFacePassHandler);
                                        EventBus.getDefault().post("mFacePassHandler");

                                    }
                                });

                                checkGroup(activity,context);


                            } catch (FacePassException e) {
                                e.printStackTrace();

                                return;
                            }
                            return;
                        }
                        try {
                            /* 如果SDK初始化未完成则需等待 */
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }


    private  void checkGroup(Activity activity, final Context context) {
        if (mFacePassHandler == null) {
            return;
        }
        String[] localGroups = mFacePassHandler.getLocalGroups();
        isLocalGroupExist = false;
        if (localGroups == null || localGroups.length == 0) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    Toast tastyToast= TastyToast.makeText(context,"还没创建识别组", TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER,0,0);
                    tastyToast.show();
                }
            });
            return;
        }
        for (String group : localGroups) {
            if (group_name.equals(group)) {

                isLocalGroupExist = true;
            }
        }
        if (!isLocalGroupExist) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast tastyToast= TastyToast.makeText(context,"还没创建识别组", TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER,0,0);
                    tastyToast.show();
                }
            });
        }
    }


    public void setMyConfig(float search){
        FacePassModel  trackModel = FacePassModel.initModel(context.getAssets(), "tracker.DT1.4.1.dingding.20180315.megface2.9.bin");
        FacePassModel  poseModel = FacePassModel.initModel(context.getAssets(), "pose.alfa.tiny.170515.bin");
        FacePassModel  blurModel = FacePassModel.initModel(context.getAssets(), "blurness.v5.l2rsmall.bin");
        FacePassModel livenessModel = FacePassModel.initModel(context.getAssets(), "panorama.facepass.offline.180312.bin");
        FacePassModel searchModel = FacePassModel.initModel(context.getAssets(), "feat.small.facepass.v2.9.bin");
        FacePassModel detectModel = FacePassModel.initModel(context.getAssets(), "detector.mobile.v5.fast.bin");
        FacePassModel ageGenderModel = FacePassModel.initModel(context.getAssets(), "age_gender.bin");
        /* SDK 配置 */
        float searchThreshold =search ;
        float livenessThreshold = baoCunBean.getHuoTiFZ();
        boolean livenessEnabled = baoCunBean.isHuoTi();
        int faceMinThreshold =baoCunBean.getShibieFaceSize();
        FacePassPose poseThreshold = new FacePassPose(30f, 30f, 30f);
        float blurThreshold = 0.3f;
        float lowBrightnessThreshold = 70f;
        float highBrightnessThreshold = 210f;
        float brightnessSTDThreshold = 60f;
        int retryCount = 2;
        int rotation = cameraRotation;
        String fileRootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        FacePassConfig config;
        try {

            /* 填入所需要的配置 */
            config = new FacePassConfig(searchThreshold, livenessThreshold, livenessEnabled,
                    faceMinThreshold, poseThreshold, blurThreshold,
                    lowBrightnessThreshold, highBrightnessThreshold, brightnessSTDThreshold,
                    retryCount, rotation, fileRootPath,
                    trackModel, poseModel, blurModel, livenessModel, searchModel, detectModel, ageGenderModel);
            mFacePassHandler.setConfig(config);

        }catch (Exception e){
            Log.d("MainActivity", e.getMessage()+"设置识别配置异常");
        }
    }

}

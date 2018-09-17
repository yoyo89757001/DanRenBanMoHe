package com.xiaojun.danrenbanmohe.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.badoo.mobile.util.WeakHandler;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.sdsmdg.tastytoast.TastyToast;
import com.xiaojun.danrenbanmohe.MyApplication;
import com.xiaojun.danrenbanmohe.R;
import com.xiaojun.danrenbanmohe.adapter.DiBuAdapter;
import com.xiaojun.danrenbanmohe.bean.BaoCunBean;
import com.xiaojun.danrenbanmohe.bean.PaiHangBean;
import com.xiaojun.danrenbanmohe.bean.Subject;
import com.xiaojun.danrenbanmohe.bean.Subjects;
import com.xiaojun.danrenbanmohe.bean.YanZhiBean;
import com.xiaojun.danrenbanmohe.camera.CameraManager;
import com.xiaojun.danrenbanmohe.camera.CameraPreview;
import com.xiaojun.danrenbanmohe.camera.CameraPreviewData;
import com.xiaojun.danrenbanmohe.camera.SettingVar;
import com.xiaojun.danrenbanmohe.tts.control.InitConfig;
import com.xiaojun.danrenbanmohe.tts.control.MySyntherizer;
import com.xiaojun.danrenbanmohe.tts.control.NonBlockSyntherizer;
import com.xiaojun.danrenbanmohe.tts.listener.UiMessageListener;
import com.xiaojun.danrenbanmohe.tts.util.OfflineResource;
import com.xiaojun.danrenbanmohe.utils.DateUtils;
import com.xiaojun.danrenbanmohe.utils.FacePassUtil;
import com.xiaojun.danrenbanmohe.utils.FileUtil;
import com.xiaojun.danrenbanmohe.utils.GsonUtil;
import com.xiaojun.danrenbanmohe.view.FaceView;
import com.xiaojun.danrenbanmohe.view.GlideCircleTransform;
import com.xiaojun.danrenbanmohe.view.GlideRoundTransform;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.CharsetUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import megvii.facepass.FacePassException;
import megvii.facepass.FacePassHandler;
import megvii.facepass.ruitong.FaceInit;
import megvii.facepass.types.FacePassDetectionResult;
import megvii.facepass.types.FacePassFace;
import megvii.facepass.types.FacePassImage;
import megvii.facepass.types.FacePassImageRotation;
import megvii.facepass.types.FacePassImageType;
import megvii.facepass.types.FacePassRecognitionResult;
import megvii.facepass.types.FacePassRecognitionResultType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class MainActivity extends Activity implements CameraManager.CameraListener {
    protected Handler mainHandler;
    @BindView(R.id.fuzhi)
    TextView fuzhi;
    @BindView(R.id.xingbie)
    TextView xingbie;
    @BindView(R.id.guanzhudu)
    TextView guanzhudu;
    @BindView(R.id.touxiang)
    ImageView touxiang;
    @BindView(R.id.nianling)
    TextView nianling;
    private FacePassUtil facePassUtil = null;
    @BindView(R.id.renshu)
    TextView renshu;
    @BindView(R.id.xingqi)
    TextView xingqi;
    @BindView(R.id.shijian)
    TextView shijian;
    @BindView(R.id.fcview)
    FaceView fcview;
    private ImageView mingxingIM;
    private final int TIMEOUT = 1000 * 6;
    @BindView(R.id.zhongjianview)
    RelativeLayout zhongjianview;
    @BindView(R.id.tickerview)
    TickerView tickerview;
    //kk
    @BindView(R.id.tickerview2)
    TickerView tickerview2;
    private Box<Subjects> subjectsBox = null;
    private String appId = "11644783";
    private String appKey = "knGksRFLoFZ2fsjZaMC8OoC7";
    private String secretKey = "IXn1yrFezEo55LMkzHBGuTs1zOkXr9P4";
    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    private TtsMode ttsMode = TtsMode.MIX;
    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_speech_female.data为离线男声模型；bd_etts_speech_female.data为离线女声模型
    private String offlineVoice = OfflineResource.VOICE_FEMALE;
    // 主控制类，所有合成控制方法从这个类开始
    private MySyntherizer synthesizer;
    private ImageView imageView;
    private RequestOptions myOptions = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
            .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
    // .transform(new GlideRoundTransform(MainActivity.this,10));

    private RequestOptions myOptions2 = new RequestOptions()
            .fitCenter()
            .error(R.drawable.erroy_bg)
            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
            .transform(new GlideRoundTransform(MainActivity.this, 20));

    private enum FacePassSDKMode {
        MODE_ONLINE,
        MODE_OFFLINE
    }

    private static int count = 0;

    private final Timer timer = new Timer();
    private TimerTask task;
    //  private DBG_View dbg_view;
    // private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
    private static final String DEBUG_TAG = "FacePassDemo";
    // private static final int MSG_SHOW_TOAST = 1;
    // private static final int DELAY_MILLION_SHOW_TOAST = 2000;
    /* 识别服务器IP */
    // private static final String serverIP_offline = "10.104.44.50";//offline
    // private static final String serverIP_online = "10.199.1.14";
    //  private static String serverIP;
    //  private static final String authIP = "https://api-cn.faceplusplus.com";
    //  private static final String apiKey = "CKbSYQqAuc5AzCMoOK-kbo9KaabtEciQ";
    //  private static final String apiSecret = "HeZgW5ILE83nKkqF-QO5IqEEmeRxPgeI";
    //  private static String recognize_url;
    // private LinkedBlockingQueue<Subject> linkedBlockingQueue;
    /* 人脸识别Group */
    private String filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"alape.jpg";
    private static final String group_name = "face-pass-test-x";
    private static final String group_name_mxNan = "face-pass-test-xnan";
    private static final String group_name_mxNv = "face-pass-test-xnv";
    private static Vector<Subject> dibuList = new Vector<>();//下面的弹窗
    /* 程序所需权限 ：相机 文件存储 网络访问 */
    private static final int PERMISSIONS_REQUEST = 1;
    private static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private static final String PERMISSION_WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final String PERMISSION_READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String PERMISSION_INTERNET = Manifest.permission.INTERNET;
    private static final String PERMISSION_ACCESS_NETWORK_STATE = Manifest.permission.ACCESS_NETWORK_STATE;
    private String[] Permission = new String[]{PERMISSION_CAMERA, PERMISSION_WRITE_STORAGE, PERMISSION_READ_STORAGE, PERMISSION_INTERNET, PERMISSION_ACCESS_NETWORK_STATE};

    private WindowManager wm;
    /* SDK 实例对象 */
    public FacePassHandler mFacePassHandler;
    /* 相机实例 */
    private CameraManager manager;
    /* 显示人脸位置角度信息 */

    /* 相机预览界面 */
    private CameraPreview cameraView;
    private boolean isAnXia = true;
    /* 在预览界面圈出人脸 */
    private FaceView faceView;
    /* 相机是否使用前置摄像头 */
    private static boolean cameraFacingFront = true;
    /* 相机图片旋转角度，请根据实际情况来设置
     * 对于标准设备，可以如下计算旋转角度rotation
     * int windowRotation = ((WindowManager)(getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
     * Camera.CameraInfo info = new Camera.CameraInfo();
     * Camera.getCameraInfo(cameraFacingFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK, info);
     * int cameraOrientation = info.orientation;
     * int rotation;
     * if (cameraFacingFront) {
     *     rotation = (720 - cameraOrientation - windowRotation) % 360;
     * } else {
     *     rotation = (windowRotation - cameraOrientation + 360) % 360;
     * }
     */
    private int cameraRotation;
    private static final int cameraWidth = 1280;
    private static final int cameraHeight = 720;
    // private int mSecretNumber = 0;
    // private static final long CLICK_INTERVAL = 500;
    //  private long mLastClickTime;
    //  private IjkVideoView shipingView;
    // private static Vector<DIKu> diKuVector=new Vector<>();
    private static Vector<PaiHangBean> paiHangBeanVector = new Vector<>();
    private int heightPixels;
    private int widthPixels;
    private View view;
    int screenState = 0;// 0 横 1 竖
    /* 网络请求队列*/
    //   RequestQueue requestQueue;
    //    private EditText gaodu,kuandu;
//    private TextView biaoti;
//    private WindowManager.LayoutParams wmParams;
//    private View xiugaiView;
    /*Toast 队列*/
    LinkedBlockingQueue<Toast> mToastBlockQueue;
    /*DetectResult queue*/
    ArrayBlockingQueue<FacePassDetectionResult> mDetectResultQueue;
    ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    /*recognize thread*/
    RecognizeThread mRecognizeThread;
    FeedFrameThread mFeedFrameThread;
    TanChuangThread tanChuangThread;
    private static boolean isLink = true;
    private int dw, dh;
    //  private LayoutInflater mInflater = null;
    /*图片缓存*/
    //  private FaceImageCache mImageCache;
    // private Handler mAndroidHandler;
    private Box<BaoCunBean> baoCunBeanDao = null;
    private BaoCunBean baoCunBean = null;
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private WeakHandler mHandler;
    private DiBuAdapter diBuAdapter = null;
    private GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2, LinearLayoutManager.HORIZONTAL, false);
    private static final String authIP = "https://api-cn.faceplusplus.com";
    private static final String apiKey = "VIA9tPfCsx0_UXpf1oGmh6_dMqHvbmm9";
    private static final String apiSecret = "SYqy-J0lvdNpBpTfXz8ZOtsaXGsyHEQf";
    private byte[] nanT, nvT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mImageCache = new FaceImageCache();
        mToastBlockQueue = new LinkedBlockingQueue<>();
        mDetectResultQueue = new ArrayBlockingQueue<FacePassDetectionResult>(5);
        mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        // initAndroidHandler();
        baoCunBeanDao = MyApplication.myApplication.getBoxStore().boxFor(BaoCunBean.class);
        subjectsBox = MyApplication.myApplication.getBoxStore().boxFor(Subjects.class);
        mainHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }

        };

        baoCunBean = baoCunBeanDao.get(123456L);
        //  subjectBox = MyApplication.myApplication.getBoxStore().boxFor(Subject.class);

        //每分钟的广播
        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        //  linkedBlockingQueue = new LinkedBlockingQueue<>();

        EventBus.getDefault().register(this);//订阅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;

        /* 初始化界面 */
        initView();
//        dibuliebiao.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
//                    @Override
//                    public void onGlobalLayout(){
//                        //只需要获取一次高度，获取后移除监听器
//                        dibuliebiao.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//
//                    }
//
//                });

        /* 申请程序所需权限 */
        if (!hasPermission()) {
            requestPermission();
        } else {
            //初始化
            FacePassHandler.getAuth(authIP, apiKey, apiSecret);
            FacePassHandler.initSDK(getApplicationContext());
            //  FaceInit init = new FaceInit(getApplicationContext());
            // init.initFacePass();
        }

        if (baoCunBean != null)
            initialTts();

        if (baoCunBean != null) {
            facePassUtil = new FacePassUtil();
            facePassUtil.init(MainActivity.this, getApplicationContext(), cameraRotation, baoCunBean);
        } else {
            Toast tastyToast = TastyToast.makeText(MainActivity.this, "获取本地设置失败,请进入设置界面设置基本信息", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        }

        /* 初始化网络请求库 */
        //   requestQueue = Volley.newRequestQueue(getApplicationContext());

        mFeedFrameThread = new FeedFrameThread();
        mFeedFrameThread.start();

        mRecognizeThread = new RecognizeThread();
        mRecognizeThread.start();

        tanChuangThread = new TanChuangThread();
        tanChuangThread.start();


        mHandler = new WeakHandler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 111:
                        //弹窗

                        break;

                }
                return false;
            }
        });

    }


    @Override
    protected void onResume() {
        initToast();
        /* 打开相机 */
        if (hasPermission()) {
            manager.open(getWindowManager(), cameraFacingFront, cameraWidth, cameraHeight);
        }
        adaptFrameLayout();

        super.onResume();
    }


    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (mFacePassHandler == null) {
            return;
        }
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
        FacePassImage image;
        try {
            image = new FacePassImage(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height, cameraRotation, FacePassImageType.NV21);
        } catch (FacePassException e) {
            e.printStackTrace();
            return;
        }
        mFeedFrameQueue.offer(image);

    }

    private class FeedFrameThread extends Thread {
        boolean isIterrupt;

        @Override
        public void run() {
            while (!isIterrupt) {
                try {

                    FacePassImage image = mFeedFrameQueue.take();
                    /* 将每一帧FacePassImage 送入SDK算法， 并得到返回结果 */
                    final   FacePassDetectionResult  detectionResult = mFacePassHandler.feedFrame(image);
                    if (detectionResult == null || detectionResult.faceList.length == 0) {
                        faceView.clear();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                faceView.invalidate();
                            }
                        });
                    } else {
                        //拿陌生人图片
                        showFacePassFace(detectionResult);
                        //   Log.d("FeedFrameThread", "detectionResult.images.length:" + image.width+"  "+image.height);
                    }
//
                    //                  if (SDK_MODE == FacePassSDKMode.MODE_ONLINE) {
                    //                      /*抓拍版模式*/
//                        if (detectionResult != null && detectionResult.message.length != 0) {
//                            /* 构建http请求 */
//                            FacePassRequest request = new FacePassRequest(recognize_url, detectionResult, new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    Log.d(DEBUG_TAG, String.format("%s", response));
//                                    try {
//                                        JSONObject jsresponse = new JSONObject(response);
//                                        int code = jsresponse.getInt("code");
//                                        if (code != 0) {
//                                            Log.e(DEBUG_TAG, String.format("error code: %d", code));
//                                            return;
//                                        }
//                                        /* 将服务器返回的结果交回SDK进行处理来获得识别结果 */
//                                        FacePassRecognitionResult[] result = null;
//                                        try {
//                                            Log.i("lengthlength", "length is " + jsresponse.getString("data").getBytes().length);
//                                            result = mFacePassHandler.decodeResponse(jsresponse.getString("data").getBytes());
//                                        } catch (FacePassException e) {
//                                            e.printStackTrace();
//                                            return;
//                                        }
//                                        if (result == null || result.length == 0) {
//                                            return;
//                                        }
//
//                                        for (FacePassRecognitionResult res : result) {
//                                            String faceToken = new String(res.faceToken);
//                                            if (FacePassRecognitionResultType.RECOG_OK == res.facePassRecognitionResultType) {
//                                                getFaceImageByFaceToken(res.trackId, faceToken);
//                                            }
//                                            showRecognizeResult(res.trackId, res.detail.searchScore, res.detail.livenessScore, FacePassRecognitionResultType.RECOG_OK == res.facePassRecognitionResultType);
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError errors) {
//                                    final VolleyError error = errors;
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Log.e(DEBUG_TAG, "volley error response");
//                                            if (error.networkResponse != null) {
//                                                faceEndTextView.append(String.format("network error %d", error.networkResponse.statusCode));
//                                            } else {
//                                                String errorMessage = error.getClass().getSimpleName();
//                                                faceEndTextView.append("network error" + errorMessage);
//                                            }
//                                            faceEndTextView.append("\n");
//                                        }
//                                    });
//                                }
//                            });
//                            request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//                            Log.d(DEBUG_TAG, "request add");
//                            request.setTag("upload_detect_result_tag");
//                            requestQueue.add(request);
//                        }
                    //     } else {

                    /*离线模式，将识别到人脸的，message不为空的result添加到处理队列中*/
                    if (detectionResult != null && detectionResult.message.length != 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                        if (isLink) {
                            isLink = false;
                          //  mDetectResultQueue.offer(detectionResult);
                            try {
                              //  FacePassDetectionResult detectionResult = mDetectResultQueue.take();
                                int size = detectionResult.images.length;
                                int mow = 0;
                                Bitmap bitmapTX = null;
                                for (int i = 0; i < size; i++) {
                                    if (detectionResult.images[0].trackId == detectionResult.faceList[i].trackId) {
                                        //detectionResult.images存在 ，拿到男女 0是男 1是女
                                        mow = detectionResult.faceList[i].gender;
                                        YuvImage image3 = new YuvImage(detectionResult.images[0].image, ImageFormat.NV21, detectionResult.images[0].width, detectionResult.images[0].height, null);
                                        ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                                        image3.compressToJpeg(new Rect(0, 0, detectionResult.images[0].width, detectionResult.images[0].height), 100, stream3);
                                        final Bitmap bmp = BitmapFactory.decodeByteArray(stream3.toByteArray(), 0, stream3.size());
                                        stream3.close();
                                        bitmapTX = bmp;
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                imageView.setImageBitmap(bmp);
                                            }
                                        });

                                        break;
                                    }
                                }
                                if (bitmapTX == null) {
                                    isLink = true;
                                    mFacePassHandler.reset();
                                    return;
                                }
                                //private static final String group_name_mxNan = "face-pass-test-xnan";
                                //private static final String group_name_mxNv = "face-pass-test-xnv";
                                String gname;
                                if (mow == 0) {
                                    Log.d("RecognizeThread", "男");
                                    gname = "face-pass-test-xnan";
                                } else {
                                    Log.d("RecognizeThread", "女");
                                    gname = "face-pass-test-xnv";
                                }
                                FacePassRecognitionResult[] recognizeResult = mFacePassHandler.recognize(gname, detectionResult.message);
                                Log.d("RecognizeThread", "recognizeResult.length:" + recognizeResult.length);
                                if (recognizeResult != null && recognizeResult.length > 0) {
                                    for (final FacePassRecognitionResult result : recognizeResult) {
                                        //String faceToken = new String(result.faceToken);
                                        if (FacePassRecognitionResultType.RECOG_OK == result.facePassRecognitionResultType) {
                                            //识别的
                                            count = 0;

                                            Log.d("RecognizeThread", "识别的qq" + result.trackId);
                                            //转向
                                            Bitmap fileBitmap=FileUtil.adjustPhotoRotation(bitmapTX,90);
                                            if (fileBitmap==null){
                                                isLink = true;
                                                mFacePassHandler.reset();
                                            }
                                            saveBitmap2File(fileBitmap, filePath,100);
                                            getOkHttpClient2( result.faceToken);

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });


                                        } else {
                                            //未识别的
                                            // ConcurrentHashMap 建议用他去重
                                            Log.d("RecognizeThread", "未识别的qq" + result.trackId);
//                                    for (FacePassImage images:detectionResult.images){
//                                        Log.d("RecognizeThread", "images.trackId:" + images.trackId);
//                                        Log.d("RecognizeThread", "result.trackId:" + result.trackId);
//                                        if (images.trackId==result.trackId){
//                                            try {
//                                                YuvImage image2 = new YuvImage(images.image, ImageFormat.NV21,images.width ,images.height , null);
//                                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                                image2.compressToJpeg(new Rect(0, 0,images.width ,images.height), 100, stream);
//                                                final Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                                                stream.close();
//                                                runOnUiThread(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        Log.d("RecognizeThread", "图片");
//                                                        imageView.setImageBitmap(bitmap);
//                                                    }
//                                                });
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                            }
//                                            break;
//                                        }
//                                    }
                                            count++;
                                            if (count >= 4) {
                                                count = 0;
                                                //走下一步,网络请求
                                                isLink = true;
                                                mFacePassHandler.reset();
                                            } else {
                                                isLink = true;
                                                mFacePassHandler.reset();
                                            }

                                        }

                                    }
                                } else {
                                    isLink = true;
                                    mFacePassHandler.reset();
                                }

                            } catch ( FacePassException | IOException e) {
                                e.printStackTrace();
                                isLink = true;
                                mFacePassHandler.reset();
                            }


                        }

                        //getOkHttpClient2();

                            }
                        }).start();

                    }
                    //     }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class RecognizeThread extends Thread {

        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {


            }
        }


    }

    public void bidui(FacePassDetectionResult detectionResult, int p) {

    }

    private class TanChuangThread extends Thread {
        boolean isRing;

        @Override
        public void run() {
            while (!isRing) {
                try {
                    //有动画 ，延迟到一秒一次
                    SystemClock.sleep(1100);
                    //    Subject subject = linkedBlockingQueue.take();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isRing = true;
            // Log.d("RecognizeThread", "中断了弹窗线程");
            super.interrupt();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//        shipingView.pause();
    }


    /* 判断程序是否有所需权限 android22以上需要自申请权限 */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(PERMISSION_CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_READ_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(PERMISSION_ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    /* 请求程序所需权限 */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(Permission, PERMISSIONS_REQUEST);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED)
                    granted = false;
            }
            if (!granted) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    if (!shouldShowRequestPermissionRationale(PERMISSION_CAMERA)
                            || !shouldShowRequestPermissionRationale(PERMISSION_READ_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_WRITE_STORAGE)
                            || !shouldShowRequestPermissionRationale(PERMISSION_INTERNET)
                            || !shouldShowRequestPermissionRationale(PERMISSION_ACCESS_NETWORK_STATE)) {
                        Toast.makeText(getApplicationContext(), "需要开启摄像头网络文件存储权限", Toast.LENGTH_SHORT).show();
                    }
            } else {

                FaceInit init = new FaceInit(getApplicationContext());
                init.initFacePass();

            }
        }
    }

    private void adaptFrameLayout() {
        SettingVar.isButtonInvisible = false;
        SettingVar.iscameraNeedConfig = false;
    }

    private void initToast() {
        SettingVar.isButtonInvisible = false;
    }

    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        int windowRotation = ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
        if (windowRotation == 0) {
            cameraRotation = FacePassImageRotation.DEG90;
        } else if (windowRotation == 90) {
            cameraRotation = FacePassImageRotation.DEG0;
        } else if (windowRotation == 270) {
            cameraRotation = FacePassImageRotation.DEG180;
        } else {
            cameraRotation = FacePassImageRotation.DEG270;
        }
//        Log.i(DEBUG_TAG, "cameraRation: " + cameraRotation);
        cameraFacingFront = true;
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.isCross = preferences.getBoolean("isCross", SettingVar.isCross);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        if (SettingVar.isSettingAvailable) {
            cameraRotation = SettingVar.faceRotation;
            cameraFacingFront = SettingVar.cameraFacingFront;
        }

        //  Log.i("orientation", String.valueOf(windowRotation));
        final int mCurrentOrientation = getResources().getConfiguration().orientation;

        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = 1;
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = 0;
        }
        setContentView(R.layout.activity_main2);
        ScreenAdapterTools.getInstance().loadView(getWindow().getDecorView());
        ButterKnife.bind(this);
        AssetManager mgr = getAssets();
        //Univers LT 57 Condensed
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
        Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        Typeface tf3 = Typeface.createFromAsset(mgr, "fonts/kai.ttf");
        String riqi2 = DateUtils.getWeek(System.currentTimeMillis());
        //  riqi.setTypeface(tf);
        xingqi.setText(riqi2);
        //   xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
        //  riqi.setText(DateUtils.timesTwodian(System.currentTimeMillis() + ""));
        // xiaoshi.setTypeface(tf);
        shijian.setText(DateUtils.xiaoshi(System.currentTimeMillis() + ""));
        mingxingIM = findViewById(R.id.mingxing);
        view = findViewById(R.id.preview);

        //   daBg.setBackgroundResource(R.drawable.d_bg2);


//        shipingView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                long curTime = System.currentTimeMillis();
//                long durTime = curTime - mLastClickTime;
//                mLastClickTime = curTime;
//                if (durTime < CLICK_INTERVAL) {
//                    ++mSecretNumber;
//                    if (mSecretNumber == 3) {
//                        dialog=new XiuGaiGaoKuanDialog(MainActivity.this,MainActivity.this);
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.setContents("修改视频的宽高",(shipingView.getRight()-shipingView.getLeft())+"",(shipingView.getBottom()-shipingView.getTop())+"",2);
//                        dialog.setOnQueRenListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                } else {
//                    mSecretNumber = 0;
//                }
//            }
//        });
//
//


//        shipingView.setHudView(mHudView); //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
//        shipingView.setVideoPath(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "laowang.mp4");
//        // shipingView.setVideoURI(Uri.parse("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"));
//        shipingView.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(IMediaPlayer iMediaPlayer) {
//                //  shipingView.start();
//            }
//        });
//
//
//        shipingView.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
//                TastyToast.makeText(MainActivity2.this, "播放视频失败", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
//                return false;
//            }
//        });

        //  shipingView.start();


//        dbg_view=findViewById(R.id.dabg);
//        dbg_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                long curTime = System.currentTimeMillis();
//                long durTime = curTime - mLastClickTime;
//                mLastClickTime = curTime;
//                if (durTime < CLICK_INTERVAL) {
//                    ++mSecretNumber;
//                    if (mSecretNumber == 3) {
//                        dialog=new XiuGaiGaoKuanDialog(MainActivity.this,MainActivity.this);
//                        dialog.setCanceledOnTouchOutside(false);
//                        dialog.setContents("修改背景宽高",(dbg_view.getRight()-dbg_view.getLeft())+"",(dbg_view.getBottom()-dbg_view.getTop())+"",1);
//                        dialog.setOnQueRenListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }
//                } else {
//                    mSecretNumber = 0;
//                }
//            }
//        });
        imageView = findViewById(R.id.gggghh);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;

        /* 初始化界面 */
        faceView = this.findViewById(R.id.fcview);
        SettingVar.cameraSettingOk = false;

        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview22);
        cameraView.setH(dh, faceView);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

        //中间view
        //  setViewFullScreen(zhongjianview);

        Glide.with(MainActivity.this)
                .load(R.drawable.zidonghuoqu3)
                .apply(myOptions)
                .into(touxiang);

        //翻动动画
        tickerview.setCharacterLists(TickerUtils.provideNumberList());
        tickerview.setAnimationDuration(3000);
        tickerview.setAnimationInterpolator(new OvershootInterpolator());
        tickerview.setText("0");

        tickerview2.setCharacterLists(TickerUtils.provideNumberList());
        tickerview2.setAnimationDuration(3000);
        tickerview2.setAnimationInterpolator(new OvershootInterpolator());
        tickerview2.setText("0");

        try {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
            params.topMargin = -60;
            params.height = (dh * 5) / 7;
            params.bottomMargin = 10;
            view.setLayoutParams(params);
            view.invalidate();

            ;
        } catch (Exception e) {
            Log.d("CameraPreview", e.getMessage());
        }


    }


    /**
     * 设置view的大小
     *  @param
     * @param diKu
     * @param facetoken
     */
    private void setViewFullScreen(final RelativeLayout view, final PaiHangBean diKu, final byte[] facetoken) {
        Glide.get(MainActivity.this).clearDiskCache();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = dw;
                layoutParams.topMargin = -72;
                layoutParams.height = (dh * 5) / 7 + 30;
                view.setLayoutParams(layoutParams);
                view.invalidate();
                view.setX(-dw);
                zhongjianview.setVisibility(View.VISIBLE);

                //入场动画(从右往左)
                ValueAnimator anim = ValueAnimator.ofInt(-dw, 0);
                anim.setDuration(1600);
                anim.setRepeatMode(ValueAnimator.RESTART);
                Interpolator interpolator = new DecelerateInterpolator(2f);
                anim.setInterpolator(interpolator);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {

                        int currentValue = (Integer) animation.getAnimatedValue();
                        // 获得改变后的值
                        //   System.out.println(currentValue);
                        // 输出改变后的值
                        // 步骤4：将改变后的值赋给对象的属性值，下面会详细说明
                        view.setX(currentValue);
                        // 步骤5：刷新视图，即重新绘制，从而实现动画效果
                        view.requestLayout();

                    }
                });
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        try {
                            tickerview.setText(diKu.getYanzhi()+"");
                            float ll=74+(diKu.getYanzhi()/6);
                            if (ll>=100){
                                ll=99.99f;
                            }
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            tickerview2.setText( decimalFormat.format(ll)+"");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                anim.start();
                xingbie.setText(diKu.getXingbie());
                nianling.setText(diKu.getNianl()+"");
                fuzhi.setText(diKu.getFuzhi());
              //  guanzhudu.setText(diKu.getGuanzhu());

                Glide.get(MainActivity.this).clearMemory();
                Glide.with(MainActivity.this)
                        .load(diKu.getBytes())
                        .apply(myOptions)
                        .into(touxiang);

                try {
                    Glide.with(MainActivity.this)
                            .load(bitmabToBytes(mFacePassHandler.getFaceImage(facetoken)))
                            .apply(myOptions)
                            .into(mingxingIM);
                } catch (FacePassException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    protected void onStop() {
        SettingVar.isButtonInvisible = false;
        mToastBlockQueue.clear();
        mDetectResultQueue.clear();
        mFeedFrameQueue.clear();
        if (manager != null) {
            manager.release();
        }

        super.onStop();
    }

    @Override
    protected void onRestart() {
        //faceView.clear();
        // faceView.invalidate();
        //  if (shipingView!=null)
        // shipingView.start();
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();

    }


    @Override
    protected void onDestroy() {
        if (mRecognizeThread != null) {
            mRecognizeThread.isInterrupt = true;
            mRecognizeThread.interrupt();
        }
        if (tanChuangThread != null) {
            tanChuangThread.isRing = true;
            tanChuangThread.interrupt();
        }
        //时钟


        unregisterReceiver(timeChangeReceiver);


        EventBus.getDefault().unregister(this);//解除订阅

        if (manager != null) {
            manager.release();
        }
        if (mToastBlockQueue != null) {
            mToastBlockQueue.clear();
        }
//        if (mAndroidHandler != null) {
//            mAndroidHandler.removeCallbacksAndMessages(null);
//        }

        if (mFacePassHandler != null) {
            mFacePassHandler.release();
        }
        if (mFeedFrameQueue != null) {
            mFeedFrameQueue.clear();
        }
        if (synthesizer != null)
            synthesizer.release();

        timer.cancel();
        if (task != null)
            task.cancel();

        super.onDestroy();

        isLink = true;
    }


    private void showFacePassFace(FacePassDetectionResult detectResults) {
        FacePassFace[] detectResult = detectResults.faceList;
        //        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {

        //  long t1 = System.currentTimeMillis();
        faceView.clear();
        for (final FacePassFace face : detectResult) {
            boolean mirror = cameraFacingFront; /* 前摄像头时mirror为true */
            StringBuilder faceIdString = new StringBuilder();
            faceIdString.append("ID = ").append(face.trackId);
            SpannableString faceViewString = new SpannableString(faceIdString);
            faceViewString.setSpan(new TypefaceSpan("fonts/kai"), 0, faceViewString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            StringBuilder faceRollString = new StringBuilder();
            faceRollString.append("旋转: ").append((int) face.pose.roll).append("°");
            StringBuilder facePitchString = new StringBuilder();
            facePitchString.append("上下: ").append((int) face.pose.pitch).append("°");
            StringBuilder faceYawString = new StringBuilder();
            faceYawString.append("左右: ").append((int) face.pose.yaw).append("°");
            StringBuilder faceBlurString = new StringBuilder();
            faceBlurString.append("模糊: ").append(String.format("%.2f", face.blur));
            StringBuilder faceAgeString = new StringBuilder();
            faceAgeString.append("年龄: ").append(face.age);
            StringBuilder faceGenderString = new StringBuilder();

            switch (face.gender) {
                case 0:
                    faceGenderString.append("性别: 男");
                    break;
                case 1:
                    faceGenderString.append("性别: 女");
                    break;
                default:
                    faceGenderString.append("性别: ?");
            }

            Matrix mat = new Matrix();
            int w = cameraView.getMeasuredWidth();
            int h = cameraView.getMeasuredHeight();

            int cameraHeight = manager.getCameraheight();
            int cameraWidth = manager.getCameraWidth();

            float left = 0;
            float top = 0;
            float right = 0;
            float bottom = 0;
            switch (cameraRotation) {
                case 0:
                    left = face.rect.left;
                    top = face.rect.top;
                    right = face.rect.right;
                    bottom = face.rect.bottom;
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraWidth : 0f, 0f);
                    mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                    break;
                case 90:
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                    mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                    left = face.rect.top;
                    top = cameraWidth - face.rect.right;
                    right = face.rect.bottom;
                    bottom = cameraWidth - face.rect.left;

                    //北京面板机特有方向
//                            left =cameraHeight-face.rect.bottom;
//                            top = face.rect.left;
//                            right =cameraHeight-face.rect.top;
//                            bottom =face.rect.right;

                    break;
                case 180:
                    mat.setScale(1, mirror ? -1 : 1);
                    mat.postTranslate(0f, mirror ? (float) cameraHeight : 0f);
                    mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                    left = face.rect.right;
                    top = face.rect.bottom;
                    right = face.rect.left;
                    bottom = face.rect.top;
                    break;
                case 270:
                    mat.setScale(mirror ? -1 : 1, 1);
                    mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                    mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                    left = cameraHeight - face.rect.bottom;
                    top = face.rect.left;
                    right = cameraHeight - face.rect.top;
                    bottom = face.rect.right;
            }

            RectF drect = new RectF();

            RectF srect = new RectF(left - 40, top - 120, right + 40, bottom + 20);

            mat.mapRect(drect, srect);
            //头像加宽加高点
//                    RectF srect2 = new RectF(face.rect.left-100<0?0:face.rect.left-100, face.rect.top-100<0?0:face.rect.top-100,
//                            face.rect.right+100>imageee.width?imageee.width:face.rect.right+100, face.rect.bottom+100>imageee.height?imageee.height:face.rect.bottom+100);

//                    float pitch = face.pose.pitch;
//                    float roll = face.pose.roll;
//                    float yaw = face.pose.yaw;
//
//                    if (pitch < 15 && pitch > -15 && roll < 15 && roll > -15 && yaw < 15 && yaw > -15 && face.blur < 0.3) {
//                        try {
//                            faceView.addRect(drect);
//                            faceView.addId(faceIdString.toString());
//                            faceView.addRoll(faceRollString.toString());
//                            faceView.addPitch(facePitchString.toString());
//                            faceView.addYaw(faceYawString.toString());
//                            faceView.addBlur("");
//                            faceView.addAge(faceAgeString.toString());
//                            faceView.addGenders(faceGenderString.toString());
//
//                            if (isLink) {
//                                isLink = false;
//                                final long takeid=face.trackId;
//                                final  FacePassImage image=imageee;
//                                int size=detectResults.images.length;
//                                FacePassImage  iioio=null;
//                                for (int i=0;i<size;i++){
//                                    if (detectResults.images[i].trackId==takeid){
//                                        iioio=detectResults.images[i];
//                                    }
//                                }
//                                if (iioio==null){
//                                    mFacePassHandler.reset();
//                                    Log.d("MainActivity", "重置");
//                                    isLink=true;
//                                    return;
//                                }else {
//                                    Log.d("MainActivity", "ffffffff");
//                                }
//                                Log.d("MainActivity", "dddddddddd");
//
//                                //获取图片
//                                int ww=image.width;
//                                int hh=image.height;
//                                YuvImage image2 = new YuvImage(image.image, ImageFormat.NV21,ww ,hh , null);
//                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                                image2.compressToJpeg(new Rect(0, 0, ww, hh), 100, stream);
//                                final Bitmap bitmap = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
//                                stream.close();
//
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        imageView.setImageBitmap(bitmap);
//                                    }
//                                });
//
//                                int x1, y1, x2, y2 = 0;
//                                x1 = (int) srect2.left;
//                                y1 = (int) srect2.top ;
//                                //宽高不是坐标
//                                x2 = (srect2.left+(srect2.right-srect2.left))>image.width?(int)(image.width-srect2.left):(int)(srect2.right-srect2.left);
//                                y2 = (srect2.top+(srect2.bottom-srect2.top))>image.height?(int)(image.height-srect2.top):(int)(srect2.bottom-srect2.top);
//
//                                Bitmap oneBitmap1 = Bitmap.createBitmap(bitmap,x1,y1,x2,y2);
//                                 if (cameraRotation==270){
//                                    //转一下方向
//                                     oneBitmap1= rotateBitmap(bitmap,90);
//                                 }
//                                 final Bitmap oneBitmap=oneBitmap1;
//
//
//                               //  Bitmap bitmap = Bitmap.createScaledBitmap(oneBitmap,640,320, false);
//
////                                byte[] dstYuv;
////                               int  dstYuvLength = bitmap.getWidth() * bitmap.getHeight() * 3 / 2;
////                               int  srcYuvLength = bitmap.getWidth() * bitmap.getHeight() * 3 / 2;
////                               int  srcArgbLength = bitmap.getWidth() * bitmap.getHeight() * 4; // I
////                                YuvUtils.allocateMemo(srcYuvLength, srcArgbLength, dstYuvLength);
////                                dstYuv = new byte[dstYuvLength];
////                                YuvUtils.rgbToYuvBylibyuv(bitmap, dstYuv);
////
////                                YuvImage image3 = new YuvImage(dstYuv, ImageFormat.NV21, bitmap.getWidth(), bitmap.getHeight(), null);
////                                ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
////                                image3.compressToJpeg(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()), 100, stream3);
////                                final Bitmap bmp3 = BitmapFactory.decodeByteArray(stream3.toByteArray(), 0, stream3.size());
////                                stream3.close();
//                                facePassUtil.setMyConfig(75);
//
//                                final FacePassImage finalIioio = iioio;
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//
//                                        boolean isP=false;
//                                        for (int i=0;i<50;i++){
//                                            FacePassImage image1;
//                                            try {
//                                                image1 = new FacePassImage(image.image, finalIioio.width, finalIioio.height, cameraRotation, FacePassImageType.NV21);
//                                            } catch (FacePassException e) {
//                                                e.printStackTrace();
//                                                isLink=true;
//                                                return;
//                                            }
//                                            Log.d("MainActivity", "l1");
//                                            FacePassDetectionResult detectionResult = null;
//                                            detectionResult = mFacePassHandler.feedFrame(image1);
//                                            if (detectionResult != null && detectionResult.message.length != 0) {
//                                                //每次流程只会进来一次
//                                                isP=true;
//                                                Log.d("MainActivity", "l2");
//                                                FacePassRecognitionResult[] recognizeResult = mFacePassHandler.recognize(group_name,finalIioio.image);
//                                                if (recognizeResult != null && recognizeResult.length > 0) {
//                                                    Log.d("MainActivity", "l3");
//                                                    int isU=-1;
//                                                    for (FacePassRecognitionResult result2 : recognizeResult) {
//                                                        Log.d("MainActivity", "l5"+"  "+takeid+result2.trackId);
//
//                                                            if (FacePassRecognitionResultType.RECOG_OK == result2.facePassRecognitionResultType) {
//                                                                //识别的
//                                                                Log.d("RecognizeThread", "识别的111" + result2.trackId);
//                                                                isU=0;
//
//                                                            } else {
//                                                                //未识别的
//                                                                // ConcurrentHashMap 建议用他去重
//                                                                Log.d("RecognizeThread", "未识别的111" + result2.trackId);
//                                                                isU=1;
//                                                            }
//
//                                                    }
//                                                    //里面小循环完了
//                                                    if (isU==-1){
//                                                        //没找到trackId重新截图
//                                                        isLink=true;
//                                                        break;
//                                                    }else {
//                                                        if (isU==0){
//                                                            //识别出来了
//                                                            //联网
//                                                            // getOkHttpClient2(oneBitmap,face.trackId,image.image);
//                                                        }else {
//                                                            //未识别出来的入库
//                                                            FacePassAddFaceResult rf= mFacePassHandler.addFace(oneBitmap);
//                                                            if (rf.result==0){
//                                                                //入库成功
//                                                                subjectsBox.put(new Subjects(rf.faceToken));
//                                                                //联网
//                                                                //  getOkHttpClient2(oneBitmap,face.trackId,image.image);
//                                                            }else {
//                                                                isLink=true;
//                                                                break;
//                                                            }
//                                                        }
//
//
//                                                        //识别完了//去男明星库比对
//                                                        facePassUtil.setMyConfig(40);
//                                                        FacePassRecognitionResult[] recognizeResult2 = mFacePassHandler.recognize(group_name_mxNan, detectionResult.message);
//                                                        if (recognizeResult2 != null && recognizeResult2.length > 0) {
//                                                            for (FacePassRecognitionResult result2 : recognizeResult2) {
//                                                                if (result2.trackId==face.trackId){
//                                                                    if (FacePassRecognitionResultType.RECOG_OK == result2.facePassRecognitionResultType) {
//                                                                        //识别的
//                                                                        Log.d("RecognizeThread", "识别的男明星" + result2.trackId);
//                                                                        nanT=result2.faceToken;
//
//                                                                    } else {
//                                                                        nanT=null;
//                                                                        //未识别的
//                                                                        // ConcurrentHashMap 建议用他去重
//                                                                        Log.d("RecognizeThread", "未识别的男明星" + result2.trackId);
//
//                                                                    }
//                                                                }
//                                                            }
//                                                            //小循环完了 去女明星库比对
//                                                            facePassUtil.setMyConfig(40);
//                                                            FacePassRecognitionResult[] recognizeResult3 = mFacePassHandler.recognize(group_name_mxNan, detectionResult.message);
//                                                            if (recognizeResult3 != null && recognizeResult3.length > 0) {
//                                                                for (FacePassRecognitionResult result2 : recognizeResult3) {
//                                                                    if (result2.trackId==face.trackId){
//                                                                        if (FacePassRecognitionResultType.RECOG_OK == result2.facePassRecognitionResultType) {
//                                                                            //识别的
//                                                                            Log.d("RecognizeThread", "识别的女明星" + result2.trackId);
//                                                                            nvT=result2.faceToken;
//
//                                                                        } else {
//                                                                            nvT=null;
//                                                                            //未识别的
//                                                                            // ConcurrentHashMap 建议用他去重
//                                                                            Log.d("RecognizeThread", "未识别的女明星" + result2.trackId);
//
//                                                                        }
//                                                                    }
//                                                                }
//                                                                //识别完了 网络请求
//
//
//                                                                getOkHttpClient2(oneBitmap,nanT,nvT);
//                                                                //message为空的话 有可能出现这种情况
//                                                            }else {
//                                                                //没了 进行网络请求
//                                                                getOkHttpClient2(oneBitmap,nanT,nvT);
//
//                                                            }
//
//                                                        //message为空的话 有可能出现这种情况
//                                                        }else {
//                                                            //为空
//                                                            //进行女明星库比对
//                                                            //识别完了//去男明星库比对
//                                                            facePassUtil.setMyConfig(40);
//                                                            FacePassRecognitionResult[] recognizeResult3 = mFacePassHandler.recognize(group_name_mxNan, detectionResult.message);
//                                                            if (recognizeResult3 != null && recognizeResult3.length > 0) {
//                                                                for (FacePassRecognitionResult result2 : recognizeResult3) {
//                                                                    if (result2.trackId==face.trackId){
//                                                                        if (FacePassRecognitionResultType.RECOG_OK == result2.facePassRecognitionResultType) {
//                                                                            //识别的
//                                                                            Log.d("RecognizeThread", "识别的女明星" + result2.trackId);
//
//                                                                            nvT=result2.faceToken;
//                                                                        } else {
//                                                                            //未识别的
//                                                                            // ConcurrentHashMap 建议用他去重
//                                                                            Log.d("RecognizeThread", "未识别的女明星" + result2.trackId);
//                                                                            nvT=null;
//                                                                        }
//                                                                    }
//                                                                }
//                                                                //识别完了 网络请求
//                                                                getOkHttpClient2(oneBitmap,nanT,nvT);
//
//                                                            //message为空的话 有可能出现这种情况
//                                                            }else {
//                                                                //没了 进行网络请求
//                                                                getOkHttpClient2(oneBitmap,nanT,nvT);
//
//                                                            }
//
//                                                        }
//
//                                                    }
//
//                                                }else {
//                                                    Log.d("MainActivity", "l4");
//                                                    //为空 重新截图
//                                                    isLink=true;
//                                                    break;
//                                                }
//
//                                                break;
//                                            }
//
//                                        }
//                                           //表示detectionResult.message.length一直是0；所以要重新截图
//                                            if (!isP){
//                                                isLink=true;
//                                            }
//
//                                        } catch (Exception e) {
//                                           e.printStackTrace();
//                                          isLink=true;
//                                      }
//                                    }
//
//                                }).start();
//
//
//                            }
//
//                        } catch (Exception ex) {
//                            isLink = true;
//                            Log.e("Sys", "Error:" + ex.getMessage());
//                        }
//
//                    }else {
            faceView.addRect(drect);
            faceView.addId(faceIdString.toString());
            faceView.addRoll(faceRollString.toString());
            faceView.addPitch(facePitchString.toString());
            faceView.addYaw(faceYawString.toString());
            faceView.addBlur("");
            faceView.addAge(faceAgeString.toString());
            faceView.addGenders(faceGenderString.toString());
            //   }
            break;
        }

        faceView.postInvalidate();
//            }
//
//        });

    }


    /***
     *保存bitmap对象到文件中
     * @param bm
     * @param path
     * @param quality
     * @return
     */
    public  void saveBitmap2File(Bitmap bm, final String path, int quality) {
        if (null == bm || bm.isRecycled()) {
            Log.d("InFoActivity", "回收|空");
            return ;
        }
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            if (!bm.isRecycled()) {
                bm.recycle();
            }
            bm = null;
        }
    }

    //首先登录-->获取所有主机-->创建或者删除或者更新门禁
    private void getOkHttpClient2( final byte[] facetoken) {

        MultipartBody mBody=null;
        try {
            File  batt =new File(filePath);
            if (batt.length()<=0){
                isLink = true;
                mFacePassHandler.reset();
                return;
            }
            RequestBody fileBody1 = RequestBody.create(MediaType.parse("application/octet-stream") , batt);
            final String file1Name = System.currentTimeMillis()+"testFile1.jpg";
             mBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    /* 底下是上传了两个文件 */
                    .addFormDataPart("image_file" , file1Name , fileBody1)
                    /* 上传一个普通的String参数 */
                    .addFormDataPart("api_key", "BBDRR-nwJM38qGHUiBV0k4eMUZ2jDsa1")
                    .addFormDataPart("api_secret", "YDhcIhcjc5OVnDVQvspwSoSQnjM-fWYn")
                    .addFormDataPart("return_attributes", "gender,age,emotion,eyestatus,beauty,skinstatus")
                    .build();

        } catch (Exception e) {
            isLink = true;
            mFacePassHandler.reset();
            Log.d("YanShiActivityttttttt", e.getMessage() + "bitmap回收");
            return;
        }

        //  Base64.decode(printData.getBytes(), Base64.DEFAULT);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                //   .cookieJar(new CookiesManager())
                //  .retryOnConnectionFailure(true)
                .build();

//        RequestBody body = new FormBody.Builder()
//                .add("api_key", "BBDRR-nwJM38qGHUiBV0k4eMUZ2jDsa1")
//                .add("api_secret", "YDhcIhcjc5OVnDVQvspwSoSQnjM-fWYn")
//                .add("image_base64", batt)
//                .add("return_attributes", "gender,age,emotion,eyestatus,beauty,skinstatus")
//                .build();

        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder();
        //requestBuilder.header("User-Agent", "Koala Admin");
        //requestBuilder.header("Content-Type","application/json");
        requestBuilder.post(mBody);
        requestBuilder.url("https://api-cn.faceplusplus.com/facepp/v3/detect");
        final okhttp3.Request request = requestBuilder.build();

        Call mcall = okHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Log.d("YanShiActivity", "请求失败" + e.getMessage());
                isLink = true;
                mFacePassHandler.reset();
               // batt.delete();

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try {

                    String s = response.body().string();
                    Log.d("YanShiActivitytttttt", "检测" + s);
                    JsonObject jsonObject = GsonUtil.parse(s).getAsJsonObject();
                    Gson gson = new Gson();
                    YanZhiBean menBean = gson.fromJson(jsonObject, YanZhiBean.class);
                    if (menBean.getFaces() != null && menBean.getFaces().get(0) != null) {
                        Bitmap bitmap=BitmapFactory.decodeFile(filePath);
                      //  gengxingjiemian(menBean,facetoken,BitmapFactory.decodeFile(filePath));

                        //判断是否是同一个人
                        if (paiHangBeanVector.size() > 0) {
                            //比对
                            int dui = 0;
                            int size = paiHangBeanVector.size();
                            if (size>5){
                                //只比对前5个
                                size=5;
                            }
                            for (int i = 0; i < size; i++) {
                                float sou = 0;
                                try {
                                    //    Log.d("YanShiActivity", "bitmap.getHeight():" + bitmap.getHeight());
                                    //  Log.d("YanShiActivity", "diKuVector.get(i).getBytes().length:" + diKuVector.get(i).getBytes().length);
                                    sou = mFacePassHandler.compare(BitmapFactory.decodeByteArray(paiHangBeanVector.get(i).getBytes(), 0, paiHangBeanVector.get(i).getBytes().length), bitmap, false).score;
                                } catch (FacePassException e) {
                                    isLink = true;
                                    //  Log.d("YanShiActivity", e.getMessage()+"ggggg");
                                }
                                //   Log.d("YanShiActivity", "sou:" + sou);
                                if (sou >= 65) {
                                    dui = 1;
                                    //通过
                                    PaiHangBean diKu = new PaiHangBean();
                                    //更新
                                    YanZhiBean.FacesBean.AttributesBean.SkinstatusBean nn = menBean.getFaces().get(0).getAttributes().getSkinstatus();
                                    HashMap<Double, String> kk = new HashMap<>();
                                    double[] a = new double[4];
                                    a[0] = nn.getAcne();
                                    a[1] = nn.getDark_circle();
                                    a[2] = nn.getHealth();
                                    a[3] = nn.getStain();
                                    kk.put(a[0], "青春痘");
                                    kk.put(a[1], "黑眼圈");
                                    kk.put(a[2], "健康");
                                    kk.put(a[3], "暗淡");
                                    Arrays.sort(a);  //进行排序

                                    YanZhiBean.FacesBean.AttributesBean.EmotionBean nn2 = menBean.getFaces().get(0).getAttributes().getEmotion();
                                    HashMap<Double, String> kk2 = new HashMap<>();
                                    double[] a2 = new double[7];
                                    a2[0] = nn2.getAnger();
                                    a2[1] = nn2.getDisgust();
                                    a2[2] = nn2.getFear();
                                    a2[3] = nn2.getHappiness();
                                    a2[4] = nn2.getNeutral();
                                    a2[5] = nn2.getSadness();
                                    a2[6] = nn2.getSurprise();
                                    kk2.put(a2[0], "愤怒");
                                    kk2.put(a2[1], "厌恶");
                                    kk2.put(a2[2], "害怕");
                                    kk2.put(a2[3], "高兴");
                                    kk2.put(a2[4], "平静");
                                    kk2.put(a2[5], "悲伤");
                                    kk2.put(a2[6], "惊讶");
                                    Arrays.sort(a2);  //进行排序

                                    String xb = "";
                                    if (menBean.getFaces().get(0).getAttributes().getGender().getValue().equals("Male")) {
                                        xb = "男";
                                    } else {
                                        xb = "女";
                                    }
                                    //  Log.d("YanShiActivityttttt", "bitmabToBytes(bitmap):" + bitmabToBytes(bitmap).length);
                                    // diKu.setTrackId(trackId);
                                    diKu.setCishu(paiHangBeanVector.get(i).getCishu() + 1);
                                    diKu.setFuzhi(kk.get(a[3]));
                                    if (paiHangBeanVector.get(i).getNianl() > (menBean.getFaces().get(0).getAttributes().getAge().getValue() - 3)) {
                                        diKu.setNianl(menBean.getFaces().get(0).getAttributes().getAge().getValue() - 3);
                                    } else {
                                        diKu.setNianl(paiHangBeanVector.get(i).getNianl());
                                    }

                                    diKu.setXingbie(xb);
                                    //  diKu.setGuanzhu(System.currentTimeMillis());
                                    float yan = (float) (xb.equals("女") ? menBean.getFaces().get(0).getAttributes().getBeauty().getFemale_score() : menBean.getFaces().get(0).getAttributes().getBeauty().getMale_score());
                                    float fl=yan + 18 >= 100 ? 99.9f : yan + 18;
                                    if (paiHangBeanVector.get(i).getYanzhi() < yan) {
                                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                        diKu.setYanzhi(Float.valueOf(decimalFormat.format(fl)));
                                    } else {
                                        diKu.setYanzhi(paiHangBeanVector.get(i).getYanzhi());
                                    }

                                    diKu.setBiaoqing(kk2.get(a2[6]));
                                    diKu.setBytes(bitmabToBytes(bitmap));
                                    //替换掉
                                    paiHangBeanVector.set(i, diKu);
                                    //更新界面
                                    setViewFullScreen(zhongjianview, diKu, facetoken);

                                    break;

                                } else {
                                    //不是同一个人
                                    dui = 0;
                                }
                            }
                            //跟所有人不同 就添加
                            if (dui == 0) {
                                PaiHangBean diKu = new PaiHangBean();
                                //更新
                                YanZhiBean.FacesBean.AttributesBean.SkinstatusBean nn = menBean.getFaces().get(0).getAttributes().getSkinstatus();
                                HashMap<Double, String> kk = new HashMap<>();
                                double[] a = new double[4];
                                a[0] = nn.getAcne();
                                a[1] = nn.getDark_circle();
                                a[2] = nn.getHealth();
                                a[3] = nn.getStain();
                                kk.put(a[0], "青春痘");
                                kk.put(a[1], "黑眼圈");
                                kk.put(a[2], "健康");
                                kk.put(a[3], "暗淡");
                                Arrays.sort(a);  //进行排序

                                YanZhiBean.FacesBean.AttributesBean.EmotionBean nn2 = menBean.getFaces().get(0).getAttributes().getEmotion();
                                HashMap<Double, String> kk2 = new HashMap<>();
                                double[] a2 = new double[7];
                                a2[0] = nn2.getAnger();
                                a2[1] = nn2.getDisgust();
                                a2[2] = nn2.getFear();
                                a2[3] = nn2.getHappiness();
                                a2[4] = nn2.getNeutral();
                                a2[5] = nn2.getSadness();
                                a2[6] = nn2.getSurprise();
                                kk2.put(a2[0], "愤怒");
                                kk2.put(a2[1], "厌恶");
                                kk2.put(a2[2], "害怕");
                                kk2.put(a2[3], "高兴");
                                kk2.put(a2[4], "平静");
                                kk2.put(a2[5], "悲伤");
                                kk2.put(a2[6], "惊讶");
                                Arrays.sort(a2);  //进行排序

                                String xb = "";
                                if (menBean.getFaces().get(0).getAttributes().getGender().getValue().equals("Male")) {
                                    xb = "男";
                                } else {
                                    xb = "女";
                                }
                                //  Log.d("YanShiActivityttttt", "bitmabToBytes(bitmap):" + bitmabToBytes(bitmap).length);
                                //  diKu.setTrackId(trackId);
                                diKu.setCishu(diKu.getCishu() + 1);
                                diKu.setFuzhi(kk.get(a[3]));
                                diKu.setNianl(menBean.getFaces().get(0).getAttributes().getAge().getValue() - 3);
                                diKu.setXingbie(xb);
                                diKu.setGuanzhu(System.currentTimeMillis());
                                float yan = (float) (xb.equals("女") ? menBean.getFaces().get(0).getAttributes().getBeauty().getFemale_score() : menBean.getFaces().get(0).getAttributes().getBeauty().getMale_score());
                                float fl=yan + 18 >= 100 ? 99.9f : yan + 18;
                                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                diKu.setYanzhi(Float.valueOf(decimalFormat.format(fl)));
                                diKu.setBiaoqing(kk2.get(a2[6]));
                                diKu.setBytes(bitmabToBytes(bitmap));
                                paiHangBeanVector.add(diKu);
                                //更新界面
                                setViewFullScreen(zhongjianview, diKu, facetoken);

                            }


                        } else {
                            PaiHangBean diKu = new PaiHangBean();
                            //更新
                            YanZhiBean.FacesBean.AttributesBean.SkinstatusBean nn = menBean.getFaces().get(0).getAttributes().getSkinstatus();
                            HashMap<Double, String> kk = new HashMap<>();
                            double[] a = new double[4];
                            a[0] = nn.getAcne();
                            a[1] = nn.getDark_circle();
                            a[2] = nn.getHealth();
                            a[3] = nn.getStain();
                            kk.put(a[0], "青春痘");
                            kk.put(a[1], "黑眼圈");
                            kk.put(a[2], "健康");
                            kk.put(a[3], "暗淡");
                            Arrays.sort(a);  //进行排序

                            YanZhiBean.FacesBean.AttributesBean.EmotionBean nn2 = menBean.getFaces().get(0).getAttributes().getEmotion();
                            HashMap<Double, String> kk2 = new HashMap<>();
                            double[] a2 = new double[7];
                            a2[0] = nn2.getAnger();
                            a2[1] = nn2.getDisgust();
                            a2[2] = nn2.getFear();
                            a2[3] = nn2.getHappiness();
                            a2[4] = nn2.getNeutral();
                            a2[5] = nn2.getSadness();
                            a2[6] = nn2.getSurprise();
                            kk2.put(a2[0], "愤怒");
                            kk2.put(a2[1], "厌恶");
                            kk2.put(a2[2], "害怕");
                            kk2.put(a2[3], "高兴");
                            kk2.put(a2[4], "平静");
                            kk2.put(a2[5], "悲伤");
                            kk2.put(a2[6], "惊讶");
                            Arrays.sort(a2);  //进行排序

                            String xb = "";
                            if (menBean.getFaces().get(0).getAttributes().getGender().getValue().equals("Male")) {
                                xb = "男";
                            } else {
                                xb = "女";
                            }
                            //  Log.d("YanShiActivityttttt", "bitmabToBytes(bitmap):" + bitmabToBytes(bitmap).length);
                            // diKu.setTrackId(trackId);
                            diKu.setCishu(diKu.getCishu() + 1);
                            diKu.setFuzhi(kk.get(a[3]));
                            diKu.setNianl(menBean.getFaces().get(0).getAttributes().getAge().getValue() - 3);
                            diKu.setXingbie(xb);
                            diKu.setGuanzhu(System.currentTimeMillis());
                            float yan = (float) (xb.equals("女") ? menBean.getFaces().get(0).getAttributes().getBeauty().getFemale_score() : menBean.getFaces().get(0).getAttributes().getBeauty().getMale_score());
                            float fl=yan + 18 >= 100 ? 99.9f : yan + 18;
                            DecimalFormat decimalFormat = new DecimalFormat("0.00");
                            diKu.setYanzhi(Float.valueOf(decimalFormat.format(fl)));
                            diKu.setBiaoqing(kk2.get(a2[6]));
                            diKu.setBytes(bitmabToBytes(bitmap));
                            paiHangBeanVector.add(diKu);
                            //更新界面
                            setViewFullScreen(zhongjianview, diKu, facetoken);

                        }

                        //排序
                        Log.d("YanShiActivity", "paiHangBeanVector.size():" + paiHangBeanVector.size());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Comparator<PaiHangBean> studentComparator2 = new Comparator<PaiHangBean>() {
                                    @Override
                                    public int compare(PaiHangBean o1, PaiHangBean o2) {

                                        if (o1.getYanzhi() != o2.getYanzhi()) {
                                            return (int) (o2.getYanzhi() - o1.getYanzhi());
                                        }
                                        return 0;
                                    }
                                };
                                Collections.sort(paiHangBeanVector, studentComparator2);
                                int size = paiHangBeanVector.size();
                                if (size > 6) {
                                    paiHangBeanVector.remove(0);
                                    size -= 1;
                                }
                                //  adapter_zuo.notifyDataSetChanged();

                            }
                        });

                        Log.d("YanShiActivitytttttt", "更新成功");


                    }

                } catch (Exception e) {
                    Log.d("YanShiActivity", e.getMessage() + "");
                    isLink = true;
                }

            }
        });

    }




    //图片转为二进制数据
    public byte[] bitmabToBytes(Bitmap bitmap) {
        //将图片转化为位图
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            return baos.toByteArray();
        } catch (Exception ignored) {
        } finally {
            try {
                bitmap.recycle();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    //图片转为二进制数据
    public byte[] bitmabToBytes2(Bitmap bitmap) {
        //将图片转化为位图
        int size = bitmap.getWidth() * bitmap.getHeight() * 4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos = new ByteArrayOutputStream(size);
        try {
            //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            //将字节数组输出流转化为字节数组byte[]
            return baos.toByteArray();
        } catch (Exception ignored) {
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }


    private static final int REQUEST_CODE_CHOOSE_PICK = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //从相册选取照片后读取地址
            case REQUEST_CODE_CHOOSE_PICK:
                if (resultCode == RESULT_OK) {
                    String path = "";
                    Uri uri = data.getData();
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    if (cursor != null) {
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                    }
                    if (!TextUtils.isEmpty(path) && "file".equalsIgnoreCase(uri.getScheme())) {
                        path = uri.getPath();
                    }
                    if (TextUtils.isEmpty(path)) {
                        try {
                            path = FileUtil.getPath(getApplicationContext(), uri);
                        } catch (Exception e) {
                        }
                    }
                    if (TextUtils.isEmpty(path)) {
                        toast("图片选取失败！");
                        return;
                    }
//                    if (!TextUtils.isEmpty(path) && mFaceOperationDialog != null && mFaceOperationDialog.isShowing()) {
//                        EditText imagePathEdt = (EditText) mFaceOperationDialog.findViewById(R.id.et_face_image_path);
//                        imagePathEdt.setText(path);
//                    }
                }
                break;
        }
    }




//    /**
//     * 选择变换
//     *
//     * @param origin 原图
//     * @param alpha  旋转角度，可正可负
//     * @return 旋转后的图片
//     */
//    private Bitmap rotateBitmap(Bitmap origin, float alpha) {
//        if (origin == null) {
//            return null;
//        }
//        int width = origin.getWidth();
//        int height = origin.getHeight();
//        Matrix matrix = new Matrix();
//        matrix.setRotate(alpha);
//        // 围绕原地进行旋转
//        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
//        if (newBM.equals(origin)) {
//            return newBM;
//        }
//        origin.recycle();
//
//        return newBM;
//    }


//    private void getFaceImageByFaceToken(final long trackId, String faceToken) {
//        if (TextUtils.isEmpty(faceToken)) {
//            return;
//        }
//
//        final String faceUrl = "http://" + serverIP + ":8080/api/image/v1/query?face_token=" + faceToken;
//
//        final Bitmap cacheBmp = mImageCache.getBitmap(faceUrl);
//        if (cacheBmp != null) {
//            mAndroidHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache not null");
//                    showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, cacheBmp);
//                }
//            });
//            return;
//        } else {
//            try {
//                final Bitmap bitmap = mFacePassHandler.getFaceImage(faceToken.getBytes());
//                mAndroidHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.i(DEBUG_TAG, "getFaceImageByFaceToken cache is null");
//                        showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, bitmap);
//                    }
//                });
//                if (bitmap != null) {
//                    return;
//                }
//            } catch (FacePassException e) {
//                e.printStackTrace();
//            }
//
//        }
//        ByteRequest request = new ByteRequest(Request.Method.GET, faceUrl, new Response.Listener<byte[]>() {
//            @Override
//            public void onResponse(byte[] response) {
//
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = false;
//                Bitmap bitmap = BitmapFactory.decodeByteArray(response, 0, response.length, options);
//                mImageCache.putBitmap(faceUrl, bitmap);
//                showToast("ID = " + String.valueOf(trackId), Toast.LENGTH_SHORT, true, bitmap);
//                Log.i(DEBUG_TAG, "getFaceImageByFaceToken response ");
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i(DEBUG_TAG, "image load failed ! ");
//            }
//        });
//        request.setTag("load_image_request_tag");
//        requestQueue.add(request);
//    }


//    /*同步底库操作*/
//    private void showSyncGroupDialog() {
//
//        if (mSyncGroupDialog != null && mSyncGroupDialog.isShowing()) {
//            mSyncGroupDialog.hide();
//            requestQueue.cancelAll("handle_sync_request_tag");
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_sync_groups, null);
//
//        final EditText groupNameEt = (EditText) view.findViewById(R.id.et_group_name);
//        final TextView syncDataTv = (TextView) view.findViewById(R.id.tv_show_sync_data);
//
//        Button obtainGroupsBtn = (Button) view.findViewById(R.id.btn_obtain_groups);
//        Button createGroupBtn = (Button) view.findViewById(R.id.btn_submit);
//        ImageView closeWindowIv = (ImageView) view.findViewById(R.id.iv_close);
//
//        final Button handleSyncDataBtn = (Button) view.findViewById(R.id.btn_handle_sync_data);
//        final ListView groupNameLv = (ListView) view.findViewById(R.id.lv_group_name);
//        final ScrollView syncScrollView = (ScrollView) view.findViewById(R.id.sv_handle_sync_data);
//
//        final GroupNameAdapter groupNameAdapter = new GroupNameAdapter();
//
//        builder.setView(view);
//        closeWindowIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mSyncGroupDialog.dismiss();
//            }
//        });
//
//
//
//
//        handleSyncDataBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                String requestData = mFacePassHandler.getSyncRequestData();
//                getHandleSyncGroupData(requestData);
//            }
//
//            private void getHandleSyncGroupData(final String paramsValue) {
//
//                ByteRequest request = new ByteRequest(Request.Method.POST, "http://" + serverIP + ":8080/api/service/sync/v1", new Response.Listener<byte[]>() {
//                    @Override
//                    public void onResponse(byte[] response) {
//                        if (mFacePassHandler == null) {
//
//                            return;
//                        }
//                        FacePassSyncResult result3 = null;
//                        try {
//                            result3 = mFacePassHandler.handleSyncResultData(response);
//                        } catch (FacePassException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (result3 == null || result3.facePassGroupSyncDetails == null) {
//                            toast("handle sync result is failed!");
//                            return;
//                        }
//
//                        StringBuilder builder = new StringBuilder();
//                        for (FacePassGroupSyncDetail detail : result3.facePassGroupSyncDetails) {
//                            builder.append("========" + detail.groupName + "==========" + "\r\n");
//                            builder.append("groupName :" + detail.groupName + " \r\n");
//                            builder.append("facetokenadded :" + detail.faceAdded + " \r\n");
//                            builder.append("facetokendeleted :" + detail.faceDeleted + " \r\n");
//                            builder.append("resultcode :" + detail.result + " \r\n");
//                        }
//                        syncDataTv.setText(builder);
//                        syncScrollView.setVisibility(View.VISIBLE);
//                        groupNameLv.setVisibility(View.GONE);
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        error.printStackTrace();
//                    }
//                }) {
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//
//                        return paramsValue.getBytes();
//                    }
//                };
//                request.setTag("handle_sync_request_tag");
//                requestQueue.add(request);
//            }
//        });
//
//        groupNameAdapter.setOnItemDeleteButtonClickListener(new GroupNameAdapter.ItemDeleteButtonClickListener() {
//            @Override
//            public void OnItemDeleteButtonClickListener(int position) {
//                List<String> groupNames = groupNameAdapter.getData();
//                if (groupNames == null) {
//                    return;
//                }
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                String groupName = groupNames.get(position);
//                boolean isSuccess = false;
//                try {
//                    isSuccess = mFacePassHandler.deleteLocalGroup(groupName);
//                } catch (FacePassException e) {
//                    e.printStackTrace();
//                }
//                if (isSuccess) {
//                    String[] groups = mFacePassHandler.getLocalGroups();
//                    if (group_name.equals(groupName)) {
//                        //isLocalGroupExist = false;
//                    }
//                    if (groups != null) {
//                        groupNameAdapter.setData(Arrays.asList(groups));
//                        groupNameAdapter.notifyDataSetChanged();
//                    }
//                    toast("删除成功!");
//                } else {
//                    toast("删除失败!");
//
//                }
//            }
//
//        });
//
//        mSyncGroupDialog = builder.create();
//
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//
//        WindowManager.LayoutParams attributes = mSyncGroupDialog.getWindow().getAttributes();
//        attributes.height = d.getHeight();
//        attributes.width = d.getWidth();
//        mSyncGroupDialog.getWindow().setAttributes(attributes);
//
//        mSyncGroupDialog.show();
//
//    }

    //   private AlertDialog mFaceOperationDialog;

//    private void showAddFaceDialog() {
//
//        if (mFaceOperationDialog != null && !mFaceOperationDialog.isShowing()) {
//            mFaceOperationDialog.show();
//            return;
//        }
//        if (mFaceOperationDialog != null && mFaceOperationDialog.isShowing()) {
//            return;
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_face_operation, null);
//        builder.setView(view);
//
//        final EditText faceImagePathEt = (EditText) view.findViewById(R.id.et_face_image_path);
//        final EditText faceTokenEt = (EditText) view.findViewById(R.id.et_face_token);
//        final EditText groupNameEt = (EditText) view.findViewById(R.id.et_group_name);
//
//        Button choosePictureBtn = (Button) view.findViewById(R.id.btn_choose_picture);
//        Button addFaceBtn = (Button) view.findViewById(R.id.btn_add_face);
//        Button getFaceImageBtn = (Button) view.findViewById(R.id.btn_get_face_image);
//        Button deleteFaceBtn = (Button) view.findViewById(R.id.btn_delete_face);
//        Button bindGroupFaceTokenBtn = (Button) view.findViewById(R.id.btn_bind_group);
//        Button getGroupInfoBtn = (Button) view.findViewById(R.id.btn_get_group_info);
//
//        ImageView closeIv = (ImageView) view.findViewById(R.id.iv_close);
//
//        final ListView groupInfoLv = (ListView) view.findViewById(R.id.lv_group_info);
//
//        final FaceTokenAdapter faceTokenAdapter = new FaceTokenAdapter();
//
//        groupNameEt.setText(group_name);
//
//        closeIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFaceOperationDialog.dismiss();
//            }
//        });
//
//        choosePictureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
//                intentFromGallery.setType("image/*"); // 设置文件类型
//                intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(intentFromGallery, REQUEST_CODE_CHOOSE_PICK);
//                } catch (ActivityNotFoundException e) {
//                    toast("请安装相册或者文件管理器");
//                }
//            }
//        });
//
//        addFaceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                String imagePath = faceImagePathEt.getText().toString();
//                if (TextUtils.isEmpty(imagePath)) {
//                    toast("请输入正确的图片路径！");
//                    return;
//                }
//
//                File imageFile = new File(imagePath);
//                if (!imageFile.exists()) {
//                    toast("图片不存在 ！");
//                    return;
//                }
//
//                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
//
//                try {
//                    FacePassAddFaceResult result = mFacePassHandler.addFace(bitmap);
//                    if (result != null) {
//                        if (result.result == 0) {
//                            toast("add face successfully！");
//                            faceTokenEt.setText(new String(result.faceToken));
//                        } else if (result.result == 1) {
//                            toast("no face ！");
//                        } else {
//                            toast("quality problem！");
//                        }
//                    }
//                } catch (FacePassException e) {
//                    e.printStackTrace();
//                    toast(e.getMessage());
//                }
//            }
//        });
//
//        getFaceImageBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                try {
//                    byte[] faceToken = faceTokenEt.getText().toString().getBytes();
//                    Bitmap bmp = mFacePassHandler.getFaceImage(faceToken);
//                    final ImageView iv = (ImageView) findViewById(R.id.imview);
//                    iv.setImageBitmap(bmp);
//                    iv.setVisibility(View.VISIBLE);
//                    iv.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            iv.setVisibility(View.GONE);
//                            iv.setImageBitmap(null);
//                        }
//                    }, 2000);
//                    mFaceOperationDialog.dismiss();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toast(e.getMessage());
//                }
//            }
//        });
//
//        deleteFaceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                boolean b = false;
//                try {
//                    byte[] faceToken = faceTokenEt.getText().toString().getBytes();
//                    b = mFacePassHandler.deleteFace(faceToken);
//                    if (b) {
//                        String groupName = groupNameEt.getText().toString();
//                        if (TextUtils.isEmpty(groupName)) {
//                            toast("group name  is null ！");
//                            return;
//                        }
//                        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
//                        List<String> faceTokenList = new ArrayList<>();
//                        if (faceTokens != null && faceTokens.length > 0) {
//                            for (int j = 0; j < faceTokens.length; j++) {
//                                if (faceTokens[j].length > 0) {
//                                    faceTokenList.add(new String(faceTokens[j]));
//                                }
//                            }
//
//                        }
//                        faceTokenAdapter.setData(faceTokenList);
//                        groupInfoLv.setAdapter(faceTokenAdapter);
//                    }
//                } catch (FacePassException e) {
//                    e.printStackTrace();
//                    toast(e.getMessage());
//                }
//
//                String result = b ? "success " : "failed";
//                toast("delete face " + result);
//                Log.d(DEBUG_TAG, "delete face  " + result);
//
//            }
//        });
//
//        bindGroupFaceTokenBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//
//                byte[] faceToken = faceTokenEt.getText().toString().getBytes();
//                String groupName = groupNameEt.getText().toString();
//                if (faceToken == null || faceToken.length == 0 || TextUtils.isEmpty(groupName)) {
//                    toast("params error！");
//                    return;
//                }
//                try {
//                    boolean b = mFacePassHandler.bindGroup(groupName, faceToken);
//                    String result = b ? "success " : "failed";
//                    toast("bind  " + result);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toast(e.getMessage());
//                }
//
//
//            }
//        });
//
//        getGroupInfoBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                String groupName = groupNameEt.getText().toString();
//                if (TextUtils.isEmpty(groupName)) {
//                    toast("group name  is null ！");
//                    return;
//                }
//                try {
//                    byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
//                    List<String> faceTokenList = new ArrayList<>();
//                    if (faceTokens != null && faceTokens.length > 0) {
//                        for (int j = 0; j < faceTokens.length; j++) {
//                            if (faceTokens[j].length > 0) {
//                                faceTokenList.add(new String(faceTokens[j]));
//                            }
//                        }
//
//                    }
//                    faceTokenAdapter.setData(faceTokenList);
//                    groupInfoLv.setAdapter(faceTokenAdapter);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toast("get local group info error!");
//                }
//
//            }
//        });
//
//        faceTokenAdapter.setOnItemButtonClickListener(new FaceTokenAdapter.ItemButtonClickListener() {
//            @Override
//            public void onItemDeleteButtonClickListener(int position) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//                String groupName = groupNameEt.getText().toString();
//                if (TextUtils.isEmpty(groupName)) {
//                    toast("group name  is null ！");
//                    return;
//                }
//                try {
//                    byte[] faceToken = faceTokenAdapter.getData().get(position).getBytes();
//                    boolean b = mFacePassHandler.deleteFace(faceToken);
//                    String result = b ? "success " : "failed";
//                    toast("delete face " + result);
//                    if (b) {
//                        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
//                        List<String> faceTokenList = new ArrayList<>();
//                        if (faceTokens != null && faceTokens.length > 0) {
//                            for (int j = 0; j < faceTokens.length; j++) {
//                                if (faceTokens[j].length > 0) {
//                                    faceTokenList.add(new String(faceTokens[j]));
//                                }
//                            }
//
//                        }
//                        faceTokenAdapter.setData(faceTokenList);
//                        groupInfoLv.setAdapter(faceTokenAdapter);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toast(e.getMessage());
//                }
//
//            }
//
//            @Override
//            public void onItemUnbindButtonClickListener(int position) {
//                if (mFacePassHandler == null) {
//                    toast("FacePassHandle is null ! ");
//                    return;
//                }
//
//                String groupName = groupNameEt.getText().toString();
//                if (TextUtils.isEmpty(groupName)) {
//                    toast("group name  is null ！");
//                    return;
//                }
//                try {
//                    byte[] faceToken = faceTokenAdapter.getData().get(position).getBytes();
//                    boolean b = mFacePassHandler.unBindGroup(groupName, faceToken);
//                    String result = b ? "success " : "failed";
//                    toast("unbind " + result);
//                    if (b) {
//                        byte[][] faceTokens = mFacePassHandler.getLocalGroupInfo(groupName);
//                        List<String> faceTokenList = new ArrayList<>();
//                        if (faceTokens != null && faceTokens.length > 0) {
//                            for (int j = 0; j < faceTokens.length; j++) {
//                                if (faceTokens[j].length > 0) {
//                                    faceTokenList.add(new String(faceTokens[j]));
//                                }
//                            }
//
//                        }
//                        faceTokenAdapter.setData(faceTokenList);
//                        groupInfoLv.setAdapter(faceTokenAdapter);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    toast("unbind error!");
//                }
//
//            }
//        });
//
//
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//        mFaceOperationDialog = builder.create();
//        WindowManager.LayoutParams attributes = mFaceOperationDialog.getWindow().getAttributes();
//        attributes.height = d.getHeight();
//        attributes.width = d.getWidth();
//        mFaceOperationDialog.getWindow().setAttributes(attributes);
//        mFaceOperationDialog.show();
//    }

    private void toast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 根据facetoken下载图片缓存
     */
    private static class FaceImageCache implements ImageLoader.ImageCache {

        private static final int CACHE_SIZE = 6 * 1024 * 1024;

        LruCache<String, Bitmap> mCache;

        public FaceImageCache() {
            mCache = new LruCache<String, Bitmap>(CACHE_SIZE) {

                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getRowBytes() * value.getHeight();
                }
            };
        }

        @Override
        public Bitmap getBitmap(String url) {
            return mCache.get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            mCache.put(url, bitmap);
        }
    }

    private class FacePassRequest extends Request<String> {

        HttpEntity entity;

        FacePassDetectionResult mFacePassDetectionResult;
        private Response.Listener<String> mListener;

        public FacePassRequest(String url, FacePassDetectionResult detectionResult, Response.Listener<String> listener, Response.ErrorListener errorListener) {
            super(Method.POST, url, errorListener);
            mFacePassDetectionResult = detectionResult;
            mListener = listener;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(String response) {
            mListener.onResponse(response);
        }

        @Override
        public String getBodyContentType() {
            return entity.getContentType().getValue();
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//        beginRecogIdArrayList.clear();

            for (FacePassImage passImage : mFacePassDetectionResult.images) {
                /* 将人脸图转成jpg格式图片用来上传 */
                YuvImage img = new YuvImage(passImage.image, ImageFormat.NV21, passImage.width, passImage.height, null);
                Rect rect = new Rect(0, 0, passImage.width, passImage.height);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                img.compressToJpeg(rect, 95, os);
                byte[] tmp = os.toByteArray();
                ByteArrayBody bab = new ByteArrayBody(tmp, String.valueOf(passImage.trackId) + ".jpg");
//            beginRecogIdArrayList.add(passImage.trackId);
                entityBuilder.addPart("image_" + String.valueOf(passImage.trackId), bab);
            }
            StringBody sbody = null;
            try {
                sbody = new StringBody(MainActivity.group_name, ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            entityBuilder.addPart("group_name", sbody);
            StringBody data = null;
            try {
                data = new StringBody(new String(mFacePassDetectionResult.message), ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            entityBuilder.addPart("face_data", data);
            entity = entityBuilder.build();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                entity.writeTo(bos);
            } catch (IOException e) {
                VolleyLog.e("IOException writing to ByteArrayOutputStream");
            }
            byte[] result = bos.toByteArray();
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                startActivity(new Intent(MainActivity.this, SheZhiActivity.class));
                finish();
            }

        }
        Log.d("MainActivity", "keyCode:" + keyCode);
        Log.d("MainActivity", "event:" + event);

        return super.onKeyDown(keyCode, event);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        Log.d("MainActivity", "ev.getPointerCount()1:" + ev.getPointerCount());
        Log.d("MainActivity", "ev.getAction()1:" + ev.getAction());
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isAnXia = true;
        }
        if (isAnXia) {
            if (ev.getPointerCount() == 4) {
                isAnXia = false;
                startActivity(new Intent(MainActivity.this, SheZhiActivity.class));
                finish();
            }
        }

        return super.dispatchTouchEvent(ev);
    }

    /**
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        // 设置初始化参数
        SpeechSynthesizerListener listener = new UiMessageListener(mainHandler); // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        Map<String, String> params = getParams();
        // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
        InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);
        synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler); // 此处可以改为MySyntherizer 了解调用过程

    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        params.put(SpeechSynthesizer.PARAM_SPEAKER, baoCunBean.getBoyingren() + ""); // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_VOLUME, "8"); // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, baoCunBean.getYusu() + "");// 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, baoCunBean.getYudiao() + "");// 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);         // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());

        return params;
    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
            // toPrint("【error】:copy files from assets failed." + e.getMessage());
        }
        return offlineResource;
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {
        if (event.equals("mFacePassHandler")) {
            mFacePassHandler = MyApplication.myApplication.getFacePassHandler();
            // diBuAdapter = new DiBuAdapter(dibuList, MainActivity.this, dibuliebiao.getWidth(), dibuliebiao.getHeight(), mFacePassHandler);
            // dibuliebiao.setLayoutManager(gridLayoutManager);
            // dibuliebiao.setAdapter(diBuAdapter);
            return;
        }
        Toast tastyToast = TastyToast.makeText(MainActivity.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:

                    AssetManager mgr = getAssets();
                    //Univers LT 57 Condensed
                    Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
                    Typeface tf2 = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
                    Typeface tf3 = Typeface.createFromAsset(mgr, "fonts/kai.ttf");
                    // String riqi2 = DateUtils.timesTwo(System.currentTimeMillis() + "") + "   " + DateUtils.getWeek(System.currentTimeMillis());
                    //  riqi.setTypeface(tf);
                    //  riqi.setText(riqi2);
                    //   xiaoshi.setTypeface(tf);
                    //  xiaoshi.setText(DateUtils.timeMinute(System.currentTimeMillis() + ""));

                    String riqi2 = DateUtils.getWeek(System.currentTimeMillis());
                    //  riqi.setTypeface(tf);
                    xingqi.setText(riqi2);
                    //   xingqi.setText(DateUtils.getWeek(System.currentTimeMillis()));
                    //  riqi.setText(DateUtils.timesTwodian(System.currentTimeMillis() + ""));
                    // xiaoshi.setTypeface(tf);
                    shijian.setText(DateUtils.xiaoshi(System.currentTimeMillis() + ""));
                    //  Toast.makeText(context, "1 min passed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


    //上传识别记录
    private void link_shangchuanjilu(Subject subject) {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .writeTimeout(100000, TimeUnit.MILLISECONDS)
                .connectTimeout(100000, TimeUnit.MILLISECONDS)
                .readTimeout(100000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
                .retryOnConnectionFailure(true)
                .build();
        RequestBody body = null;

        body = new FormBody.Builder()
                //.add("name", subject.getName()) //
                //.add("companyId", subject.getCompanyId()+"") //公司di
                //.add("companyName",subject.getCompanyName()+"") //公司名称
                //.add("storeId", subject.getStoreId()+"") //门店id
                //.add("storeName", subject.getStoreName()+"") //门店名称
                //   .add("subjectId", subject.getId() + "") //员工ID
                //   .add("subjectType", subject.getPeopleType()) //人员类型
                // .add("department", subject.getPosition()+"") //部门
                //   .add("discernPlace", FileUtil.getSerialNumber(this) == null ? FileUtil.getIMSI() : FileUtil.getSerialNumber(this))//识别地点
                // .add("discernAvatar",  "") //头像
                .add("identificationTime", DateUtils.time(System.currentTimeMillis() + ""))//时间
                .build();


        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/historySave");

        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());

        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "上传识别记录" + ss);


                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }


}

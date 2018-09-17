package com.xiaojun.danrenbanmohe.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.xiaojun.danrenbanmohe.view.FaceView;

/**
 * Created by linyue on 16/1/2.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private Camera camera = null;
    private int hh;
    private CameraPreviewListener listener;
    private FaceView faceView;
    private float scaleW = 1;
    private float scaleH = 1;

    public void setH(int h, FaceView faceView){
        this.faceView=faceView;
        hh=h;
    }

    public CameraPreview(Context context) {
        super(context);
        init();
    }



    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    void setCamera(Camera camera) {
        this.camera = camera;
        restartPreview(getHolder());
    }

    private void restartPreview(SurfaceHolder holder) {
        if (camera != null) {
            if (holder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
            } catch (Exception e) {
            }

            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
//                camera.startFaceDetection();
                if (listener != null) {
                    listener.onStartPreview();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setScale(float scaleW, float scaleH) {
        this.scaleW = scaleW;
        this.scaleH = scaleH;
    }

    public void surfaceCreated(SurfaceHolder holder) {
        restartPreview(holder);


    }

    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        restartPreview(holder);

//        try {
//            RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) CameraPreview.this.getLayoutParams();
//            params.topMargin=-60;
//            params.height=(hh*5)/7;
//            params.bottomMargin=10;
//            CameraPreview.this.setLayoutParams(params);
//            CameraPreview.this.invalidate();
//
//            RelativeLayout.LayoutParams params2= (RelativeLayout.LayoutParams) faceView.getLayoutParams();
//            params2.topMargin=-60;
//            params2.height=(hh*5)/7;
//            params2.bottomMargin=10;
//            faceView.setLayoutParams(params2);
//            faceView.invalidate();
//        }catch (Exception e){
//            Log.d("CameraPreview", e.getMessage());
//        }

      //  Log.d("CameraPreview", "ddddddddddddd");
    }

    public void setListener(CameraPreviewListener listener) {
        this.listener = listener;
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        setMeasuredDimension((int) (width * scaleW), (int) (height * scaleH));
//    }

    public interface CameraPreviewListener {
        public void onStartPreview();
    }
}
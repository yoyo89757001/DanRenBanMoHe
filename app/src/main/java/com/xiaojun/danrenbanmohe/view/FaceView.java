package com.xiaojun.danrenbanmohe.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.xiaojun.danrenbanmohe.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangjingyi on 28/03/2017.
 */

public class FaceView extends View {
    private List<String> ids;
    private List<String> yaws;
    private List<String> pitchs;
    private List<String> rolls;
    private List<String> blurs;
    private List<String> ages;
    private List<String> genders;
    private List<Rect> rect;
    private Paint paint = new Paint();
    private Paint idPaint = new Paint();
    private Paint attributePaint = new Paint();
    private Paint backPaint = new Paint();
    private Bitmap bitmap= BitmapFactory.decodeResource(getResources(), R.drawable.kuang);
    private ValueAnimator animator=null;
    private float jiaodu=1;
    private float ziWith=0;

    private void initData() {
        ids = new ArrayList<String>();
        yaws = new ArrayList<>();
        pitchs = new ArrayList<>();
        rolls = new ArrayList<>();
        blurs = new ArrayList<>();
        rect = new ArrayList<Rect>();
        ages = new ArrayList<>();
        genders = new ArrayList<>();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4.0f);


        backPaint.setARGB(122, 255, 255, 255);
        backPaint.setStyle(Paint.Style.FILL);

        idPaint.setARGB(255, 80, 80, 80);
        idPaint.setTextSize(40);

        attributePaint.setColor(Color.WHITE);
        attributePaint.setTextSize(20);
        attributePaint.setAntiAlias(true);
        ziWith=attributePaint.measureText("需要正对屏幕");

        if (animator==null) {
            animator = new ValueAnimator();
            animator = ValueAnimator.ofFloat(1, 40);
            //动画时长，让进度条在CountDown时间内正好从0-360走完，
            animator.setDuration(800);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.setInterpolator(new LinearInterpolator());//匀速
            animator.setRepeatCount(-1);//表示不循环，-1表示无限循环
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    /**
                     * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                     * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                     * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                     */
                    jiaodu = Float.valueOf(animation.getAnimatedValue().toString()).intValue();
                   // MianBanJiView.this.invalidate();

                }
            });
            animator.start();
        }
    }

    public FaceView(Context context) {
        super(context);
        initData();
    }

    public FaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public FaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    public void addId(String label) {
        ids.add(label);
    }

    public void addYaw(String label) {
        yaws.add(label);
    }
    public void addPitch(String label) {
        pitchs.add(label);
    }
    public void addRoll(String label) {
        rolls.add(label);
    }
    public void addBlur(String label) {
        blurs.add(label);
    }
    public void addAge(String label) {
        ages.add(label);
    }
    public void addGenders(String label) {
        genders.add(label);
    }

    public void addRect(RectF rect) {
        Rect buffer = new Rect();
        buffer.left = (int) rect.left;
        buffer.top = (int) rect.top;
        buffer.right = (int) rect.right;
        buffer.bottom = (int) rect.bottom;
        this.rect.add(buffer);
    }

    public void clear() {
        rect.clear();
        ids.clear();
        yaws.clear();
        rolls.clear();
        blurs.clear();
        pitchs.clear();
        ages.clear();
        genders.clear();
    }




    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {

        for (int i = 0; i < rect.size(); i++) {
            Rect r = rect.get(i);
            canvas.drawBitmap(bitmap,null,r, null);

///            canvas.drawRect(r.right+5, r.top - 5, r.right + ids.get(i).length() * 25, r.top + 170 + 33 * 2, backPaint);
//            canvas.drawText(ids.get(i), r.right + 5, r.top + 30, idPaint);
//            canvas.drawText(yaws.get(i), r.right + 5, r.top + 60, attributePaint);
//            canvas.drawText(pitchs.get(i), r.right + 5, r.top + 93, attributePaint);
//            canvas.drawText(rolls.get(i), r.right + 5, r.top + 126, attributePaint);
            canvas.drawText(blurs.get(i), r.left + ((r.right-r.left)-ziWith)/2, r.bottom + 24, attributePaint);
//            canvas.drawText(ages.get(i), r.right + 5, r.top + 159 + 33, attributePaint);
            canvas.drawLine(r.left+6,r.bottom-((r.bottom-r.top)/40)*jiaodu,r.right-6,r.bottom-((r.bottom-r.top)/40)*jiaodu,paint);

            }
        this.clear();

            }catch (Exception e){
            Log.d("FaceView", e.getMessage());
        }
    }
}

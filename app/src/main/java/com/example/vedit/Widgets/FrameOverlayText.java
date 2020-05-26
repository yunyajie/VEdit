package com.example.vedit.Widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.vedit.Activities.WaterMarkActivity;
import com.example.vedit.Interface.ViewErrorListener;
import com.example.vedit.Utils.DimensionUtil;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Widgets
 * @ClassName: FrameOverlayText
 * @Description: 文字水印控件
 * @Author: yunyajie
 * @CreateDate: 2020/5/18 14:53
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/18 14:53
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FrameOverlayText extends View {
    private final String TAG="FrameOverText";
    //创建画笔，Paint.ANTI_ALIAS_FLAG:用于绘制时抗锯齿
    private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
    // 代码块，初始化一些属性
    {
        // 关闭硬件加速，注：不能在view级别开启硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        // 设置画笔颜色为白色
        paint.setColor(Color.WHITE);
        // 设置只绘制图形轮廓（描边）
        paint.setStyle(Paint.Style.STROKE);
        // 设置线宽
        paint.setStrokeWidth(6);
    }
    // 设置边距
    private int margin = 10;
    private RectF frameRect = new RectF();
    // 声明手势识别类
    private GestureDetector gestureDetector;


    //文字水印
    private String textWatermark="请添加文字";
    private Paint textPaint;

    private int fontLength=5;
    private int fontSize=40;
    private int fontColor=Color.WHITE;

    private ViewErrorListener viewErrorListener;

    public FrameOverlayText(Context context) {
        super(context);
        init();
    }

    public FrameOverlayText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameOverlayText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化工作
    private void init(){
        Log.e(TAG,"init");
        gestureDetector=new GestureDetector(getContext(),simpleOnGestureListener);
        textPaint=new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setStrokeWidth(8);
        textPaint.setTextSize(fontSize);
        textPaint.setColor(fontColor);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }


    //记录控件的高度和宽度
    int parentW=0,parentH=0;
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG,w+":"+h+":"+oldw+":"+oldh);
        //当前控件初始化的时候，会调用一次这个函数，此时初始化边框矩形
        //修改控件宽高
        parentW=w;
        parentH=h;
        initFrameRect(w,h);
    }

    //初始化边框矩形
    private void initFrameRect(int w, int h) {
        frameRect.left=0;
        frameRect.top=(int)(h*0.5);
        frameRect.right=frameRect.left+fontLength*fontSize;
        frameRect.bottom=frameRect.top+fontSize;
    }
    //调整矩形
    private void adjustFrameRect(){
        frameRect.right=frameRect.left+fontLength*fontSize;
        frameRect.bottom=frameRect.top+fontSize;
    }

    Paint.FontMetrics fontMetrics;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,"onDraw");
        //设置线的粗细
        paint.setStrokeWidth(DimensionUtil.dpTopx(1));
        //绘制矩形
        canvas.drawRect(frameRect,paint);
        //写字
        if (textWatermark!=null&&!textWatermark.equals("")){
            fontMetrics=textPaint.getFontMetrics();
            float diatance=(fontMetrics.bottom-fontMetrics.top)/2-fontMetrics.bottom;
            float baseline=frameRect.centerY()+diatance;
            canvas.drawText(textWatermark,frameRect.centerX(),baseline,textPaint);
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //拖动操作
            float ex=60;
            //兼容性处理，兼容偏移值为60px
            RectF rectExtend=new RectF(frameRect.left-ex,frameRect.top-ex,
                    frameRect.right+ex,frameRect.bottom+ex);
            if (rectExtend.contains(event.getX(),event.getY())){
                //将touch事件与手势识别绑定
                gestureDetector.onTouchEvent(event);
                return true;
            }
        return false;
    }



    //获取矩形的四个点坐标，方便截取矩形中的视频
    public Rect getFrameRect(){
        Rect rect=new Rect();
        rect.left=(int)frameRect.left;
        rect.top=(int)frameRect.top;
        rect.right=(int)frameRect.right;
        rect.bottom=(int)frameRect.bottom;
        return rect;
    }

    public Rect getParentInfo(){
        Rect rect=new Rect();
        rect.top=0;
        rect.left=0;
        rect.bottom=parentH;
        rect.right=parentW;
        return rect;
    }


    //****************处理矩形框缩放**********


    //***********处理拖动事件start***********
    //监听拖动事件，onDown->onScroll->onScroll->onFiling
    private GestureDetector.SimpleOnGestureListener simpleOnGestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //处理拖动事件，这个地方需要在onTouchEvent事件中将View的Event传递给GestureDetector的onTouchEvent,然后再调用移动方法
            translate(distanceX,distanceY);
            return true;
        }
    };

    //在屏幕上move的时候，处理矩形边框的拖动事件
    private void translate(float x, float y) {
        if (x>0){
            //moving left
            if (frameRect.left-x<margin){
                x=frameRect.left-margin;
            }
        }else {
            if (frameRect.right-x>getWidth()-margin){
                x=frameRect.right-getWidth()+margin;
            }
        }
        if (frameRect.top-y<margin){
            y=frameRect.top-margin;
        }else {
            if (frameRect.bottom-y>getHeight()-margin){
                y=frameRect.bottom-getHeight()+margin;
            }
        }
        frameRect.offset(-x,-y);
        invalidate();
    }
    //**************处理拖动事件end**********

    public void setTextWatermark(String textWatermark) {
        if (textWatermark.length()*fontSize>getWidth()){
            viewErrorListener.onError();
            return;
        }
        this.textWatermark = textWatermark;
        this.fontLength=textWatermark.length();
        Log.i(TAG,"字体长度****fontLength="+fontLength);
        initFrameRect(parentW,parentH);
        invalidate();
    }

    public void setFontSize(int textSize) {
        if (textWatermark.length()*textSize>getWidth()){
            viewErrorListener.onError();
            return;
        }
        this.fontSize = textSize;
        textPaint.setTextSize(textSize);
        adjustFrameRect();
        invalidate();
    }
    public void setFontColor(int fontcolor){
        textPaint.setColor(fontcolor);
        invalidate();
    }

    public void setViewErrorListener(ViewErrorListener viewErrorListener) {
        this.viewErrorListener = viewErrorListener;
    }

    public String getTextWatermark() {
        return textWatermark;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getFontColor() {
        return fontColor;
    }
}

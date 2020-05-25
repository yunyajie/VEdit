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

import com.example.vedit.Utils.DimensionUtil;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Widgets
 * @ClassName: FrameOverlayPic
 * @Description: java类作用描述
 * @Author: yunyajie
 * @CreateDate: 2020/5/25 18:27
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/25 18:27
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FrameOverlayPic extends View {

    private final String TAG="FrameOverPic";
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

    private int currentCorner = -1;
    private static final int CORNER_LEFT_TOP = 1;
    private static final int CORNER_RIGHT_TOP = 2;
    private static final int CORNER_RIGHT_BOTTOM = 3;
    private static final int CORNER_LEFT_BOTTOM = 4;
    // 设置边距
    private int margin = 20;
    // 设置角的线宽
    private int cornerLineWidth = 6;
    private int cornerLength = 20;
    private RectF touchRect = new RectF();
    private RectF frameRect = new RectF();
    // 声明手势识别类
    private GestureDetector gestureDetector;
    //添加的水印图片
    private Bitmap waterPic;
    private Paint paintPic=new Paint();



    public FrameOverlayPic(Context context) {
        super(context);
        init();
    }

    public FrameOverlayPic(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameOverlayPic(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化工作
    private void init(){
        Log.e(TAG,"init");
        gestureDetector=new GestureDetector(getContext(),simpleOnGestureListener);
        //设置角的长度
        cornerLength= DimensionUtil.dpTopx(18);
        //设置角线的宽度
        cornerLineWidth=DimensionUtil.dpTopx(3);
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
        frameRect.left=(int)(w*0.05);
        frameRect.top=(int)(h*0.25);
        frameRect.right=w-frameRect.left;
        frameRect.bottom=h-frameRect.top;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG,"onDraw");
        if (waterPic!=null){
            Log.e(TAG,"绘制图片");
            canvas.drawBitmap(waterPic,null,frameRect,paintPic);
        }
        //绘制四个角
        drawConers(canvas);

    }

    //绘制四个角
    private void drawConers(Canvas canvas) {
        paint.setStrokeWidth(cornerLineWidth);
        //left top左上角
        drawLine(canvas,frameRect.left-cornerLineWidth/2,frameRect.top,cornerLength,0);
        drawLine(canvas,frameRect.left,frameRect.top,0,cornerLength);
        //right top右上角
        drawLine(canvas,frameRect.right+cornerLineWidth/2,frameRect.top,-cornerLength,0);
        drawLine(canvas,frameRect.right,frameRect.top,0,cornerLength);
        //right bottom右下角
        drawLine(canvas,frameRect.right,frameRect.bottom,0,-cornerLength);
        drawLine(canvas,frameRect.right+cornerLineWidth/2,frameRect.bottom,-cornerLength,0);
        //left bottom左下角
        drawLine(canvas,frameRect.left-cornerLineWidth/2,frameRect.bottom,cornerLength,0);
        drawLine(canvas,frameRect.left,frameRect.bottom,0,-cornerLength);
    }
    //画线
    private void drawLine(Canvas canvas, float x, float y, int dx, int dy) {
        canvas.drawLine(x,y,x+dx,y+dy,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //先处理是否是矩形框伸缩
        boolean result=handleDown(event);
        //如果不是伸缩操作，则是拖动操作
        if(!result){
            float ex=60;
            //兼容性处理，兼容偏移值为60px
            RectF rectExtend=new RectF(frameRect.left-ex,frameRect.top-ex,
                    frameRect.right+ex,frameRect.bottom+ex);
            if (rectExtend.contains(event.getX(),event.getY())){
                //将touch事件与手势识别绑定
                gestureDetector.onTouchEvent(event);
                return true;
            }
        }
        return result;
    }

    //**************处理矩形框缩放start***********
    private boolean handleDown(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                currentCorner=-1;
                break;
            case MotionEvent.ACTION_DOWN:
                //按下时判断按的是哪个角
                float radius=cornerLength;
                //按下时设置矩形的四角坐标
                touchRect.set(event.getX()-radius,event.getY()-radius,event.getX()+radius,event.getY()+radius);
                //缩小那个角
                if (touchRect.contains(frameRect.left,frameRect.top)){
                    currentCorner=CORNER_LEFT_TOP;
                    return true;
                }
                if (touchRect.contains(frameRect.right,frameRect.top)){
                    currentCorner=CORNER_RIGHT_TOP;
                    return true;
                }
                if (touchRect.contains(frameRect.right, frameRect.bottom)) {
                    currentCorner = CORNER_RIGHT_BOTTOM;
                    return true;
                }

                if (touchRect.contains(frameRect.left, frameRect.bottom)) {
                    currentCorner = CORNER_LEFT_BOTTOM;
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                //移动的时候处理缩放
                return handleScale(event);
            default:
        }
        return false;
    }

    //处理伸缩事件
    private boolean handleScale(MotionEvent event) {
        //判断缩放的是哪个角
        switch (currentCorner) {
            case CORNER_LEFT_TOP:
                // 当拖动左上角的时候，右下角的坐标是不变的
                scaleTo(event.getX(), event.getY(), frameRect.right, frameRect.bottom);
                return true;
            case CORNER_RIGHT_TOP:
                // 当拖动右上角的时候，左下角是不变的
                scaleTo(frameRect.left, event.getY(), event.getX(), frameRect.bottom);
                return true;
            case CORNER_RIGHT_BOTTOM:
                // 当拖动右下角的时候，左上角是不变的
                scaleTo(frameRect.left, frameRect.top, event.getX(), event.getY());
                return true;
            case CORNER_LEFT_BOTTOM:
                // 当拖动左下角的时候，右上角时不变的
                scaleTo(event.getX(), frameRect.top, frameRect.right, event.getY());
                return true;
            default:
                return false;
        }
    }

    //移动时缩放矩形
    private void scaleTo(float left, float top, float right, float bottom) {
        // 当高度缩放到最大时
        if (bottom - top < getMinimumFrameHeight()) {
            top = frameRect.top;
            bottom = frameRect.bottom;
        }
        //当宽度缩放到最大时
        if (right - left < getMinimumFrameWidth()) {
            left = frameRect.left;
            right = frameRect.right;
        }
        left = Math.max(margin, left);
        top = Math.max(margin, top);
        right = Math.min(getWidth() - margin, right);
        bottom = Math.min(getHeight() - margin, bottom);
        // 重绘矩形
        frameRect.set(left, top, right, bottom);
        invalidate();
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
    //获取矩形最小宽度
    private float getMinimumFrameWidth(){
        return 2.4f*cornerLength;
    }
    //获取矩形最小高度
    private float getMinimumFrameHeight(){
        return 2.4f*cornerLength;
    }


    public void setWaterPic(Bitmap waterPic) {
        this.waterPic = waterPic;
        invalidate();
    }
}

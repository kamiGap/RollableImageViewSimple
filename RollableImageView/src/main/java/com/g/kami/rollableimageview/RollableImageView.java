package com.g.kami.rollableimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.g.kami.rollableimageview.RollableImageView.RollDirection.DOWN;
import static com.g.kami.rollableimageview.RollableImageView.RollDirection.LEFT;
import static com.g.kami.rollableimageview.RollableImageView.RollDirection.RIGHT;
import static com.g.kami.rollableimageview.RollableImageView.RollDirection.UP;
import static java.lang.Math.abs;
import static java.lang.Math.min;

public class RollableImageView extends AppCompatImageView {

    private final int DEFUALT_DURATION = 400;

    private Bitmap mBitmap; // 需要绘制的图片

    private int repeatTime; // 图片重复播放的次数

    private int duration; // 图片重播的持续时间

    private OverScroller mOverScroller;

    private int bitmapWidth, bitmapHeight; // 图片的宽高信息

    private int nowPoint; // 绘制图片最左的位置

    private Rect endPart, startPart;

    private Rect canvasPartOne, canvasPartTwo;

    private boolean isInfinite = false; // 是否无限播放

    private boolean isAutoPlay; // 是否自动播放

    @RollDirection
    private int mRollDirection = 1;

    public RollableImageView(Context context) {
        this(context, null);
    }

    public RollableImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOverScroller = new OverScroller(context, new LinearInterpolator());
        startPart = new Rect();
        endPart = new Rect();
        canvasPartOne = new Rect();
        canvasPartTwo = new Rect();
        initBitmap();
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.RollableImageView);
        setAttributes(ta);
        ta.recycle();
        if (isAutoPlay)
            doAutoPlay();
    }

    private void initBitmap(){
        Drawable srcDrawable = getDrawable();
        if (null != srcDrawable){
            mBitmap = Bitmap.createBitmap(srcDrawable.getIntrinsicWidth(),
                    srcDrawable.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(mBitmap);
            srcDrawable.setBounds(0,
                    0,
                    srcDrawable.getIntrinsicWidth(),
                    srcDrawable.getIntrinsicHeight());
            srcDrawable.draw(c);
        }
    }

    private void setAttributes(TypedArray typedArray){
        mRollDirection = typedArray.getInteger(R.styleable.RollableImageView_riv_rolldirection, 1);
        repeatTime = typedArray.getInteger(R.styleable.RollableImageView_riv_repeatTime, 0);
        duration = typedArray.getInteger(R.styleable.RollableImageView_riv_duration, DEFUALT_DURATION);
        isAutoPlay = typedArray.getBoolean(R.styleable.RollableImageView_riv_auto_play, false);
    }

    private void doAutoPlay(){
        duration = duration > 0 ? duration : DEFUALT_DURATION;
        repeatTime = repeatTime >= 0 ? repeatTime : 0;
        if (repeatTime == 0)
            startRollInfinite(duration, mRollDirection);
        else
            startRoll(repeatTime, duration, mRollDirection);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isRollHorizontal())
            drawHorizontalAnim(canvas);
        else
            drawVerticalAnim(canvas);
    }

    private void drawHorizontalAnim(Canvas canvas){
        if (nowPoint + getWidth() > bitmapWidth) {
            startPart.set(nowPoint, 0, bitmapWidth, bitmapHeight);
            canvasPartOne.set(0, 0, bitmapWidth - nowPoint, getHeight());
            canvas.drawBitmap(mBitmap, startPart, canvasPartOne, null);
            endPart.set(0, 0, nowPoint + getWidth() - bitmapWidth, bitmapHeight);
            canvasPartTwo.set(bitmapWidth - nowPoint, 0, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, endPart, canvasPartTwo, null);
        } else {
            startPart.set(nowPoint, 0, nowPoint + getWidth(), bitmapHeight);
            canvasPartOne.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, startPart, canvasPartOne, null);
        }
    }

    private void drawVerticalAnim(Canvas canvas){
        if (nowPoint + getHeight() > bitmapHeight) {
            startPart.set(0, nowPoint, bitmapWidth, bitmapHeight);
            canvasPartOne.set(0, 0, getWidth(), bitmapHeight - nowPoint);
            canvas.drawBitmap(mBitmap, startPart, canvasPartOne, null);
            endPart.set(0, 0, bitmapWidth, nowPoint + getHeight() - bitmapHeight);
            canvasPartTwo.set(0, bitmapHeight - nowPoint, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, endPart, canvasPartTwo, null);
        } else {
            startPart.set(0, nowPoint, bitmapWidth, nowPoint + getHeight());
            canvasPartOne.set(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(mBitmap, startPart, canvasPartOne, null);
        }
    }

    public void setImageBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        updateBitmap();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int defaultWidth = mBitmap.getWidth();
        final int defaultHeight = mBitmap.getHeight();
        int width = getMeasurSize(defaultWidth, widthMeasureSpec);
        int height = getMeasurSize(defaultHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getMeasurSize(int defaultSize, int sizeSpec) {
        final int mode = MeasureSpec.getMode(sizeSpec);
        final int size = MeasureSpec.getSize(sizeSpec);
        int result = defaultSize;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                break;
            case MeasureSpec.AT_MOST:
                result = min(size, defaultSize);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(l, t, r, b);
        updateBitmap();
    }

    protected void updateBitmap() {
        int h = getHeight(); // height
        int w = getWidth(); // width
        int destHeight = mBitmap.getHeight(); // bitmapHeight
        int destWidth = mBitmap.getWidth();
        if (destHeight < h)
            destHeight = h;
        if (destWidth < w)
            destWidth = w;
        mBitmap = Bitmap.createScaledBitmap(mBitmap, destWidth, destHeight, false);
        bitmapWidth = mBitmap.getWidth();
        bitmapHeight = mBitmap.getHeight();
    }

    @Override
    public void computeScroll() {
        if (mOverScroller.computeScrollOffset()) {
            if (isRollHorizontal()) {
                final int x = mOverScroller.getCurrX();
                nowPoint = abs(x);
            }else {
                final int y = mOverScroller.getCurrY();
                nowPoint = abs(y);
            }
            postInvalidateOnAnimation();
        } else if (isInfinite) {
            rollInfinite(mRollDirection);
            postInvalidateOnAnimation();
        }
    }

    private boolean isRollHorizontal() {
        return mRollDirection == RollDirection.RIGHT || mRollDirection == RollDirection.LEFT;
    }

    public void startRollInfinite(@IntRange(from = 0) int oneTimeDuration, @RollDirection int rollDirection) {
        isInfinite = true;
        duration = oneTimeDuration;
        mRollDirection = rollDirection;
        rollInfinite(rollDirection);
        postInvalidateOnAnimation();
    }

    private void rollInfinite(@RollDirection int rollDirection) {
        switch (rollDirection) {
            case RIGHT:
                mOverScroller.startScroll(0, 0, bitmapWidth, 0, duration);
                break;
            case LEFT:
                mOverScroller.startScroll(bitmapWidth, 0, -bitmapWidth, 0, duration);
                break;
            case UP:
                mOverScroller.startScroll(0, 0, 0, bitmapHeight, duration);
                break;
            case DOWN:
                mOverScroller.startScroll(0, bitmapHeight, 0, -bitmapHeight, duration);
                break;
        }
    }

    public void startRoll(@IntRange(from = 1) int repeatTime,
                          @IntRange(from = 0) int duration,
                          @RollDirection int rollDirection) {
        isInfinite = false;
        this.duration = duration;
        final int scrollWidth = bitmapWidth * repeatTime;
        final int scrollHeight = bitmapHeight * repeatTime;
        mRollDirection = rollDirection;
        switch (rollDirection) {
            case RIGHT:
                mOverScroller.startScroll(0, 0, scrollWidth, 0, duration);
                break;
            case LEFT:
                mOverScroller.startScroll(scrollWidth, 0, -scrollWidth, 0, duration);
                break;
            case UP:
                mOverScroller.startScroll(0, 0, 0, scrollHeight, duration);
                break;
            case DOWN:
                mOverScroller.startScroll(0, scrollHeight, 0, -scrollHeight, duration);
                break;
        }
        postInvalidateOnAnimation();
    }

    @IntDef
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @Retention(RetentionPolicy.CLASS)
    public @interface RollDirection {
        int RIGHT = 1;
        int LEFT = 2;
        int UP = 3;
        int DOWN = 4;
    }
}

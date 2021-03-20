
package com.lcx.scanner;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public final class ScannerView extends View implements ValueAnimator.AnimatorUpdateListener {

    private Paint paint;
    private int maskColor;
    private int position = 0;
    private float triangleLength;
    private Rect frame;
    private Context mContext;
    private String msg;
    private int textColor;
    private int strokeColor;
    private float strokeWidth;
    private ValueAnimator animator;

    public ScannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs == null) {
            return;
        }
        mContext = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskColor = Color.parseColor("#60000000");
        setAttr(context, attrs);
    }

    private void setAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScannerView);
        msg = typedArray.getString(R.styleable.ScannerView_msg);
        if (msg == null) {
            msg = "请将二维码置于扫描框内";
        }
        strokeColor = typedArray.getColor(R.styleable.ScannerView_stroke_color, 0xff03a9f4);
        textColor = typedArray.getColor(R.styleable.ScannerView_msg_color, 0xffc8c8c8);
        triangleLength = typedArray.getDimension(R.styleable.ScannerView_stroke_length, dp2px(context, 15));
        strokeWidth = typedArray.getDimension(R.styleable.ScannerView_stroke_width, dp2px(mContext, 2));
        typedArray.recycle();
    }

    public void setScanRect(Rect frame) {
        this.frame = frame;
        animator = ValueAnimator.ofInt(0, frame.height());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(this);
        animator.setDuration(1500);
        animator.start();
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (frame == null) {
            return;
        }
        if(position == 0) {
            position = frame.top;
        }
        int width = getWidth();
        int height = getHeight();
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top, paint);
        canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
        canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
        canvas.drawRect(0, frame.bottom + 1, width, height, paint);
        drawRightTriangle(canvas, paint, frame.left, frame.top, frame.height());
        paint.setColor(textColor);
        paint.setTextSize(sp2px(mContext, 14));
        float textWidth = paint.measureText(msg);
        float textStart = (width - textWidth)/2;
        canvas.drawText(msg, textStart, frame.bottom + dp2px(mContext, 30), paint);
        paint.setColor(strokeColor);
        paint.setStrokeWidth(dp2px(mContext, 1));
        canvas.drawLine(frame.left, position, frame.left + frame.height(), position, paint);
        if(position >= frame.top + frame.height()) {
            position = frame.top;
        }
    }

    private void drawRightTriangle(Canvas canvas, Paint paint, int x, int y, int height) {
        paint.setColor(strokeColor);
        paint.setStrokeWidth(strokeWidth);
        canvas.drawLine(x - strokeWidth/2, y, x + triangleLength, y, paint);
        canvas.drawLine(x, y, x, y + triangleLength, paint);
        canvas.drawLine(x + height - triangleLength, y, x + height + strokeWidth/2, y, paint);
        canvas.drawLine(x + height, y, x + height, y + triangleLength, paint);
        canvas.drawLine(x, y + height, x, y + height - triangleLength, paint);
        canvas.drawLine(x - strokeWidth/2, y + height, x + triangleLength, y + height, paint);
        canvas.drawLine(x + height + strokeWidth/2, y + height, x + height - triangleLength,
                y + height, paint);
        canvas.drawLine(x + height, y + height, x + height, y + height - triangleLength, paint);
    }

    //dp转px
    public static int dp2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //sp转px
    public static int sp2px(Context context, float spValue) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * scale + 0.5f);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        int value = (int) animation.getAnimatedValue();
        position = frame.top + value;
        invalidate();
    }

    public void stop() {
        if (animator != null) {
            animator.cancel();
        }
    }
}

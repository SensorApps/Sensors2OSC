package org.sensors2.osc.views;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.sensors2.osc.fragments.MultiTouchFragment;

public class MultiTouchView extends View {

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    private int[] touching = new int[MultiTouchFragment.MAX_POINTER_COUNT];
    private float[] x = new float[MultiTouchFragment.MAX_POINTER_COUNT];
    private float[] y = new float[MultiTouchFragment.MAX_POINTER_COUNT];

    public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiTouchView(Context context) {
        super(context);
        init();
    }

    void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
    }

    // TODO improve the drawing performance and make it scale to screen size
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < touching.length; i++) {
            if (touching[i] != 0) {
                for (int j = 0; j < i + 1; j++) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle(x[i], y[i], 75 - (j * 6), paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = (event.getAction() & MotionEvent.ACTION_MASK);
        int pointCount = event.getPointerCount();

        for (int i = 0; i < pointCount; i++) {
            int id = event.getPointerId(i);

            // Ignore pointer higher than our max
            if (id < touching.length) {
                x[id] = (int) event.getX(i);
                y[id] = (int) event.getY(i);

                if ((action == MotionEvent.ACTION_DOWN)
                        || (action == MotionEvent.ACTION_POINTER_DOWN)
                        || (action == MotionEvent.ACTION_MOVE)) {
                    touching[id] = 2;

                } else {
                    touching[id] = 0;
                }
            }
        }

        invalidate();
        return true;
    }
}

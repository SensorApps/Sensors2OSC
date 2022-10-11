package org.sensors2.osc.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.sensors2.osc.R;
import org.sensors2.osc.fragments.MultiTouchFragment;

public class MultiTouchView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int[] touching = new int[MultiTouchFragment.MAX_POINTER_COUNT];
    private final float[] x = new float[MultiTouchFragment.MAX_POINTER_COUNT];
    private final float[] y = new float[MultiTouchFragment.MAX_POINTER_COUNT];
    private int drawingColor;
    private final Context context;
    private int touchSize;

    public MultiTouchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public MultiTouchView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        TypedValue typedValue = new TypedValue();
        this.context.getTheme().resolveAttribute(R.attr.colorOnSurface, typedValue, true);
        this.drawingColor = typedValue.data;
        paint.setColor(this.drawingColor);
    }

    // TODO: improve drawing performance
    @SuppressLint("CanvasSize")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.touchSize == 0) {
            this.touchSize = Math.min(Math.min(canvas.getWidth(), canvas.getHeight()) / 10, 75);
        }
        for (int i = 0; i < touching.length; i++) {
            if (touching[i] != 0) {
                for (int j = 0; j < i + 1; j++) {
                    paint.setColor(this.drawingColor);
                    canvas.drawCircle(x[i], y[i], this.touchSize - (j * 6), paint);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
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

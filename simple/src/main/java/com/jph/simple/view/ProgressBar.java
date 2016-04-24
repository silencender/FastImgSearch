package com.jph.simple.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 15-6-18.
 */
public class ProgressBar extends View {
    private int progress = 1;
    private final static int HEIGHT = 5;
    private Paint paint;
    private final static int colors[] = new int[]{Color.RED, Color.RED};
    public ProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressBar(Context context) {
        super(context);
        LinearGradient shader = new LinearGradient(
                0, 0,
                100, HEIGHT,
                colors,
                null,
                Shader.TileMode.MIRROR);
        paint=new Paint(Paint.DITHER_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(HEIGHT);
        paint.setAntiAlias(true);
        paint.setShader(shader);
    }
    public void setProgress(int progress){
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth() * progress / 100, HEIGHT, paint);
    }
}

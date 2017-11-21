package com.hzx.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义蜘蛛网图
 * Created by Hman on 2017/10/27.
 */
public class SpiderView  extends View {

    private Paint fiveLinePaint,showdowPaint,showdowToPaint,textPaint,minShowPaint;
    private float x, y;
    // 线的长度
    private float r;
    // 线的条数
    private int count = 6;
    // 夹角度数
    private double angle = 360/count;

    private double rArray[] = {9.0/11.0, 3.0/4.0, 3.0/5.0, 2.0/3.0, 4.0/7.0, 7.0/9.0, 4.0/5.0};
    private double rArrayTo[] = {1.0/3.0, 1.0/2.0, 2.0/5.0, 5.0/6.0, 3.0/7.0, 5.0/9.0, 3.0/5.0};
    private double minArray[] = {1.0/2.0, 1.0/3.0, 1.0/5.0, 1.0/4.0, 2.0/7.0, 2.0/9.0, 1.0/5.0};
    private String[] titles = {"ph值","浑浊度","电导率","溶解氧","色度","温度","湿度"};

    // 直线颜色
    private int beelineColor = Color.RED;
    // 第一区域阴影颜色
    private int footColor = Color.YELLOW;
    // 第二区域阴影颜色、表面区域颜色
    private int faceColor = Color.BLUE;
    // 下区域透明度 0到255之间 越小越透明
    private int footAlpha = 79;
    // 表面区域透明度 0到255之间
    private int faceAlpha = 125;

    public SpiderView(Context context) {
        super(context);
        init();
    }

    public SpiderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpiderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 五边形
        fiveLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fiveLinePaint.setColor(beelineColor);
        fiveLinePaint.setStyle(Paint.Style.STROKE);

        // 第一阴影区
        showdowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        showdowPaint.setColor(footColor);
        showdowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 第二阴影区
        showdowToPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        showdowToPaint.setColor(faceColor);
        showdowToPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 第三区域
        minShowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        minShowPaint.setColor(beelineColor);
        minShowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // 写字
        textPaint = new Paint();
        textPaint.setTextSize(20);
        textPaint.setStyle(Paint.Style.FILL); // 填充
        textPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.r = Math.min(h, w)/2*0.9f;
        this.x = w/2;
        this.y = h/2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSixLine(canvas);
        drawShadow(canvas, showdowPaint, rArray, footAlpha);
        drawShadow(canvas, minShowPaint, minArray, footAlpha);
        drawShadow(canvas, showdowToPaint, rArrayTo, faceAlpha);
        drawText(canvas);
    }

    // 画直线
    private void drawSixLine(Canvas canvas) {
        Path path = new Path();

        // 直接画图
        for (int i = 0; i < count; i++) {
            path.moveTo(x, y);
            path.lineTo((float)(x + r*Math.sin(Math.toRadians(i*angle))), (float)(y - r*Math.cos(Math.toRadians(i*angle))));
            path.close();
            canvas.drawPath(path, fiveLinePaint);
        }
        // 通过选择画图
//        for (int i = 0; i < count ; i++) {
//            canvas.rotate(60,x, y);
//            canvas.drawPath(path, fiveLinePaint);
//        }
    }

    private void drawShadow(Canvas canvas, Paint paint, double array[], int alpha) {
        Path path = new Path();
        float xx, yy;
        paint.setAlpha(255);
        for (int i = 0; i < count; i++) {
            xx = (float)(x + array[i]*r*Math.sin(Math.toRadians(i*angle)));
            yy = (float)(y - array[i]*r*Math.cos(Math.toRadians(i*angle)));
            if (i == 0) {
                path.moveTo(xx, yy);
            } else {
                path.lineTo(xx, yy);
            }
//            canvas.drawCircle((float)(x + rArray[i]*Math.sin(Math.toRadians(i*angle))), (float)(y - rArray[i]*Math.cos(Math.toRadians(i*angle))), 10, showdowToPaint);
        }
        path.lineTo((float)(x + array[0]*r*Math.sin(Math.toRadians(0))), (float)(y - array[0]*r*Math.cos(Math.toRadians(0))));

        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        paint.setAlpha(alpha);
        //绘制填充区域
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, paint);
    }
    // 写字
    private void drawText(Canvas canvas) {
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();

        for(int i=0;i<count;i++){
            float xx = (float)(x + r*Math.sin(Math.toRadians(i*angle)));
            float yy = (float)(y - r*Math.cos(Math.toRadians(i*angle)));
            if(angle*i >= 0 && angle*i <= 90){//第1象限
                canvas.drawText(titles[i], xx,yy,textPaint);
            }else if(angle*i >= 270 && angle*i <= 360){//第4象限
                float dis = textPaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], xx-dis,yy,textPaint);
            }else if(angle*i >= 90 && angle*i < 180){//第2象限
                canvas.drawText(titles[i], xx,yy,textPaint);
            }else if(angle*i > 180 && angle*i < 270){//第3象限
                float dis = textPaint.measureText(titles[i]);//文本长度
                canvas.drawText(titles[i], xx-dis,yy,textPaint);
            } else if(angle*i == 180) {
                canvas.drawText(titles[i], xx,yy + 20,textPaint);
            } else if(angle*i == 0) {
                canvas.drawText(titles[i], xx,yy - 10,textPaint);
            }
        }

    }

    public int getCount() {
        return count;
    }

    // 设置线条数目
    public void setCount(int count) {
        this.count = count;
    }

    public double[] getrArray() {
        return rArray;
    }

    // 设置第一层阴影点的比例
    public void setrArray(double[] rArray) {
        this.rArray = rArray;
    }

    public double[] getrArrayTo() {
        return rArrayTo;
    }

    // 设置第二层阴影点的比例
    public void setrArrayTo(double[] rArrayTo) {
        this.rArrayTo = rArrayTo;
    }

    public String[] getTitles() {
        return titles;
    }

    // 最小区域
    public double[] getMinArray() {
        return minArray;
    }

    public void setMinArray(double[] minArray) {
        this.minArray = minArray;
    }
    // 设置边角文字
    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public int getFaceAlpha() {
        return faceAlpha;
    }

    public int getFootAlpha() {
        return footAlpha;
    }

    public int getFaceColor() {
        return faceColor;
    }

    public int getFootColor() {
        return footColor;
    }
    public int getBeelineColor() {
        return beelineColor;
    }

    // 设置透明度 0~255
    public void setFaceAlpha(int faceAlpha) {
        this.faceAlpha = faceAlpha;
    }

    public void setFootAlpha(int footAlpha) {
        this.footAlpha = footAlpha;
    }

    // 设置表面颜色 Color类中的颜色或者如 ContextCompat.getColor(context, R.color.black); Android6.0之后
    public void setFaceColor(int faceColor) {
        showdowToPaint.setColor(faceColor);
    }

    public void setFootColor(int footColor) {
        showdowPaint.setColor(footColor);
    }

    public void setBeelineColor(int beelineColor) {
        fiveLinePaint.setColor(beelineColor);
    }

}


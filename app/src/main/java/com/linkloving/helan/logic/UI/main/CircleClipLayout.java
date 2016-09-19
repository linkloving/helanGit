package com.linkloving.helan.logic.UI.main;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.linkloving.helan.R;


/**
 * Created by kong on 5/24/16.
 *
 * # 以中点为圆形, 裁剪出一个圆形区域;
 * # 圆的半径为短的那个边减掉两侧的padding;
 */
public class CircleClipLayout extends FrameLayout
{
    public CircleClipLayout(Context context)
    {
        super(context);
        doInit(context, null);
    }

    public CircleClipLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        doInit(context, attrs);
    }

    public CircleClipLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        doInit(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleClipLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        doInit(context, attrs);
    }

    protected void doInit(Context context, AttributeSet attrs)
    {
        if (null != attrs)
        {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleClipLayout);
            m_borderWidth = a.getDimension(R.styleable.CircleClipLayout_app_borderWidth, 3);
        }

        setLayerType(View.LAYER_TYPE_SOFTWARE, null); //canvas.clipPath必须关闭硬件加速;

        if (m_borderWidth > 0)
        {
            m_borderPaintOrNull = new Paint(Paint.ANTI_ALIAS_FLAG);
            m_borderPaintOrNull.setDither(true);
            m_borderPaintOrNull.setColor(m_borderColor); //borderColor
            m_borderPaintOrNull.setStrokeWidth(m_borderWidth); //borderWidth
            m_borderPaintOrNull.setStyle(Paint.Style.FILL);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas)
    {

        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG));
        //#padding区域内的矩形区域内裁剪;圆心是: (width - pl, height - pt)
        //#以短的边为圆的半径;

        int pl = getPaddingLeft();
        int pt = getPaddingTop();
        int pr = getPaddingRight();
        int pb = getPaddingBottom();

        int w = getWidth();
        int h = getHeight();

        int wContent = w - pl - pr;
        int hContent = h - pt - pb;

        int cx = (int) (pl + wContent * 0.5f);
        int cy = (int) (pt + hContent * 0.5f);

        int shortLen = wContent < hContent ? wContent : hContent;
        int radius = (int)(shortLen * 0.5f);

        //画border;
        if (null != m_borderPaintOrNull)
        {
            canvas.drawCircle(cx, cy, radius, m_borderPaintOrNull);
        }

        //裁剪头像;
        int sc = canvas.save(Canvas.CLIP_SAVE_FLAG);

        m_pathForClip.reset();
        m_pathForClip.addCircle(cx, cy,
                                radius,
                                Path.Direction.CCW);
        canvas.clipPath(m_pathForClip);

        super.dispatchDraw(canvas);

        canvas.restoreToCount(sc);
    }

    private static final String TAG = CircleClipLayout.class.getName();

    //#描边宽度;
    protected float m_borderWidth;
    //#描边颜色;
    protected int m_borderColor;

    public void setM_borderColor(int m_borderColor) {
        this.m_borderColor = m_borderColor;
    }

    protected Paint m_borderPaintOrNull;
//    protected Paint m_sharePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected Path m_pathForClip = new Path();

}

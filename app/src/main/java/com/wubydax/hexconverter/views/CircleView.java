package com.wubydax.hexconverter.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.wubydax.hexconverter.R;

/*      Created by Roberto Mariani and Anna Berkovitch, 28/06/2016
        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

public class CircleView extends View {

    private int mFillColor;
    private Paint mCirclePaint;
    private RectF mViewRect;

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleView, 0, 0);
        try {
            mFillColor = typedArray.getColor(R.styleable.CircleView_fillColor, Color.RED);
        } finally {
            typedArray.recycle();
            init();
        }
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(mFillColor);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        mViewRect = new RectF(0, 0, params.width, params.height);
    }

    public void setFillColor(int color) {
        mFillColor = color;
        mCirclePaint.setColor(color);
        invalidate();
        requestLayout();
    }

    public int getFillColor() {
        return mFillColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawOval(mViewRect, mCirclePaint);

    }

}

package com.lisb.adjustable_imageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AdjustableImageView extends ImageView {

    private boolean adjustViewBounds;

    public AdjustableImageView(Context context) {
        super(context);
    }

    public AdjustableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        this.adjustViewBounds = adjustViewBounds;
        super.setAdjustViewBounds(adjustViewBounds);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (adjustViewBounds) {
            final Drawable drawable = getDrawable();
            if (drawable == null) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }

            final int drawableWidth = drawable.getIntrinsicWidth();
            final int drawableHeight = drawable.getIntrinsicHeight();

            if (drawableWidth == 0 || drawableHeight == 0) {
                // intrinsicWidth や intrinsicHeight の幅が0のときがあるので 0 じゃないかチェック。
                // Android 2.3系列でこの現象が発生することはなかったが、
                // Sony Tablet Sで試したところ、ImageViewにnullをセットすると、
                // Drawable自体はnullにならずに、幅・高さが0になりエラーになったので急遽対応した。
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }

            final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

            final int paddingHorizontal = getPaddingLeft() + getPaddingRight();
            final int paddingVertical = getPaddingTop() + getPaddingBottom();

            final int coreWidthSize = widthSize - paddingHorizontal;
            final int coreHeightSize = heightSize - paddingVertical;

            if (heightMode == MeasureSpec.EXACTLY && widthMode != MeasureSpec.EXACTLY) {
                // Fixed Height & Adjustable Width
                final int width = coreHeightSize * drawableWidth / drawableHeight + paddingHorizontal;
                setMeasuredDimension(Math.min(width, widthSize), heightSize);
            } else if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
                // Fixed Width & Adjustable Height
                final int height = coreWidthSize * drawableHeight / drawableWidth + paddingVertical;
                setMeasuredDimension(widthSize, Math.min(height, heightSize));
            } else {
                // Adjustable Width & Adjustable Height
                final double widthScale = (double) coreWidthSize / drawableWidth;
                final double heightScale = (double) coreHeightSize / drawableHeight;
                if (widthScale == heightScale) {
                    setMeasuredDimension(widthSize, heightSize);
                } else if (widthScale < heightScale) {
                    setMeasuredDimension(widthSize, (int) (drawableHeight * coreWidthSize / drawableWidth + paddingVertical));
                } else {
                    setMeasuredDimension((int) (drawableWidth * coreHeightSize / drawableHeight + paddingHorizontal), heightSize);
                }
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
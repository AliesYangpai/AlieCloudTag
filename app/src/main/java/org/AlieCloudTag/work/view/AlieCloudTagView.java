package org.AlieCloudTag.work.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alie on 2019/3/3.
 * 类描述
 * 版本
 */
public class AlieCloudTagView extends ViewGroup {

    private List<Integer> heights;
    private List<List<View>> allLineViews;

    public AlieCloudTagView(Context context) {
        super(context);
        heights = new ArrayList<>();
        allLineViews = new ArrayList<>();
    }

    public AlieCloudTagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        heights = new ArrayList<>();
        allLineViews = new ArrayList<>();
    }

    public AlieCloudTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        heights = new ArrayList<>();
        allLineViews = new ArrayList<>();
    }

    /**
     * 由于后面需要使用子view的边距，因此这里需要重写此方法，避免在强转时出错
     * （如果这个自定义view继承自 RaleitveLayout，则不需重写此方法）
     *
     * @param attrs
     * @return
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightmode = MeasureSpec.getMode(heightMeasureSpec);

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;

        if (parentHeightmode == MeasureSpec.EXACTLY && parentWidthMode == MeasureSpec.EXACTLY) {
            measureWidth = parentWidthSize;
            measureHeight = parentHeightSize;
        } else {

            int currentAddedWidth = 0; // 当前行内累加的宽度 （控件宽度）
            int currentAddedHeight = 0; // 当前行内累加的高度 （控件高度）
            int childCount = this.getChildCount();

            List<View> singleLineViews = new ArrayList<>();
            for (int i = 0; i < childCount; i++) {
                View childView = this.getChildAt(i);
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();

                int currentWidth = childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int currentHeight = childView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
                if (currentAddedWidth + currentWidth > parentWidthSize) {
                    measureWidth = Math.max(measureWidth, currentAddedWidth);
                    measureHeight += currentAddedHeight;

                    allLineViews.add(singleLineViews);
                    heights.add(currentAddedHeight);

                    currentAddedWidth = currentWidth;
                    currentAddedHeight = currentHeight;

                    singleLineViews = new ArrayList<>();
                    singleLineViews.add(childView);

                } else {
                    currentAddedWidth += currentWidth;
                    currentAddedHeight = Math.max(currentAddedHeight, currentHeight);
                    singleLineViews.add(childView);
                }

                if (i == childCount - 1) {
                    measureWidth = Math.max(measureWidth, currentAddedWidth);
                    measureHeight += currentAddedHeight;
                    heights.add(currentAddedHeight);
                    allLineViews.add(singleLineViews);
                }
            }

        }
        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = 0;
        int right = 0;
        int bottom = 0;

        int currentLeft = 0;
        int currentTop = 0;
        for (int i = 0; i < allLineViews.size(); i++) {
            List<View> views = allLineViews.get(i);
            for (int j = 0; j < views.size(); j++) {
                View view = views.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                left = currentLeft + marginLayoutParams.leftMargin;
                top = currentTop + marginLayoutParams.topMargin;
                right = left + view.getMeasuredWidth();
                bottom = top + view.getMeasuredHeight();
                view.layout(left, top, right, bottom);
                currentLeft += view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            currentLeft = 0;
            currentTop += heights.get(i);

        }
        allLineViews.clear();
        heights.clear();
    }
}

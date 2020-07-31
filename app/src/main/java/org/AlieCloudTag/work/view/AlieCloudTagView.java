package org.AlieCloudTag.work.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
    private static final String TAG = "AlieCloudTagView";
    private List<Integer> heights;
    private List<List<View>> allLineViews;
    private boolean flag = false;

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
        Log.i(TAG, "========onMeasure");
        int parentWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int parentHeightmode = MeasureSpec.getMode(heightMeasureSpec);

        int parentWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;

        /**
         * 修改8.0以上onMeasure重复调用问题
         */
        if (allLineViews != null && !allLineViews.isEmpty()) {
            allLineViews.clear();
        }
        if (heights != null && !heights.isEmpty()) {
            heights.clear();
        }
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
        Log.i(TAG, "========onLayout");
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

                /**
                 * ======================这里是居中控件居中方案,不需要时，删除即可===========================================
                 */
                top = calculateTop(top, view.getMeasuredHeight(), heights.get(i));
                bottom = calculateBottom(bottom, view.getMeasuredHeight(), heights.get(i));
                /**
                 * ======================这里是居中控件居中方案，不需要时，删除即可===========================================
                 */
                view.layout(left, top, right, bottom);
                currentLeft += view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            currentLeft = 0;
            currentTop += heights.get(i);

        }
        allLineViews.clear();
        heights.clear();
    }


    /**
     * 重新编辑单行控件的Top坐标(为实现每行控件 垂直居中)
     *
     * @param top       当前高度坐标
     * @param rawHeight 当前控件的高度
     * @param height    当前控件所在行的高度
     * @return
     */
    private int calculateTop(int top, int rawHeight, int height) {
        int targetTop = 0;
        // 除以2表示中心点的位移，因此，由于是做差，因此也可以不用除以2
        int offsite = rawHeight / 2 - height / 2;
        // 下面的判断为了便于理解，这里选择使用绝对值
        // 【注意】 offset 只能是小于等于0，不可能大于0，因为当前行控件最大的高度就是当前行高
        if (offsite == 0) {
            targetTop = top;
        } else if (offsite < 0) {
            targetTop = top + Math.abs(offsite);
        }
        return targetTop;
    }

    /**
     * 重新编辑单行控件的Bottom坐标(为实现每行控件 垂直居中)
     *
     * @param bottom
     * @param rawHeight
     * @param height
     * @return
     */
    private int calculateBottom(int bottom, int rawHeight, int height) {
        int targetBottom = 0;
        // 除以2表示中心点的位移，因此，由于是做差，因此也可以不用除以2
        int offsite = rawHeight / 2 - height / 2;
        // 下面的判断为了便于理解，这里选择使用绝对值
        if (offsite == 0) {
            targetBottom = bottom;
        } else if (offsite < 0) {
            targetBottom = bottom + Math.abs(offsite);
        }
        return targetBottom;

    }
}

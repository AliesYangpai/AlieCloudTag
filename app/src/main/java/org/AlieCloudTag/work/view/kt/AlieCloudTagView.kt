package org.AlieCloudTag.work.view.kt

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlin.math.max

/**
 * Created by Administrator on 2020/7/31 0031.
 * 类描述
 * 版本
 */
class AlieCloudTagView(context: Context, attrs: AttributeSet?, defStyle: Int) : ViewGroup(context, attrs, defStyle) {

    var mAllLineViews: ArrayList<List<View>>? = null
    var mHeights: ArrayList<Int>? = null

    init {
        mAllLineViews = ArrayList()
        mHeights = ArrayList()
    }

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    /**
     * 必须要重写此方法，否则不能获取当前控件子控件的margin
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var allViewMaxWidth = 0
        var allViewMaxHeight = 0

        if (mAllLineViews?.isNotEmpty()!!) {
            mAllLineViews?.clear()
        }
        if (mHeights?.isNotEmpty()!!) {
            mHeights?.clear()
        }
        var singleLineViews = ArrayList<View>()
        var currentAddWidth = 0
        var currentAddHeight = 0
        for (index in 0 until childCount) {
            val childView = getChildAt(index)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val childLayoutParams = childView.layoutParams as MarginLayoutParams
            val childWidth = childView.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            val childHeight = childView.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin

            if (currentAddWidth + childWidth > widthSize) {
                allViewMaxWidth = max(widthSize, currentAddWidth)
                allViewMaxHeight += currentAddHeight
                mAllLineViews?.add(singleLineViews)
                mHeights?.add(currentAddHeight)
                //--------------------------------------
                currentAddWidth = childWidth
                currentAddHeight = childHeight
                singleLineViews = ArrayList<View>()
                singleLineViews.add(childView)
            } else {
                currentAddWidth += childWidth
                currentAddHeight = max(currentAddHeight, childHeight)
                singleLineViews.add(childView)
            }
            if (index == childCount - 1) {
                allViewMaxWidth = max(allViewMaxWidth, currentAddWidth)
                allViewMaxHeight += currentAddHeight
                mHeights?.add(currentAddHeight)
                mAllLineViews?.add(singleLineViews)
            }
        }
        setMeasuredDimension(allViewMaxWidth, allViewMaxHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int
        var currentLeft = 0
        var currentTop = 0

        for (index in 0 until mAllLineViews?.size!!) {
            val list = mAllLineViews!![index]
            list.forEach {
                val marginLayoutParams = it.layoutParams as MarginLayoutParams
                left = currentLeft + marginLayoutParams.leftMargin
                top = currentTop + marginLayoutParams.topMargin
                right = left + it.measuredWidth
                bottom = top + it.measuredHeight

                /**
                 * ======================这里是居中控件居中方案,不需要时，删除即可===========================================
                 */
                /**
                 * ======================这里是居中控件居中方案,不需要时，删除即可===========================================
                 */
                top = calculateTop(top, it.measuredHeight, mHeights?.get(index)!!)
                bottom = calculateBottom(bottom, it.measuredHeight, mHeights?.get(index)!!)
                /**
                 * ======================这里是居中控件居中方案，不需要时，删除即可===========================================
                 */


                it.layout(left, top, right, bottom)
                currentLeft += it.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            }
            currentLeft = 0
            currentTop += mHeights!![index]
        }
        mAllLineViews?.clear()
        mHeights?.clear()
    }

    /**
     * 重新编辑单行控件的Top坐标(为实现每行控件 垂直居中)
     *
     * @param top       当前高度坐标
     * @param rawHeight 当前控件的高度
     * @param height    当前控件所在行的高度
     * @return
     */
    private fun calculateTop(top: Int, rawHeight: Int, height: Int): Int {
        var targetTop = 0
        // 除以2表示中心点的位移，因此，由于是做差，因此也可以不用除以2
        val offsite = rawHeight / 2 - height / 2
        // 下面的判断为了便于理解，这里选择使用绝对值
        // 【注意】 offset 只能是小于等于0，不可能大于0，因为当前行控件最大的高度就是当前行高
        if (offsite == 0) {
            targetTop = top
        } else if (offsite < 0) {
            targetTop = top + kotlin.math.abs(offsite)
        }
        return targetTop
    }

    /**
     * 重新编辑单行控件的Bottom坐标(为实现每行控件 垂直居中)
     *
     * @param bottom
     * @param rawHeight
     * @param height
     * @return
     */
    private fun calculateBottom(bottom: Int, rawHeight: Int, height: Int): Int {
        var targetBottom = 0
        // 除以2表示中心点的位移，因此，由于是做差，因此也可以不用除以2
        val offsite = rawHeight / 2 - height / 2
        // 下面的判断为了便于理解，这里选择使用绝对值
        if (offsite == 0) {
            targetBottom = bottom
        } else if (offsite < 0) {
            targetBottom = bottom + Math.abs(offsite)
        }
        return targetBottom
    }
}
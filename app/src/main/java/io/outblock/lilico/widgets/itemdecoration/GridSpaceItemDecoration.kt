package io.outblock.lilico.widgets.itemdecoration

import android.graphics.Rect
import android.view.View
import androidx.annotation.Dimension
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.outblock.lilico.utils.extensions.dp2px
import java.util.*
import kotlin.math.roundToInt

class GridSpaceItemDecoration(
    @Dimension(unit = Dimension.DP) private val start: Double = 0.0,
    @Dimension(unit = Dimension.DP) private val top: Double = 0.0,
    @Dimension(unit = Dimension.DP) private val end: Double = 0.0,
    @Dimension(unit = Dimension.DP) private val bottom: Double = 0.0,
    @Dimension(unit = Dimension.DP) private val horizontal: Double = 0.0,
    @Dimension(unit = Dimension.DP) private val vertical: Double = 0.0,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val gridLayoutManager = parent.layoutManager as? GridLayoutManager ?: return
        val itemCount = parent.adapter?.itemCount ?: return
        val spanCount = gridLayoutManager.spanCount
        val position = parent.getChildLayoutPosition(view)
        val spanSizeLookup = gridLayoutManager.spanSizeLookup

        val spanSize = spanSizeLookup.getSpanSize(position)
        val spanIndex = spanSizeLookup.getSpanIndex(position, spanCount)
        val spanGroupIndex =
            spanSizeLookup.getSpanGroupIndex(position, spanCount) // nMinusOne in vertical

        val isRtl =
            TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL

        val verticalSpanCount = spanSizeLookup.getSpanGroupIndex(itemCount - 1, spanCount) + 1

        val verticalSizeAvg =
            (top + bottom + vertical * (verticalSpanCount - 1)) / verticalSpanCount

        val horizontalSizeAvg =
            (start + end + horizontal * (spanCount - 1)) / spanCount

        val start = calculateStartOffsets(spanIndex, horizontalSizeAvg)
        val end =
            calculateEndOffset((spanCount - 1) - (spanIndex + spanSize - 1), horizontalSizeAvg)
        val top = calculateTopOffsets(spanGroupIndex, verticalSizeAvg)
        val bottom =
            calculateBottomOffsets((verticalSpanCount - 1) - (spanGroupIndex), verticalSizeAvg)

        val (left, right) = if (isRtl) {
            end to start
        } else {
            start to end
        }
        outRect.set(
            left.dp2px().toInt(),
            top.dp2px().toInt(),
            right.dp2px().toInt(),
            bottom.dp2px().toInt(),
        )
    }

    @Dimension(unit = Dimension.DP)
    private fun calculateBottomOffsets(
        nMinusOne: Int,
        @Dimension(unit = Dimension.DP) verticalSizeAvg: Double
    ): Int {
        return calculateOffset(bottom, nMinusOne, vertical - verticalSizeAvg)
    }

    @Dimension(unit = Dimension.DP)
    private fun calculateTopOffsets(
        nMinusOne: Int,
        @Dimension(unit = Dimension.DP) verticalSizeAvg: Double
    ): Int {
        return calculateOffset(top, nMinusOne, vertical - verticalSizeAvg)
    }


    @Dimension(unit = Dimension.DP)
    private fun calculateStartOffsets(
        nMinusOne: Int,
        @Dimension(unit = Dimension.DP) horizontalSizeAvg: Double
    ): Int {
        return calculateOffset(start, nMinusOne, horizontal - horizontalSizeAvg)
    }

    private fun calculateOffset(
        initialValue: Double,
        nMinusOne: Int,
        commonDifference: Double
    ): Int {
        return (initialValue + nMinusOne * commonDifference).roundToInt()
    }

    private fun calculateEndOffset(nMinusOne: Int, horizontalSizeAvg: Double): Int {
        return calculateOffset(
            end,
            (nMinusOne),
            horizontal - horizontalSizeAvg
        )
    }

}
package io.outblock.lilico.page.nft.nftlist.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class NftListHeaderBehavior extends AppBarLayout.Behavior {
    private static final String TAG = "NftListHeaderBehavior";

    public NftListHeaderBehavior() {
        super();
    }

    public NftListHeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // default true
    private boolean canScroll = true;

    public void setCanScroll(boolean can){
        this.canScroll = can;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, View target, int dx, int dy, int[] consumed, int type) {
        if (canScroll) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        }
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, int[] consumed) {
        if (canScroll) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout abl, View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, abl, target, type);
    }

    @Override
    public void setDragCallback(@Nullable BaseDragCallback callback) {
        super.setDragCallback(callback);
    }

    @Override
    public boolean onMeasureChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout abl, int layoutDirection) {
        return super.onLayoutChild(parent, abl, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, @NonNull View directTargetChild, View target, int nestedScrollAxes, int type) {
        boolean res=  super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes, type);
        return res;
    }

    @Override
    protected void layoutChild(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, int layoutDirection) {
        super.layoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean setTopAndBottomOffset(int offset) {
        return super.setTopAndBottomOffset(offset);
    }

    @Override
    public boolean setLeftAndRightOffset(int offset) {
        return super.setLeftAndRightOffset(offset);
    }

    @Override
    public int getTopAndBottomOffset() {
        return super.getTopAndBottomOffset();
    }

    @Override
    public int getLeftAndRightOffset() {
        return super.getLeftAndRightOffset();
    }

    @Override
    public void setVerticalOffsetEnabled(boolean verticalOffsetEnabled) {
        super.setVerticalOffsetEnabled(verticalOffsetEnabled);
    }

    @Override
    public boolean isVerticalOffsetEnabled() {
        boolean res =  super.isVerticalOffsetEnabled();
        return res;
    }

    @Override
    public void setHorizontalOffsetEnabled(boolean horizontalOffsetEnabled) {
        super.setHorizontalOffsetEnabled(horizontalOffsetEnabled);
    }

    @Override
    public boolean isHorizontalOffsetEnabled() {
        boolean res =  super.isHorizontalOffsetEnabled();
        return res;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull AppBarLayout child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }
}

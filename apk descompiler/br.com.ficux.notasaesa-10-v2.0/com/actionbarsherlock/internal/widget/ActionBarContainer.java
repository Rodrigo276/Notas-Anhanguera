package com.actionbarsherlock.internal.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import com.actionbarsherlock.C0029R;
import com.actionbarsherlock.internal.nineoldandroids.widget.NineFrameLayout;
import com.actionbarsherlock.view.Menu;

public class ActionBarContainer extends NineFrameLayout {
    private ActionBarView mActionBarView;
    private Drawable mBackground;
    private boolean mIsSplit;
    private boolean mIsStacked;
    private boolean mIsTransitioning;
    private Drawable mSplitBackground;
    private Drawable mStackedBackground;
    private View mTabContainer;

    public ActionBarContainer(Context context) {
        this(context, null);
    }

    public ActionBarContainer(Context context, AttributeSet attrs) {
        boolean z = true;
        super(context, attrs);
        setBackgroundDrawable(null);
        TypedArray a = context.obtainStyledAttributes(attrs, C0029R.styleable.SherlockActionBar);
        this.mBackground = a.getDrawable(2);
        this.mStackedBackground = a.getDrawable(12);
        if (getId() == C0029R.id.abs__split_action_bar) {
            this.mIsSplit = true;
            this.mSplitBackground = a.getDrawable(3);
        }
        a.recycle();
        if (this.mIsSplit) {
            if (this.mSplitBackground != null) {
                z = false;
            }
        } else if (!(this.mBackground == null && this.mStackedBackground == null)) {
            z = false;
        }
        setWillNotDraw(z);
    }

    public void onFinishInflate() {
        super.onFinishInflate();
        this.mActionBarView = (ActionBarView) findViewById(C0029R.id.abs__action_bar);
    }

    public void setPrimaryBackground(Drawable bg) {
        this.mBackground = bg;
        invalidate();
    }

    public void setStackedBackground(Drawable bg) {
        this.mStackedBackground = bg;
        invalidate();
    }

    public void setSplitBackground(Drawable bg) {
        this.mSplitBackground = bg;
        invalidate();
    }

    public void setTransitioning(boolean isTransitioning) {
        int i;
        this.mIsTransitioning = isTransitioning;
        if (isTransitioning) {
            i = 393216;
        } else {
            i = Menu.CATEGORY_ALTERNATIVE;
        }
        setDescendantFocusability(i);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return this.mIsTransitioning || super.onInterceptTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }

    public boolean onHoverEvent(MotionEvent ev) {
        super.onHoverEvent(ev);
        return true;
    }

    public void setTabContainer(ScrollingTabContainerView tabView) {
        if (this.mTabContainer != null) {
            removeView(this.mTabContainer);
        }
        this.mTabContainer = tabView;
        if (tabView != null) {
            addView(tabView);
            LayoutParams lp = tabView.getLayoutParams();
            lp.width = -1;
            lp.height = -2;
            tabView.setAllowCollapse(false);
        }
    }

    public View getTabContainer() {
        return this.mTabContainer;
    }

    public void onDraw(Canvas canvas) {
        if (getWidth() != 0 && getHeight() != 0) {
            if (!this.mIsSplit) {
                if (this.mBackground != null) {
                    this.mBackground.draw(canvas);
                }
                if (this.mStackedBackground != null && this.mIsStacked) {
                    this.mStackedBackground.draw(canvas);
                }
            } else if (this.mSplitBackground != null) {
                this.mSplitBackground.draw(canvas);
            }
        }
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mActionBarView != null) {
            int actionBarViewHeight;
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) this.mActionBarView.getLayoutParams();
            if (this.mActionBarView.isCollapsed()) {
                actionBarViewHeight = 0;
            } else {
                actionBarViewHeight = (this.mActionBarView.getMeasuredHeight() + lp.topMargin) + lp.bottomMargin;
            }
            if (this.mTabContainer != null && this.mTabContainer.getVisibility() != 8 && MeasureSpec.getMode(heightMeasureSpec) == Integer.MIN_VALUE) {
                setMeasuredDimension(getMeasuredWidth(), Math.min(this.mTabContainer.getMeasuredHeight() + actionBarViewHeight, MeasureSpec.getSize(heightMeasureSpec)));
            }
        }
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        boolean hasTabs = (this.mTabContainer == null || this.mTabContainer.getVisibility() == 8) ? false : true;
        if (!(this.mTabContainer == null || this.mTabContainer.getVisibility() == 8)) {
            int containerHeight = getMeasuredHeight();
            int tabHeight = this.mTabContainer.getMeasuredHeight();
            if ((this.mActionBarView.getDisplayOptions() & 2) == 0) {
                int count = getChildCount();
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (!(child == this.mTabContainer || this.mActionBarView.isCollapsed())) {
                        child.offsetTopAndBottom(tabHeight);
                    }
                }
                this.mTabContainer.layout(l, 0, r, tabHeight);
            } else {
                this.mTabContainer.layout(l, containerHeight - tabHeight, r, containerHeight);
            }
        }
        boolean needsInvalidate = false;
        if (!this.mIsSplit) {
            if (this.mBackground != null) {
                this.mBackground.setBounds(this.mActionBarView.getLeft(), this.mActionBarView.getTop(), this.mActionBarView.getRight(), this.mActionBarView.getBottom());
                needsInvalidate = true;
            }
            boolean z = hasTabs && this.mStackedBackground != null;
            this.mIsStacked = z;
            if (z) {
                this.mStackedBackground.setBounds(this.mTabContainer.getLeft(), this.mTabContainer.getTop(), this.mTabContainer.getRight(), this.mTabContainer.getBottom());
                needsInvalidate = true;
            }
        } else if (this.mSplitBackground != null) {
            this.mSplitBackground.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            needsInvalidate = true;
        }
        if (needsInvalidate) {
            invalidate();
        }
    }
}

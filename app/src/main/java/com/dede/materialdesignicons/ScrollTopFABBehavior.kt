package com.dede.materialdesignicons

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import java.lang.ref.WeakReference
import java.util.*

/**
 * 点击FAB滚动到顶部
 */
class ScrollTopFABBehavior(context: Context, attrs: AttributeSet?) :
    FloatingActionButton.Behavior(context, attrs), View.OnClickListener {

    private var parentRef: WeakReference<CoordinatorLayout>? = null
    private var fabRef: WeakReference<FloatingActionButton>? = null

    private var scrollableIds: IntArray

    private val viewCache = WeakHashMap<Int, View>()

    init {
        val attributes =
            context.obtainStyledAttributes(attrs, R.styleable.ScrollTopFABBehavior)
        val ids = attributes.getString(R.styleable.ScrollTopFABBehavior_scrollable_ids)
        attributes.recycle()

        val split = TextUtils.split(ids, ",")
        val list = ArrayList<Int>()
        for (s in split) {
            if (TextUtils.isEmpty(s)) continue
            val id = context.resources.getIdentifier(s, "id", context.packageName)
            list.add(id)
        }
        scrollableIds = list.toIntArray()
    }

    private inline fun <reified T : View> findViewByType(refind: Boolean = false): T? {
        val clazz = T::class.java
        if (refind) {
            for (entry in viewCache) {
                if (entry.value.javaClass.isAssignableFrom(clazz)) {
                    return entry.value as T
                }
            }
        }

        var view: T? = null
        val parent = parentRef?.get()
        if (parent != null) {
            for (id in scrollableIds) {
                val child = parent.findViewById<View>(id) ?: continue
                if (child.javaClass.isAssignableFrom(clazz)) {
                    view = child as T
                }
                viewCache[id] = child
            }
        }
        return view
    }

    override fun onClick(v: View) {
        fabHide(fabRef?.get() ?: v as FloatingActionButton)
        val appBarLayout = findViewByType<AppBarLayout>()
        appBarLayout?.setExpanded(true, true)
        val recyclerView = findViewByType<RecyclerView>(true)
        recyclerView?.smoothScrollToPosition(0)
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                val offset = recyclerView.computeVerticalScrollOffset()
                if (offset <= 0) {
                    fabHide(fabRef?.get() ?: return)
                }
            }
        }
    }

    private val onOffsetChangedListener = object : AppBarLayout.OnOffsetChangedListener {
        private var lastOffset: Int = 0
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (verticalOffset > lastOffset && verticalOffset == 0) {
                val fab = fabRef?.get()
                if (fab != null) {
                    fabShow(fab)
                }
            } else if (verticalOffset < lastOffset) {
                val fab = fabRef?.get()
                if (fab != null) {
                    fabHide(fab)
                }
            }
            lastOffset = verticalOffset
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: FloatingActionButton,
        layoutDirection: Int
    ): Boolean {
        parentRef = WeakReference(parent)
        fabRef = WeakReference(child)
        child.setOnClickListener(this)
        fabHide(child)
        val recyclerView = findViewByType<RecyclerView>()
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener)
            recyclerView.addOnScrollListener(onScrollListener)
        }
        val appBarLayout = findViewByType<AppBarLayout>()
        if (appBarLayout != null) {
            appBarLayout.removeOnOffsetChangedListener(onOffsetChangedListener)
            appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener)
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: FloatingActionButton,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyConsumed >= 0) {
            fabHide(child)
        } else {
            fabShow(child)
        }
    }

    private fun fabShow(child: FloatingActionButton) {
        if (child.isOrWillBeShown) {
            return
        }
        child.show()
    }

    private fun fabHide(child: FloatingActionButton) {
        if (child.isOrWillBeHidden) {
            return
        }
        child.hide(onHideListener)
    }

    private val onHideListener = object : OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton) {
            fab.visibility = View.INVISIBLE
        }
    }

}
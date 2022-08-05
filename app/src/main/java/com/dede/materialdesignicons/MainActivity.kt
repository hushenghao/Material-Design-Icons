package com.dede.materialdesignicons

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
import androidx.core.view.MarginLayoutParamsCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.internal.EdgeToEdgeUtils
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EdgeToEdgeUtils.applyEdgeToEdge(window, true)

        val toolBar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(this,
            drawerLayout,
            toolBar,
            androidx.appcompat.R.string.abc_action_menu_overflow_description,
            androidx.constraintlayout.widget.R.string.abc_action_bar_home_description)
        drawerToggle.syncState()

        val actionBarDependencyLayout = findViewById<View>(R.id.action_bar_dependency_layout)
        val shapeDrawable = MaterialShapeDrawable().apply {
            setCornerSize(60.dpf)
            setStroke(1.dpf, Color.GRAY)
            fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
        }
        actionBarDependencyLayout.background = shapeDrawable
        val savedMargin =
            ViewGroup.MarginLayoutParams(actionBarDependencyLayout.layoutParams as ViewGroup.MarginLayoutParams)
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        var lastVerticalOffset = -1
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            if (lastVerticalOffset == verticalOffset) return@addOnOffsetChangedListener
            val p = 1f - abs(verticalOffset).toFloat() / toolBar.height
            shapeDrawable.setCornerSize(p * 60.dpf)
            shapeDrawable.setStroke(3f,
                ColorUtils.setAlphaComponent(Color.GRAY, (p * 255).roundToInt()))
            actionBarDependencyLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                height = (48 + (1 - p) * 12).dp
                topMargin = (savedMargin.topMargin * p).roundToInt()
                bottomMargin = (savedMargin.bottomMargin * p).roundToInt()
                marginStart =
                    (MarginLayoutParamsCompat.getMarginStart(savedMargin) * p).roundToInt()
                marginEnd = (MarginLayoutParamsCompat.getMarginEnd(savedMargin) * p).roundToInt()
            }
            lastVerticalOffset = verticalOffset
        }
        appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, IconListFragment())
            .commit()
    }

    val Number.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        ).roundToInt()

    val Number.dpf: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            resources.displayMetrics
        )

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return drawerToggle.onOptionsItemSelected(item) && super.onOptionsItemSelected(item)
    }

}
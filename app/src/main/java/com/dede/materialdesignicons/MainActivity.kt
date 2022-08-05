package com.dede.materialdesignicons

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.ColorUtils
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
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            androidx.appcompat.R.string.abc_action_menu_overflow_description,
            androidx.constraintlayout.widget.R.string.abc_action_bar_home_description
        )
        drawerToggle.syncState()

        val actionBarDependencyLayout = findViewById<View>(R.id.action_bar_dependency_layout)
        val tvCategory = findViewById<TextView>(R.id.tv_category)
        val etSearch = findViewById<EditText>(R.id.et_search)
        val shapeDrawable = MaterialShapeDrawable().apply {
            fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
            setStroke(
                1.dpf,
                Color.GRAY
            )
        }
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val p = 1f - abs(verticalOffset).toFloat() / toolBar.height
            shapeDrawable.setCornerSize(p * 48.dpf)
            shapeDrawable.alpha = (p * 255).roundToInt()
            val inset = (12.dpf * p).roundToInt()

            actionBarDependencyLayout.background = object : InsetDrawable(
                shapeDrawable,
                inset,
                0,
                inset,
                inset,
            ) {
                override fun getPadding(padding: Rect): Boolean {
                    return false
                }

                override fun getMinimumHeight(): Int {
                    return -1
                }

                override fun getMinimumWidth(): Int {
                    return -1
                }
            }
            val topOffset = 12.dpf * (1 - p) / 2f
            tvCategory.y = topOffset
            etSearch.y = topOffset
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
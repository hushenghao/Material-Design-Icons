package com.dede.materialdesignicons

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.dede.materialdesignicons.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.color.MaterialColors
import com.google.android.material.internal.EdgeToEdgeUtils
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.abs
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("RestrictedApi")
        EdgeToEdgeUtils.applyEdgeToEdge(window, true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            binding.toolBar,
            androidx.appcompat.R.string.abc_action_menu_overflow_description,
            androidx.constraintlayout.widget.R.string.abc_action_bar_home_description
        )
        drawerToggle.syncState()

        val strokeColor = MaterialColors.getColor(
            this,
            com.google.android.material.R.attr.colorContainer,
            Color.GRAY
        )
        val shapeDrawable = MaterialShapeDrawable().apply {
            fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
            setStroke(1.dpf, strokeColor)
        }
        val appBarLayout = findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout.addOnOffsetChangedListener { _, verticalOffset ->
            val p = 1f - abs(verticalOffset).toFloat() / binding.toolBar.height
            shapeDrawable.setCornerSize(p * 48.dpf)
            shapeDrawable.alpha = (p * 255).roundToInt()
            val inset = (12.dpf * p).roundToInt()

            binding.actionBarDependencyLayout.background = object : InsetDrawable(
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
            binding.tvCategory.y = topOffset
            binding.etSearch.y = topOffset
        }
        appBarLayout.statusBarForeground =
            MaterialShapeDrawable.createWithElevationOverlay(this)

        binding.tvCategory.setOnClickListener { v ->
            PopupMenu(this, v, Gravity.BOTTOM).apply {
                inflate(R.menu.menu_drop_category_selector)
                setOnMenuItemClickListener {
                    val category = when (it.itemId) {
                        R.id.menu_material_icons -> {
                            CATEGORY_MATERIAL_ICONS
                        }
                        R.id.menu_material_symbols -> {
                            CATEGORY_MATERIAL_SYMBOLS
                        }
                        else -> {
                            return@setOnMenuItemClickListener false
                        }
                    }
                    val fragment =
                        supportFragmentManager.findFragmentById(R.id.fragment_container_view) as IconListFragment
                    fragment.setCategory(category)
                    binding.tvCategory.text = it.title
                    return@setOnMenuItemClickListener true
                }
            }.show()
        }

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
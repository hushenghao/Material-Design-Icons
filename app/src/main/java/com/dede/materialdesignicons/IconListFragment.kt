package com.dede.materialdesignicons

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by shhu on 2022/8/4 16:14.
 *
 * @author shhu
 * @since 2022/8/4
 */
class IconListFragment : Fragment(R.layout.fragment_icon_list) {

    data class IconConfig(
        val category: String,
        val type: String,
        val codepoints: String,
        val source: String,
    )

    data class Icon(
        val name: String,
        val keyName: String,
        val char: Char,
    )

    companion object {
        const val CATEGORY_MATERIAL_ICONS = "material_icons"
        const val CATEGORY_MATERIAL_SYMBOLS = "material_symbols"

        const val TYPE_OUTLINED = "Outlined"
        const val TYPE_ROUNDED = "Rounded"
        const val TYPE_SHARP = "Sharp"
        const val TYPE_TWO_TONE = "TwoTone"
        const val TYPE_FILLED = "Filled"
    }

//    private val icons = IconConfig(MATERIAL_SYMBOLS,
//        TYPE_OUTLINED,
//        "variablefont/MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].codepoints",
//        "variablefont/MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].ttf")

    private val icons = IconConfig(CATEGORY_MATERIAL_ICONS,
        TYPE_FILLED,
        "font/MaterialIconsOutlined-Regular.codepoints",
        "font/MaterialIconsOutlined-Regular.otf")

    private val splitRegex = "\\s".toRegex()
    private val connectorRegex = "(_[a-z])".toRegex()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tempList = ArrayList<MatchResult>()
        val icons = requireContext().assets.open(icons.codepoints)
            .reader()
            .readLines()
            .asSequence()
            .filter { it.isNotBlank() }
            .map {
                val list = it.split(splitRegex)
                val name = list[0]
                val sb = StringBuilder(name)
                sb.replace(0, 1, sb[0].uppercase())
                tempList.clear()
                val resultList = connectorRegex.findAll(name).toCollection(tempList)
                for (result in resultList) {
                    val group = result.groups[1] ?: continue
                    val uppercase = group.value.replace("_", " ").uppercase()
                    sb.replace(
                        group.range.first,
                        group.range.last + 1,
                        uppercase)
                }
                Icon(sb.toString(), name, list[1].toInt(16).toChar())
            }
            .toList()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = IconsAdapter(requireContext(), this.icons, icons)
    }

    private class IconsAdapter(context: Context, iconConfig: IconConfig, val list: List<Icon>) :
        RecyclerView.Adapter<IconsAdapter.VHolder>() {

        private val typeface = Typeface.createFromAsset(context.assets, iconConfig.source)
        private val textTypeface = Typeface.createFromAsset(context.assets, "googlesanstext.woff2")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon_layout, parent, false)
            return VHolder(itemView)
        }

        override fun onBindViewHolder(holder: VHolder, position: Int) {
            val icon = list[position]
            holder.tvIcon.apply {
                text = icon.char.toString()
                typeface = this@IconsAdapter.typeface
            }
            holder.tvName.apply {
                text = icon.name
                typeface = this@IconsAdapter.textTypeface
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        private class VHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvIcon: TextView = view.findViewById(R.id.tv_icon)
            val tvName: TextView = view.findViewById(R.id.tv_name)
        }
    }

}
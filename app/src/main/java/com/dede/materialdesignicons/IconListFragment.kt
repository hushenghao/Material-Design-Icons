package com.dede.materialdesignicons

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dede.materialdesignicons.databinding.FragmentIconListBinding

class IconListFragment : Fragment(R.layout.fragment_icon_list) {

    private lateinit var binding: FragmentIconListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentIconListBinding.bind(view)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        val category = arguments?.getString(EXTRA_CATEGORY) ?: CATEGORY_MATERIAL_SYMBOLS
        val type = arguments?.getString(EXTRA_TYPE) ?: TYPE_OUTLINED
        setDataInternal(category, type)
    }

    fun setCategory(category: String) {
        val type = arguments?.getString(EXTRA_TYPE) ?: TYPE_OUTLINED
        setDataInternal(category, type)
    }

    fun setType(type: String) {
        val category = arguments?.getString(EXTRA_CATEGORY) ?: CATEGORY_MATERIAL_SYMBOLS
        setDataInternal(category, type)
    }

    private fun setDataInternal(category: String, type: String) {
        arguments?.putString(EXTRA_CATEGORY, category)
        arguments?.putString(EXTRA_TYPE, type)
        val iconSource: IconSource = if (category == CATEGORY_MATERIAL_ICONS) {
            materialIcons[type]!!
        } else {
            materialSymbols[type]!!
        }
        val assets = requireContext().assets
        val typeface =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && category == CATEGORY_MATERIAL_SYMBOLS) {
                Typeface.Builder(assets, iconSource.source)
                    .setFontVariationSettings("'FILL' 0, 'GRAD' 0, 'opsz' 48, 'wght' 400")
                    .build()
            } else {
                Typeface.createFromAsset(assets, iconSource.source)
            }

        binding.recyclerView.adapter =
            IconsAdapter(typeface, iconSource.getSource(requireContext()))
    }

    private class IconsAdapter(val iconTypeface: Typeface, val list: List<Icon>) :
        RecyclerView.Adapter<IconsAdapter.VHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon_layout, parent, false)
            return VHolder(itemView)
        }

        override fun onBindViewHolder(holder: VHolder, position: Int) {
            val icon = list[position]
            holder.tvIcon.apply {
                text = icon.char.toString()
                typeface = iconTypeface
            }
            holder.tvName.apply {
                text = icon.name
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
package com.dede.materialdesignicons

import android.content.Context

const val EXTRA_CATEGORY = "category"
const val EXTRA_TYPE = "type"

const val CATEGORY_MATERIAL_ICONS = "Material Icons"
const val CATEGORY_MATERIAL_SYMBOLS = "Material Symbols"

const val TYPE_FILLED = "Filled"
const val TYPE_OUTLINED = "Outlined"
const val TYPE_ROUNDED = "Rounded"
const val TYPE_SHARP = "Sharp"
const val TYPE_TWO_TONE = "TwoTone"

private val splitRegex = "\\s".toRegex()
private val connectorRegex = "(_[a-z])".toRegex()

data class IconSource(
    val category: String,
    val type: String,
    val codepoints: String,
    val source: String,
) {

    fun getSource(context: Context): List<Icon> {
        val tempList = ArrayList<MatchResult>()
        val icons = context.assets.open(this.codepoints)
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
                        uppercase
                    )
                }
                Icon(sb.toString(), name, list[1].toInt(16).toChar())
            }
            .toList()
        return icons
    }
}

data class Icon(
    val name: String,
    val keyName: String,
    val char: Char,
)

val materialSymbols = linkedMapOf(
    TYPE_ROUNDED to IconSource(
        CATEGORY_MATERIAL_SYMBOLS,
        TYPE_ROUNDED,
        "variablefont/MaterialSymbolsRounded[FILL,GRAD,opsz,wght].codepoints",
        "variablefont/MaterialSymbolsRounded[FILL,GRAD,opsz,wght].ttf"
    ),
    TYPE_OUTLINED to IconSource(
        CATEGORY_MATERIAL_SYMBOLS,
        TYPE_OUTLINED,
        "variablefont/MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].codepoints",
        "variablefont/MaterialSymbolsOutlined[FILL,GRAD,opsz,wght].ttf"
    ),
    TYPE_SHARP to IconSource(
        CATEGORY_MATERIAL_SYMBOLS,
        TYPE_SHARP,
        "variablefont/MaterialSymbolsSharp[FILL,GRAD,opsz,wght].codepoints",
        "variablefont/MaterialSymbolsSharp[FILL,GRAD,opsz,wght].ttf"
    ),
)

val materialSymbolsTypes = materialSymbols.keys

val materialIcons = linkedMapOf(
    TYPE_FILLED to IconSource(
        CATEGORY_MATERIAL_ICONS,
        TYPE_FILLED,
        "font/MaterialIcons-Regular.codepoints",
        "font/MaterialIcons-Regular.ttf"
    ),
    TYPE_OUTLINED to IconSource(
        CATEGORY_MATERIAL_ICONS,
        TYPE_OUTLINED,
        "font/MaterialIconsOutlined-Regular.codepoints",
        "font/MaterialIconsOutlined-Regular.otf"
    ),
    TYPE_ROUNDED to IconSource(
        CATEGORY_MATERIAL_ICONS,
        TYPE_ROUNDED,
        "font/MaterialIconsRound-Regular.codepoints",
        "font/MaterialIconsRound-Regular.otf"
    ),
    TYPE_SHARP to IconSource(
        CATEGORY_MATERIAL_ICONS,
        TYPE_SHARP,
        "font/MaterialIconsSharp-Regular.codepoints",
        "font/MaterialIconsSharp-Regular.otf"
    ),
    TYPE_TWO_TONE to IconSource(
        CATEGORY_MATERIAL_ICONS,
        TYPE_TWO_TONE,
        "font/MaterialIconsTwoTone-Regular.codepoints",
        "font/MaterialIconsTwoTone-Regular.otf"
    ),
)

val materialIconsTypes = materialIcons.keys
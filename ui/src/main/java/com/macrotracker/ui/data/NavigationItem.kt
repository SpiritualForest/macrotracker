package com.macrotracker.ui.data

import androidx.annotation.DrawableRes

data class NavigationItem(
    val title: String,
    @DrawableRes val iconResource: Int,
    val contentDescription: String = "",
    val onClick: () -> Unit = {},
)

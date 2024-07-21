package com.github.aakumykov.kotlin_playground

import androidx.annotation.DrawableRes

data class StorageDirectory(
    val name: String,
    val path: String,
    @DrawableRes val icon: Int
)

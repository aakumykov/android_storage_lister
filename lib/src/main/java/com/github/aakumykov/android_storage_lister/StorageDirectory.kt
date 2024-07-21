package com.github.aakumykov.kotlin_playground

import androidx.annotation.DrawableRes

data class StorageDirectory(
    val path: String,
    val name: String,
    @DrawableRes val icon: Int
)

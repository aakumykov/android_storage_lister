package com.github.aakumykov.android_storage_lister

import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageType

data class StorageDirectory(
    val type: AndroidStorageType,
    val path: String,
    val name: String,
    @DrawableRes val icon: Int
)

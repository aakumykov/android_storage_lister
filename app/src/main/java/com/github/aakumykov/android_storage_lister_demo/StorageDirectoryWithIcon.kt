package com.github.aakumykov.android_storage_lister_demo

import androidx.annotation.DrawableRes
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageType


data class StorageDirectoryWithIcon(
    override val type: AndroidStorageType,
    override val path: String,
    override val name: String,
    @DrawableRes val icon: Int,
): AndroidStorageDirectory

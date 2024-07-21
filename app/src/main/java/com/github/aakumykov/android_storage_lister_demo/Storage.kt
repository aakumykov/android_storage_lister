package com.github.aakumykov.android_storage_lister_demo

import com.github.aakumykov.android_storage_lister.AndroidStorageType

data class Storage(
    val name: String,
    val path: String,
    val type: AndroidStorageType
)

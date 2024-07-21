package com.github.aakumykov.android_storage_lister

data class StorageDirectory(
    val type: AndroidStorageType,
    val path: String,
    val name: String,
)

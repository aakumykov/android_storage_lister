package com.github.aakumykov.android_storage_lister

data class StorageDirectory(
    override val type: AndroidStorageType,
    override val path: String,
    override val name: String,
): AndroidStorageDirectory

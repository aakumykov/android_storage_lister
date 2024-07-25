package com.github.aakumykov.android_storage_lister

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StorageDirectory(
    override val type: AndroidStorageType,
    override val path: String,
    override val name: String,
): AndroidStorageDirectory, Parcelable

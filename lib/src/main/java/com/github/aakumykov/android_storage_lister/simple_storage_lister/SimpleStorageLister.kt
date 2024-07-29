package com.github.aakumykov.android_storage_lister.simple_storage_lister

import android.content.Context
import com.github.aakumykov.android_storage_lister.AndroidStorageDirectory
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.AndroidStorageType
import com.github.aakumykov.android_storage_lister.R

class SimpleStorageLister(applicationContext: Context) : AndroidStorageLister(applicationContext) {

    override fun createStorageDirectory(
        type: AndroidStorageType,
        name: String,
        path: String
    ): AndroidStorageDirectory {
        return StorageDirectoryWithIcon(
            name = name,
            path = path,
            type = type,
            icon = iconForType(type)
        )
    }

    private fun iconForType(type: AndroidStorageType): Int {
        return when(type) {
            AndroidStorageType.INTERNAL -> R.drawable.ic_storage_type_internal
            AndroidStorageType.USB -> R.drawable.ic_storage_type_usb
            AndroidStorageType.SD_CARD -> R.drawable.ic_storage_type_sd_card
        }
    }
}
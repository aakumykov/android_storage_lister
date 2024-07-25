package com.github.aakumykov.android_storage_lister

interface AndroidStorageDirectory {
    val type: AndroidStorageType
    val path:String
    val name: String
}
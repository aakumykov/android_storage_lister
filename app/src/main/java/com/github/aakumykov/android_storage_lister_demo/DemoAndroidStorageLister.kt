package com.github.aakumykov.android_storage_lister_demo

import android.content.Context
import com.github.aakumykov.android_storage_lister.AndroidStorageLister
import com.github.aakumykov.android_storage_lister.StorageDirectory

class DemoAndroidStorageLister(context: Context) : AndroidStorageLister<Storage>(context) {
    
    override fun createStorageRepresentationObject(storageDirectory: StorageDirectory?): Storage? {
        
        if (null == storageDirectory)
            return null
        
        return Storage(
            name = storageDirectory.name,
            path = storageDirectory.path,
            type = storageDirectory.type
        )
    }
}
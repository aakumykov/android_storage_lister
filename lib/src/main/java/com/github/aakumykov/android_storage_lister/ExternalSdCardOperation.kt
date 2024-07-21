package com.github.aakumykov.android_storage_lister

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

object ExternalSdCardOperation {

    val TAG: String = ExternalSdCardOperation::class.java.simpleName

    @JvmStatic
    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun getExtSdCardPathsForActivity(context: Context): Array<String> {

        val paths: MutableList<String> = ArrayList()

        for (file in context.getExternalFilesDirs("external")) {
            if (file != null) {
                val index = file.absolutePath.lastIndexOf("/Android/data")
                if (index < 0) {
                    Log.w(TAG, "Unexpected external file dir: " + file.absolutePath)
                } else {
                    var path = file.absolutePath.substring(0, index)
                    try {
                        path = File(path).canonicalPath
                    } catch (e: IOException) {
                        // Keep non-canonical path.
                    }
                    paths.add(path)
                }
            }
        }

        if (paths.isEmpty()) paths.add("/storage/sdcard1")
            return paths.toTypedArray()
    }
}
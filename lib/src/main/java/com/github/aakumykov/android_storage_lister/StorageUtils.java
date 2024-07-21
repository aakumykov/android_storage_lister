package com.github.aakumykov.android_storage_lister;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.storage.StorageVolume;

import java.io.File;
import java.lang.reflect.Field;

public class StorageUtils {

    @TargetApi(Build.VERSION_CODES.N)
    public static File getVolumeDirectory(StorageVolume volume) {
        try {
            Field f = StorageVolume.class.getDeclaredField("mPath");
            f.setAccessible(true);
            return (File) f.get(volume);
        } catch (Exception e) {
            // This shouldn't fail, as mPath has been there in every version
            throw new RuntimeException(e);
        }
    }
}

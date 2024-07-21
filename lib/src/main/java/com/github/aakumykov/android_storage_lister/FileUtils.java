package com.github.aakumykov.android_storage_lister;

import java.io.File;

public class FileUtils {
    public static boolean canListFiles(File f) {
        return f.canRead() && f.isDirectory();
    }
}

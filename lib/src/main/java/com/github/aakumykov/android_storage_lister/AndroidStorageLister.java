package com.github.aakumykov.android_storage_lister;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.os.Build.VERSION_CODES.KITKAT;
import static android.os.Build.VERSION_CODES.M;
import static android.os.Build.VERSION_CODES.N;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.github.aakumykov.android_storage_lister.utils.FileUtils;
import com.github.aakumykov.android_storage_lister.utils.StorageUtils;
import com.github.aakumykov.android_storage_lister.utils.OTGUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class detecting all available storage paths in Android.
 * Inspired from Amaze File Manager's code.
 * @link <a href="https://github.com/TeamAmaze/AmazeFileManager">AmazeFileManager</a>
 */
public abstract class AndroidStorageLister<T> {

    private final Context context;

    public AndroidStorageLister(Context context) {
        this.context = context;
    }

    private static final String INTERNAL_SHARED_STORAGE = "Internal shared storage";
    private static final String DEFAULT_FALLBACK_STORAGE_PATH = "/storage/sdcard0";
    private static final Pattern DIR_SEPARATOR = Pattern.compile("/");

    @Nullable
    public abstract T createStorageRepresentationObject(StorageDirectory storageDirectory);

    /**
     * @return paths to all available volumes in the system (include emulated)
     */
    public synchronized ArrayList<StorageDirectory> getStorageDirectories() {

        ArrayList<StorageDirectory> volumes;

        if (SDK_INT >= N) {
            volumes = getStorageDirectoriesNew();
        } else {
            volumes = getStorageDirectoriesLegacy();
        }

        /*if (isRootExplorer()) {
            volumes.add(
                    new StorageDirectory(
                            "/",
                            getResources().getString(R.string.root_directory),
                            R.drawable.ic_drawer_root_white));
        }*/

        return volumes;
    }

    /**
     * @return All available storage volumes (including internal storage, SD-Cards and USB devices)
     */
    @TargetApi(N)
    public synchronized ArrayList<StorageDirectory> getStorageDirectoriesNew() {
        // Final set of paths
        ArrayList<StorageDirectory> volumes = new ArrayList<>();
        StorageManager sm = context.getSystemService(StorageManager.class);
        for (StorageVolume volume : sm.getStorageVolumes()) {
            if (!volume.getState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                    && !volume.getState().equalsIgnoreCase(Environment.MEDIA_MOUNTED_READ_ONLY)) {
                continue;
            }
            File path = StorageUtils.getVolumeDirectory(volume);
            String name = volume.getDescription(context);
            if (INTERNAL_SHARED_STORAGE.equalsIgnoreCase(name)) {
                name = context.getString(R.string.storage_internal);
            }
            int icon;
            if (!volume.isRemovable()) {
                icon = R.drawable.ic_phone_android_white_24dp;
            } else {
                // HACK: There is no reliable way to distinguish USB and SD external storage
                // However it is often enough to check for "USB" String
                if (name.toUpperCase().contains("USB") || path.getPath().toUpperCase().contains("USB")) {
                    icon = R.drawable.ic_usb_white_24dp;
                } else {
                    icon = R.drawable.ic_sd_storage_white_24dp;
                }
            }

            volumes.add(new StorageDirectory(AndroidStorageType.USB, path.getPath(), name, icon));
        }
        return volumes;
    }

    /**
     * Returns all available SD-Cards in the system (include emulated)
     *
     * <p>Warning: Hack! Based on Android source code of version 4.3 (API 18) Because there was no
     * standard way to get it before android N
     *
     * @return All available SD-Cards in the system (include emulated)
     */
    public synchronized ArrayList<StorageDirectory> getStorageDirectoriesLegacy() {
        List<String> rv = new ArrayList<>();

        // Primary physical SD-CARD (not emulated)
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System.getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System.getenv("EMULATED_STORAGE_TARGET");

        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                // Check for actual existence of the directory before adding to list
                if (new File(DEFAULT_FALLBACK_STORAGE_PATH).exists()) {
                    rv.add(DEFAULT_FALLBACK_STORAGE_PATH);
                } else {
                    // We know nothing else, use Environment's fallback
                    rv.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                }
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (SDK_INT < JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                final String[] folders = DIR_SEPARATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr.split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        if (SDK_INT >= M && checkStoragePermission(context)) rv.clear();
        if (SDK_INT >= KITKAT) {
            String strings[] = ExternalSdCardOperation.getExtSdCardPathsForActivity(context);
            for (String s : strings) {
                File f = new File(s);
                if (!rv.contains(s) && FileUtils.canListFiles(f)) rv.add(s);
            }
        }
        File usb = getUsbDrive();
        if (usb != null && !rv.contains(usb.getPath())) rv.add(usb.getPath());

        if (SDK_INT >= KITKAT) {
            if (SingletonUsbOtg.getInstance().isDeviceConnected()) {
                rv.add(OTGUtil.PREFIX_OTG + "/");
            }
        }

        // Assign a label and icon to each directory
        ArrayList<StorageDirectory> volumes = new ArrayList<>();
        for (String file : rv) {
            File f = new File(file);
            @DrawableRes int icon;
            AndroidStorageType type;

            if ("/storage/emulated/legacy".equals(file)
                    || "/storage/emulated/0".equals(file)
                    || "/mnt/sdcard".equals(file)) {
                icon = R.drawable.ic_phone_android_white_24dp;
                type = AndroidStorageType.INTERNAL;
            } else if ("/storage/sdcard1".equals(file)) {
                icon = R.drawable.ic_sd_storage_white_24dp;
                type = AndroidStorageType.SD_CARD;
            } else if ("/".equals(file)) {
                icon = R.drawable.ic_drawer_root_white;
                type = AndroidStorageType.INTERNAL;
            } else {
                icon = R.drawable.ic_sd_storage_white_24dp;
                type = AndroidStorageType.SD_CARD;
            }

            @StorageNaming.DeviceDescription
            int deviceDescription = StorageNaming.getDeviceDescriptionLegacy(f);
            String name = StorageNamingHelper.getNameForDeviceDescription(context, f, deviceDescription);

            volumes.add(new StorageDirectory(type, file, name, icon));
        }

        return volumes;
    }


    private boolean checkStoragePermission(Context context) {
        // Verify that all required contact permissions have been granted.
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return (ActivityCompat.checkSelfPermission(context, Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED)
                    || (ActivityCompat.checkSelfPermission(context, Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    == PackageManager.PERMISSION_GRANTED)
                    || Environment.isExternalStorageManager();
        } else {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }


    @Nullable
    public File getUsbDrive() {

        File parent = new File("/storage");

        try {
            for (File f : parent.listFiles())
                if (f.exists() && f.getName().toLowerCase().contains("usb") && f.canExecute()) return f;
        } catch (Exception e) {}

        parent = new File("/mnt/sdcard/usbStorage");
        if (parent.exists() && parent.canExecute()) return parent;
        parent = new File("/mnt/sdcard/usb_storage");
        if (parent.exists() && parent.canExecute()) return parent;

        return null;
    }
}


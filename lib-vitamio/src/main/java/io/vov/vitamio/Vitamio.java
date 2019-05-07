package io.vov.vitamio;

import android.content.Context;
import io.vov.vitamio.utils.ContextUtils;

public class Vitamio {

    private static String vitamioPackage;
    private static String vitamioLibraryPath;
    private static String vitamioDataPath;
    private static String browserlibraryPath;

    public static boolean init(Context ctx) {
        vitamioPackage = ctx.getPackageName();
        vitamioLibraryPath = ctx.getApplicationInfo().nativeLibraryDir + "/";
        vitamioDataPath = ContextUtils.getDataDir(ctx) + "lib/";
        browserlibraryPath = ctx.getApplicationContext().getDir("libs", Context.MODE_PRIVATE).getPath();
        return true;
    }

    public static String getVitamioPackage() {
        return vitamioPackage;
    }


    public static final String getLibraryPath() {
        return vitamioLibraryPath;
    }

    public static final String getDataPath() {
        return vitamioDataPath;
    }

    public static final String getBrowserLibraryPath() {
        return browserlibraryPath;
    }

}

package com.easing.commons.android.manager;

import android.content.Context;
import lombok.SneakyThrows;

import java.io.InputStream;

public class ResourceUtil {

    @SneakyThrows
    public static String readAssetText(Context ctx, String path, String encode) {
        if (encode == null || encode.equals(""))
            encode = "UTF-8";
        InputStream in = ctx.getAssets().open(path);
        byte[] buffer = new byte[in.available()];
        in.read(buffer);
        in.close();
        return new String(buffer, encode);
    }
}

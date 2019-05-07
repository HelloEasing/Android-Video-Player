package com.easing.commons.android.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.easing.commons.android.value.measure.Size;

import java.io.InputStream;

public class BitmapUtil {
  public static Bitmap decodeBitmapFromResource(Context context, int drawableId) {
    return BitmapFactory.decodeResource(context.getResources(), drawableId);
  }

  public static Bitmap decodeBitmapFromFile(String path) {
    return BitmapFactory.decodeFile(path);
  }

  public static Bitmap decodeBitmapFromStream(InputStream is) {
    return BitmapFactory.decodeStream(is);
  }

  public static void loadImageToView(String path, ImageView iv) {
    Bitmap bitmap = BitmapUtil.decodeBitmapFromFile(path);
    iv.setImageBitmap(bitmap);
  }

  public static int calcSize(Bitmap bitmap) {
    return bitmap.getByteCount();
  }

  public static Size tryBitmapSize(Context context, int drawableId) {
    BitmapFactory.Options opt = new BitmapFactory.Options();
    opt.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(context.getResources(), drawableId, opt);
    return new Size(opt.outWidth, opt.outHeight);
  }
}

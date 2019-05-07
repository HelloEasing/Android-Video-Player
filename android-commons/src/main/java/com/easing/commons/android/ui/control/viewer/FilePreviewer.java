package com.easing.commons.android.ui.control.viewer;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easing.commons.android.R;
import com.easing.commons.android.glide.GlideApp;
import com.easing.commons.android.manager.*;
import com.easing.commons.android.media.MediaType;
import com.easing.commons.android.view.ViewManager;

import lombok.Getter;

public class FilePreviewer extends LinearLayout {

    private Context context;
    private Handler handler = new Handler();

    @Getter
    private String path;
    @Getter
    private String name;

    private ImageView iv;
    private TextView lab;

    public FilePreviewer(Context context, String path, String name) {
        super(context);
        init(context, null, path, name);
    }

    public FilePreviewer(Context context) {
        super(context);
        init(context, null, null, null);
    }

    public FilePreviewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null, null);
    }

    public FilePreviewer(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
        init(context, attrs, null, null);
    }

    private void init(Context context, AttributeSet attrSet, String path, String name) {
        this.context = context;
        View root = ViewManager.inflate(context, R.layout.layout_file_previewer);
        ViewManager.size(root, ViewManager.MATCH_PARENT, ViewManager.MATCH_PARENT, 0);
        iv = ViewManager.findView(root, R.id.iv);
        lab = ViewManager.findView(root, R.id.lab);
        super.addView(root);

        //默认显示内容
        initContent();
    }

    //延迟加载内容
    public void loadLater(String path, String name, int ms) {
        handler.postDelayed(() -> load(path, name), ms);
    }

    //加载内容
    public void load(String path, String name) {
        this.path = path;
        this.name = name;
        lab.setText(name);

        //缩放模式与边距
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv.setPadding(0, 0, 0, 0);

        //网络图片
        if (MediaType.isWebResource(path))
            GlideApp.with(context).asBitmap().load(path).into(iv);

            //文件不存在
        else if (!FileUtil.exist(path))
            lab.setText("loading...");

            //图片文件
        else if (MediaType.isImage(name))
            GlideApp.with(context).asBitmap().load(path).into(iv);

            //音频文件
        else if (MediaType.isAudio(name))
            GlideApp.with(context).asBitmap().load(R.drawable.image_file_type_m01_audio).into(iv);

            //视频文件
        else if (MediaType.isVideo(name))
            GlideApp.with(context).asBitmap().load(R.drawable.image_file_type_m01_video).into(iv);

            //文本文件
        else if (MediaType.isText(name))
            GlideApp.with(context).asBitmap().load(R.drawable.image_file_type_m01_text).into(iv);

            //文档文件
        else if (MediaType.isDocument(name))
            GlideApp.with(context).asBitmap().load(R.drawable.image_file_type_m01_doc).into(iv);

            //未知资源
        else {
            lab.setText("unkown type");
            GlideApp.with(context).asBitmap().load(R.drawable.image_file_type_m01_unknown).into(iv);
        }
    }

    //默认显示内容
    public void initContent() {
        GlideApp.with(context).asGif().load(R.drawable.image_file_type_m01_loading).into(iv);
        lab.setText("loading");
    }

}

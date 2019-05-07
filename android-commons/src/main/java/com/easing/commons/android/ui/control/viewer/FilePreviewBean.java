package com.easing.commons.android.ui.control.viewer;

import com.easing.commons.android.helper.data.JsonSerial;
import com.easing.commons.android.manager.FileUtil;

import lombok.Data;

@Data
public class FilePreviewBean implements JsonSerial {

    private String path;
    private String name;

    public static FilePreviewBean create(String path, String name) {
        FilePreviewBean bean = new FilePreviewBean();
        bean.setPath(path);
        bean.setName(name);
        return bean;
    }

    public boolean isFile() {
        return FileUtil.isFile(path);
    }
}

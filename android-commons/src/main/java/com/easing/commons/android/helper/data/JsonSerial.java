package com.easing.commons.android.helper.data;

import com.easing.commons.android.manager.JsonUtil;

import java.io.Serializable;

public interface JsonSerial extends Serializable {

    long SerializableID = 0L;

    default String toJson() {
        return JsonUtil.toJson(this);
    }
}

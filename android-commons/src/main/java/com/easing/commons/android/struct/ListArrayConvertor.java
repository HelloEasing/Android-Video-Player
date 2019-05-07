package com.easing.commons.android.struct;

import java.util.List;

public interface ListArrayConvertor<R> {

    R[] buildArray(int length);

    default R[] toArray(List<R> list) {
        R[] array = buildArray(list.size());
        for (int i = 0; i < list.size(); i++)
            array[i] = list.get(i);
        return array;
    }
}

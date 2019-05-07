package com.easing.commons.android.helper.message;

public class BusEvent {

    public int id;
    public Object data;

    public static BusEvent create(int id, Object data) {
        BusEvent event = new BusEvent();
        event.id = id;
        event.data = data;
        return event;
    }

    public <T> T getData() {
        return (T) data;
    }
}

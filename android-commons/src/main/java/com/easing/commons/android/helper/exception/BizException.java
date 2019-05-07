package com.easing.commons.android.helper.exception;

import com.easing.commons.android.code.Console;

public class BizException extends RuntimeException {

    public static BizException build(String message) {
        return new BizException(message);
    }

    private BizException(String message) {
        super(message);
    }

    public void print() {
        Console.error(getMessage());
    }
}

package com.easing.commons.android.value.identity;

import com.easing.commons.android.format.MathUtil;

import java.util.ArrayList;
import java.util.List;

public class Codes {

    private static final List<Integer> codes = new ArrayList();

    public static final int CODE_PICK_FILE = randomCode();
    public static final int CODE_PICK_IMAGE = randomCode();
    public static final int CODE_IMAGE_CAPTURE = randomCode();
    public static final int CODE_VIDEO_CAPTURE = randomCode();
    public static final int CODE_AUDIO_CAPTURE = randomCode();

    public static int randomCode() {
        int code = MathUtil.randomInt();
        while (codes.contains(code))
            code = MathUtil.randomInt();
        return code;
    }
}

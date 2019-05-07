package com.easing.commons.android.ui.app;

import com.easing.commons.android.code.Console;
import com.easing.commons.android.thread.Threads;
import lombok.SneakyThrows;

import java.lang.Thread.UncaughtExceptionHandler;

public class CommonExceptionHandler implements UncaughtExceptionHandler {

    private static final boolean SHOW_EXCEPTION = false;

    @Override
    @SneakyThrows
    public void uncaughtException(Thread t, Throwable e) {
        //开辟其它线程，显示报错信息
        Threads.post(() -> {
            Console.error(e);
            CommonApplication.ctx.handleGlobalException(e);
        });
        CommonApplication.ctx.finishProcess();
//        //阻塞，显示消息框5秒后结束进程
//        if (SHOW_EXCEPTION) {
//            t.wait(5000);
//            CommonApplication.ctx.finishProcess();
//        }
    }
}

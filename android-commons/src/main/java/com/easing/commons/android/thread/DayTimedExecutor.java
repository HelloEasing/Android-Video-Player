package com.easing.commons.android.thread;

import com.easing.commons.android.format.TimeUtil;
import com.easing.commons.android.helper.thread.AliveState;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.SneakyThrows;

//每天按时执行任务
public class DayTimedExecutor {

    private final ExecutorService pool = Executors.newCachedThreadPool();

    private AliveState aliveState = AliveState.create();

    private final Vector<EverydayTask> everydayTasks = new Vector();

    private DayTimedExecutor() {
    }

    public static DayTimedExecutor create() {
        return new DayTimedExecutor();
    }

    @SneakyThrows
    public void startEverydayTask() {
        if (!aliveState.isAlive())
            return;
        Threads.postByInterval(() -> {
            long millis = TimeUtil.millisOfDay();
            for (EverydayTask task : everydayTasks) {
                if (!task.executed && millis > task.t) {
                    pool.submit(task.r);
                    task.executed = true;
                }
            }
            //新的一天，标记所有任务未执行，重新开始轮询所有任务
            if (millis < 1000) {
                for (EverydayTask task : everydayTasks)
                    task.executed = false;
            }
        }, 500, aliveState);
    }

    //提交任务，在每天的t时刻执行r任务
    public void submitEverydayTask(EverydayTask task, long t) {
        task.t = t;
        task.executed = false;
        everydayTasks.add(task);
    }

    public void interrupt() {
        aliveState.kill();
        pool.shutdownNow();
    }

    public static class EverydayTask {

        public Runnable r;
        public long t;
        private boolean executed;

        public static EverydayTask wrap(Runnable r) {
            EverydayTask task = new EverydayTask();
            task.r = r;
            task.executed = false;
            return task;
        }
    }

}

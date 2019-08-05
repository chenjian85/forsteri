package io.forsteri.common.util;

import io.forsteri.common.factory.ForsteriThreadFactory;
import io.forsteri.common.threadpool.ForsteriThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingQueue;

public class ExecutorUtil {
    public static final ForsteriThreadPoolExecutor create(String name, int size) {
        return create(name, size, false);
    }

    public static final ForsteriThreadPoolExecutor create(String name, int size, boolean isDaemon) {
        ForsteriThreadFactory factory = new ForsteriThreadFactory(name, isDaemon);
        return new ForsteriThreadPoolExecutor(name, size, new LinkedBlockingQueue<Runnable>(), factory);
    }
}

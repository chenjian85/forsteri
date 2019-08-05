package io.forsteri.common.threadpool;

import io.forsteri.common.factory.ForsteriThreadFactory;
import io.forsteri.common.util.ExecutorUtil;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ForsteriExecutorGroup {
    private ArrayList<ForsteriThreadPoolExecutor> forsteriExecutorGroup;

    private String name;

    private int size;

    private int threadNum;

    private AtomicInteger pos = new AtomicInteger(0);

    /**
     *
     * @param name       executor group name
     * @param size       number of executor
     * @param threadNum  number of threads each executor
     * @param isDaemon   mark thread is daemon thread
     */
    public ForsteriExecutorGroup(String name, int size, int threadNum, boolean isDaemon) {
        this.name = name;
        this.size = size;
        this.threadNum = threadNum;
        forsteriExecutorGroup = new ArrayList<>(this.size);
        for(int i = 0; i < this.size; i++) {
            String groupName = String.format("%s-group-%d", this.name, i);
            this.forsteriExecutorGroup.set(i, ExecutorUtil.create(groupName, this.threadNum, isDaemon));
        }
    }

    public ForsteriExecutorGroup(String name, int size) {
        this(name,size,Runtime.getRuntime().availableProcessors(), false);
    }

    public ForsteriThreadPoolExecutor next() {
        return this.size == 1 ? this.forsteriExecutorGroup.get(0) : this.forsteriExecutorGroup.get(this.pos.getAndAdd(1) % this.size);
    }

}

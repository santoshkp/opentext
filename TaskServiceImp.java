package com.ot.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class TaskServiceImp implements Main.TaskExecutor {

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final Map<Main.TaskGroup, Semaphore> semaphore = new HashMap<>();

    public TaskServiceImp(Main.TaskGroup groupId) {
        semaphore.put(groupId, new Semaphore(1));
    }

    /**
     * @param task Task to be executed by the executor. Must not be null.
     * @param <T>
     * @return
     */
    @Override
    public <T> Future<T> submitTask(Main.Task<T> task) {

        return executor.submit(() -> {
            try {
                //use to each group which acquire the task
                semaphore.get(task.taskGroup()).acquire();
               return task.taskAction().call();
            } finally {
                semaphore.get(task.taskGroup()).release();
            }
        });
    }
}

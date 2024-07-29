package com.ot.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

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

        return (Future<T>) executor.submit(() -> {
            try {
                //use to each group which acquire the task
                semaphore.get(task.taskGroup()).acquire();
                task.taskAction().call();
            } catch (Exception e) {

            } finally {
                semaphore.get(task.taskGroup()).release();
            }
        });
    }
}

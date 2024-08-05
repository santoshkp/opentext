package com.ot.executor;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Main {

    /**
     * Enumeration of task types.
     */
    public enum TaskType {
        READ,
        WRITE,
    }

    public interface TaskExecutor {
        /**
         * Submit new task to be queued and executed.
         *
         * @param task Task to be executed by the executor. Must not be null.
         * @return Future for the task asynchronous computation result.
         */
        <T> Future<T> submitTask(Task<T> task);
    }

    /**
     * Representation of computation to be performed by the {@link TaskExecutor}.
     *
     * @param taskUUID Unique task identifier.
     * @param taskGroup Task group.
     * @param taskType Task type.
     * @param taskAction Callable representing task computation and returning the result.
     * @param <T> Task computation result value type.
     */
    public record Task<T>(
            UUID taskUUID,
            TaskGroup taskGroup,
            TaskType taskType,
            Callable<T> taskAction
    ) {
        public Task {
            if (taskUUID == null || taskGroup == null || taskType == null || taskAction == null) {
                throw new IllegalArgumentException("All parameters must not be null");
            }
        }
    }

    /**
     * Task group.
     *
     * @param groupUUID Unique group identifier.
     */
    public record TaskGroup(
            UUID groupUUID
    ) {
        public TaskGroup {
            if (groupUUID == null) {
                throw new IllegalArgumentException("All parameters must not be null");
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        TaskGroup taskGroup = new TaskGroup(UUID.randomUUID());
        CallableService<Result> cs1 = new CallableService<>("Task1");
        CallableService<Result> cs2 = new CallableService<>("Task2");
        CallableService<Result> cs3 = new CallableService<>("Task3");

        Task task1 = new Task(taskGroup.groupUUID(),taskGroup,TaskType.READ,cs1);
        Task task2 = new Task(taskGroup.groupUUID(),taskGroup,TaskType.READ,cs2);
        Task task3 = new Task(taskGroup.groupUUID(),taskGroup,TaskType.READ,cs3);

        TaskServiceImp taskServiceImp = new TaskServiceImp(taskGroup);
        List<Task> list = new ArrayList<>(List.of(task1,task2,task3));

        List<Future<?>> futures = new ArrayList<>();

        list.stream().forEach(task -> {
            Future<Result> result =taskServiceImp.submitTask(task);
            futures.add(result);
        });

        for (Future<?> future : futures) {
            System.out.println("Task Result::"+future.get());
        }


       /* list.parallelStream().forEach(task -> {
            Future<Result> result =taskServiceImp.submitTask(task);
            try {
                System.out.println("Result:: "+result.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        });*/
    }
}

package com.ot.executor;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class CallableService<R> implements Callable<Result> {
    private final String name;

    public CallableService(String name) {
        this.name = name;
    }

    @Override
    public Result call()
    {
        System.out.println("Starting task::"+ this.name +" Thread Name:: "+Thread.currentThread().getName());
        return new Result(this.name, LocalDateTime.now().toString());
    }
}

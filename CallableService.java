package com.ot.executor;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public class CallableService implements Callable<Result> {
    private final String name;

    public CallableService(String name) {
        this.name = name;
    }

    @Override
    public Result call()
    {
        System.out.println("%s: Staring::"+ this.name);

        return new Result(this.name, LocalDateTime.now().toString());
    }
}

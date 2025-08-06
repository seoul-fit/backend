package com.seoulfit.backend.batch;

public interface BatchService {
    void dailyBatch();

    default void weeklyBatch() {}

    default void monthlyBatch() {}
}

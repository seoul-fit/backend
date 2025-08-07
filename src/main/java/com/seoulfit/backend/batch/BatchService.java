package com.seoulfit.backend.batch;

public interface BatchService {
    default void dailyBatch() {}

    default void realTimeBatch() {}

    default void weeklyBatch() {}

    default void monthlyBatch() {}
}

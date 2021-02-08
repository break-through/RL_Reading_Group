package rl.env;

public interface StepResult<S, R> {
    R getReward();
    S getState();
}

package rl.env;

import rl.fig5.State;

public interface StepResult<S, R> {
    R getReward();
    S getState();
}

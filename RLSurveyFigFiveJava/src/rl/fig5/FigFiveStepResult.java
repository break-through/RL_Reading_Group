package rl.fig5;

import rl.env.StepResult;

public class FigFiveStepResult implements StepResult<State, Reward> {
    private final Reward reward;
    private final State nextState;
    
    public FigFiveStepResult(Reward reward, State nextState) {
        this.reward = reward;
        this.nextState = nextState;
    }
    
    @Override
    public Reward getReward() {
        return reward;
    }
    
    @Override
    public State getState() {
        return nextState;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof StepResult)) {
            return false;
        }
        StepResult<?,?> other = (StepResult<?, ?>) obj;
        return getReward().equals(other.getReward())
            && getState().equals(other.getState());
    }
    
    @Override
    public String toString() {
        return String.format(
            "StepResult(reward=%s, next_state=%s)",
            getReward(),
            getState()
        );
    }
}

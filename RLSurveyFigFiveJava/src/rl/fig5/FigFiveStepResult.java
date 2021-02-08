package rl.fig5;

import rl.env.StepResult;

public class FigFiveStepResult implements StepResult<IState, IReward> {
    private final IReward reward;
    private final IState nextState;
    
    public FigFiveStepResult(IReward reward, IState nextState) {
        this.reward = reward;
        this.nextState = nextState;
    }
    
    @Override
    public IReward getReward() {
        return reward;
    }
    
    @Override
    public IState getState() {
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

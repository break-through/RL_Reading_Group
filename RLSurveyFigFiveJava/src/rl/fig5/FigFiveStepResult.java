package rl.fig5;

import rl.env.IStepResult;

public class FigFiveStepResult extends IStepResult<IState, IReward> {
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
    
}

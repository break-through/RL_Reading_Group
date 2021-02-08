package rl.env;

public abstract class IStepResult<S> {
    public abstract IReward getReward();
    public abstract S getState();
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IStepResult)) {
            return false;
        }
        IStepResult<?> other = (IStepResult<?>) obj;
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

package rl.env;

import rl.fig5.IAction;

import java.util.Objects;

public abstract class IExperience<S, A, R> {
    abstract S getState();
    abstract A getAction();
    abstract R getReward();
    abstract S getNextState();
    
    @Override
    public String toString() {
        return String.format(
            "Experience(s=%s, a=%s, r=%s, s_prime=%s)",
            getState(),
            getAction(),
            getReward(),
            getNextState()
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IExperience)) {
            return false;
        }
        final IExperience<?, ?, ?> other = (IExperience<?, ?, ?>) obj;
        return getState().equals(other.getState())
            && getAction().equals(other.getAction())
            && getReward().equals(other.getReward())
            && getNextState().equals(other.getNextState());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getState(), getAction(), getReward(), getNextState());
    }
}

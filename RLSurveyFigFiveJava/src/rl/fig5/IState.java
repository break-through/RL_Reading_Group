package rl.fig5;

import java.util.Objects;

public abstract class IState {
    abstract int getN();
    
    @Override
    public String toString() {
        return String.format("State(%s)", this.getN());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IState)) {
            return false;
        }
        final IState other = (IState) obj;
        return this.getN() == other.getN();
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getN());
    }
}

package rl.fig5;

public abstract class State {
    abstract int getN();
    
    @Override
    public String toString() {
        return String.format("State(%s)", this.getN());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof State)) {
            return false;
        }
        final State other = (State) obj;
        return this.getN() == other.getN();
    }
}

package rl.fig5;

public abstract class IReward {
    abstract int reward();
    
    @Override
    public String toString() {
        return String.format("%s", this.reward());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IReward)) {
            return false;
        }
        final IReward other = (IReward) obj;
        return this.reward() == other.reward();
    }
}

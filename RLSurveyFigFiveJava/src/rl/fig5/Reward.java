package rl.fig5;

public abstract class Reward {
    abstract int reward();
    
    @Override
    public String toString() {
        return String.format("%s", this.reward());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Reward)) {
            return false;
        }
        final Reward other = (Reward) obj;
        return this.reward() == other.reward();
    }
}

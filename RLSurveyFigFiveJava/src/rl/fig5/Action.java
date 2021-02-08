package rl.fig5;

public abstract class Action {
    abstract String getName();
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Action)) {
            return false;
        }
        final Action other = (Action) obj;
        return this.getName().equals(other.getName());
    }
}

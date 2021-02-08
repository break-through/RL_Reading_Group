package rl.fig5;

public abstract class IAction {
    abstract String getName();
    
    @Override
    public String toString() {
        return this.getName();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IAction)) {
            return false;
        }
        final IAction other = (IAction) obj;
        return this.getName().equals(other.getName());
    }
}

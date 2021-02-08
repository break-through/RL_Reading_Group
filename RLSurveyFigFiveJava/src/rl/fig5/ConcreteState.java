package rl.fig5;

public class ConcreteState extends IState {
    final private int n;
    
    public ConcreteState(int n) {
        this.n = n;
    }
    
    @Override
    int getN() {
        return n;
    }
}

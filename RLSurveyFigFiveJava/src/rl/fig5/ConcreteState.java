package rl.fig5;

public class ConcreteState extends State {
    final private int n;
    
    public ConcreteState(int n) {
        this.n = n;
    }
    
    @Override
    int getN() {
        return n;
    }
}

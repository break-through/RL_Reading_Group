package rl.fig5;

final public class ConcreteEnvStats extends IEnvStats {
    final private int n;
    final private int n_sqr;
    final private int two_exp_n;
    
    public ConcreteEnvStats(int n) {
        this.n = n;
        this.n_sqr = n * n;
        this.two_exp_n = (int) Math.pow(2, n);
    }
    
    @Override
    public int getN() {
        return this.n;
    }
    
    @Override
    public int getNSquared() {
        return this.n_sqr;
    }
    
    @Override
    public int getTwoExpN() {
        return this.two_exp_n;
    }
}

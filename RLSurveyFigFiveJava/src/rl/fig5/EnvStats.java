package rl.fig5;

public abstract class EnvStats {
    abstract int getN();
    abstract int getNSquared();
    abstract int getTwoExpN();
    
    @Override
    final public String toString() {
        final int n = this.getN();
        final int n_squared = this.getNSquared();
        final int two_exp_n = this.getTwoExpN();
        return String.format(
            "EnvStats(n=%s, n_sqr=%s, 2**n=%s)",
            n,
            n_squared,
            two_exp_n
        );
    }
}

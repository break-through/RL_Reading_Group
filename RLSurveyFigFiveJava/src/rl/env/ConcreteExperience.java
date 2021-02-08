package rl.env;


public class ConcreteExperience<S, A, R> extends IExperience<S, A, R> {
    final private S s;
    final private A a;
    final private R r;
    final private S s_prime;
    
    public ConcreteExperience(S s, A a, R r, S s_prime) {
        this.s = s;
        this.a = a;
        this.r = r;
        this.s_prime = s_prime;
    }
    
    @Override
    final S getState() {
        return s;
    }
    
    @Override
    final A getAction() {
        return a;
    }
    
    @Override
    final R getReward() {
        return r;
    }
    
    @Override
    final S getNextState() {
        return s_prime;
    }
}

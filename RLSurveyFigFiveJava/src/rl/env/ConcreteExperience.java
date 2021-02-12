package rl.env;


public class ConcreteExperience<S, A> extends IExperience<S, A> {
    final private S s;
    final private A a;
    final private IReward r;
    final private S s_prime;
    
    public ConcreteExperience(S s, A a, IReward r, S s_prime) {
        this.s = s;
        this.a = a;
        this.r = r;
        this.s_prime = s_prime;
    }
    
    @Override
    final public S getState() {
        return s;
    }
    
    @Override
    final public A getAction() {
        return a;
    }
    
    @Override
    final public IReward getReward() {
        return r;
    }
    
    @Override
    final public S getNextState() {
        return s_prime;
    }
}

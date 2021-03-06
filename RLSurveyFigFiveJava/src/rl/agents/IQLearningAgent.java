package rl.agents;

import rl.env.IEnvironment;
import rl.env.IExperience;
import rl.env.RLException;
import util.*;

import java.util.*;

/**
 * This is an agent that learns by using the Q learning algorithm.
 *
 * NOTE:
 * - I've made some variables private and some others protected. If
 *   it's private, it's because it's intended to be (i.e., you shouldn't
 *   be able to access it in its subclass!).
 */
public abstract class IQLearningAgent<S, A> extends IAgent<S, A> {
    private final Map<Pair<S, A>, Double> Q;
    
    /**
     * Alpha is mainly used in the sample backups. It represents the
     * learning rate and ideally should be decreased down to zero
     * slowly.
     */
    private double alpha;
    
    /**
     * Gamma represents the discount factor.
     */
    private final double gamma;
    private final Map<Pair<S, A>, ICounterDistribution<S>> T;
    protected final Map<Pair<S, A>, Double> R;
    private final Counter sampleBackupsCounter;
    private final Counter fullBackupsCounter;
    
    public IQLearningAgent(
        IEnvironment<S, A> environment,
        double alpha,
        double gamma
    ) {
        super(environment);
        this.alpha = alpha;
        this.gamma = gamma;
        Q = new HashMap<>();
        T = new HashMap<>();
        R = new HashMap<>();
        sampleBackupsCounter = new Counter();
        fullBackupsCounter = new Counter();
        initializeQ();
    }
    
    final public double getAlpha() {
        return alpha;
    }
    
    final public double getGamma() {
        return gamma;
    }
    
    protected void initializeQ() {
        for (final Pair<S, A> pair : environment.getAllStateActionPairs()) {
            setQ(pair, RAND.nextDouble());
        }
    }
    
    final public long getTotalNumBackups() {
        return getNumSampleBackups() + getNumFullBackups();
    }
    
    final public long getNumSampleBackups() {
        return sampleBackupsCounter.value();
    }
    
    final public long getNumFullBackups() {
        return fullBackupsCounter.value();
    }
    
    
    final public double reward(S s, A a) {
        final Pair<S, A> pair = Pair.make(s, a);
        if (R.containsKey(pair)) {
            return R.get(pair);
        }
        return 0.0;
    }
    
    final public double transitionProb(S s, A a, S s_prime) {
        return getTransitionDistribution(s, a).prob(s_prime);
    }
    
    @Override
    final protected void updateModels() {
        final IExperience<S, A> experience = lastExperience();
        final S s = experience.getState();
        final A a = experience.getAction();
        final double r = experience.getReward().reward();
        final S s_prime = experience.getNextState();
        updateReward(s, a, r);
        updateTransitions(s, a, s_prime);
    }
    
    /**
     * This function is called after each sample backups.
     * It returns a value that is used to update the learning rate by
     * such that the new learning rate alpha = max(alpha + X, 0)
     * where X is what's returned here.
     */
    abstract protected double learningRateUpdate();
    
    /**
     * Updates the reward of state-action pair (s, a). This method
     * is not final, so you may override it to give it a more
     * sophisticated implementation. The current one simply assumes
     * that the received reward is always the reward (which may not
     * be the case in some environments!).
     */
    protected void updateReward(S s, A a, double r) {
        R.put(Pair.make(s, a), r);
    }
    
    private void updateTransitions(S s, A a, S s_prime) {
        final Pair<S, A> pair = Pair.make(s, a);
        final ICounterDistribution<S> dist = getTransitionDistribution(pair);
        dist.countUp(s_prime);
        T.put(pair, dist);
    }
    
    final protected ICounterDistribution<S> getTransitionDistribution(S s, A a) {
        return T.getOrDefault(Pair.make(s, a), new CounterDistribution<>());
    }
    
    final protected ICounterDistribution<S> getTransitionDistribution(Pair<S, A> pair) {
        return T.getOrDefault(pair, new CounterDistribution<>());
    }
    
    final protected void setQ(S s, A a, double value) {
        setQ(Pair.make(s, a), value);
    }
    
    final protected void setQ(Pair<S, A> pair, double value) {
        Q.put(pair, value);
    }
    
    final protected double getQ(S s, A a) {
        return Q.get(Pair.make(s, a));
    }
    
    final protected double getQ(Pair<S, A> pair) {
        return getQ(pair.getLeft(), pair.getRight());
    }
    
    final protected void fullBackup(Pair<S, A> pair) {
        fullBackup(pair.getLeft(), pair.getRight());
    }
    
    final protected void fullBackup(IExperience<S, A> experience) {
        fullBackup(experience.getState(), experience.getAction());
    }
    
    final protected void fullBackup(S s, A a) {
        fullBackupsCounter.increment();
        final IDistribution<S> dist = getTransitionDistribution(s, a);
        double summation = 0;
        for (S s_prime : dist.reachables()) {
            summation += dist.prob(s_prime) * maxQAtState(s_prime);
        }
        setQ(s, a, reward(s, a) + (gamma * summation));
        
    }
    
    final protected void sampleBackup(IExperience<S, A> experience) {
        sampleBackupsCounter.increment();
        final S s = experience.getState();
        final A a = experience.getAction();
        final double r = experience.getReward().reward();
        final S s_prime = experience.getNextState();
        setQ(s, a, alpha * (r + (gamma * maxQAtState(s_prime)) - getQ(s, a)));
        alpha = Math.max(0, alpha + learningRateUpdate());
    }
    
    final protected double maxQAtState(S s) {
        try {
            return getQ(s, bestActionAtState(s));
        } catch (RLException e) {
            return 0.0;
        }
    }
    
    final protected A bestActionAtState(S s) throws RLException {
        final Set<Pair<S, A>> all_pairs = Q.keySet();
        final List<Pair<S, A>> okay_pairs = new ArrayList<>();
        for (final Pair<S, A> pair : all_pairs) {
            if (!pair.getLeft().equals(s)) {
                continue;
            }
            okay_pairs.add(pair);
        }
    
        double best_pair_value = 0.0;
        if (okay_pairs.size() == 0) {
            throw new RLException("Didn't find any OK pair!");
        }
    
        Pair<S, A> best_pair = null;
        for (Pair<S, A> pair : okay_pairs) {
            double value = getQ(pair);
            if (best_pair != null && value <= best_pair_value) {
                continue;
            }
            best_pair = pair;
            best_pair_value = value;
        }
        return best_pair.getRight();
    }
}

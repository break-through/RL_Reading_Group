package util;

import java.util.*;

public final class CounterDistribution<S> implements ICounterDistribution<S> {
    private final Map<S, Counter> counts;
    private final Map<S, Double> probabilities;
    private boolean should_recompute = true;
    private static final Random RAND = new Random();
    
    public CounterDistribution() {
        this.counts = new HashMap<>(); // Shouldn't we be modifying 'this.counts' in the methods instead of 'counts' since it's a class attribute 'counts'
        this.probabilities = new HashMap<>();
    }
    
    @Override
    public void countUp(S s) {
        // Questions:
           // why can we change 'counts' if its final variable?
           // 'countUp' isn't called, should we call it after sampling the state?
        if (!hasState(s)) {
            counts.put(s, new Counter());
        }
        counts.get(s).increment();
        should_recompute = true;
    }

    @Override
    public void add(S s) {
        if(hasState(s)) {
            throw new RuntimeException("State being added is already in state-space");
        }
        counts.put(s, new Counter());
        setProbability(s, 0.0);
    }
    
    @Override
    public double prob(S s) {
        if (!hasState(s)) {
            return 0.0;
        }
        checkRecomputation();
        return probabilities.get(s);
    }
    
    @Override
    public S sample() {
        // From:
        // https://stackoverflow.com/questions/35701316/discrete-probability-distribution-in-java
        final List<Pair<S, Double>> probabilities = new ArrayList<>();
        for (S s : counts.keySet()) {
            probabilities.add(Pair.make(s, prob(s)));
        }
        double prob = RAND.nextDouble();
        int i;
        for (i = 0; prob > 0; i++) {
            prob -= probabilities.get(i).getRight();
        }
        return probabilities.get(i - 1).getLeft();
    }
    
    @Override
    public List<S> reachables() {
        List<S> reachables = new ArrayList<>();
        checkRecomputation();
        for (S s : counts.keySet()) {
            if (prob(s) > 0) {
                reachables.add(s);
            }
        }
        return reachables;
    }
    
    @Override
    public String toString() {
        return "CounterDistribution{}";
    }
    
    // Private Methods
    
    private void checkRecomputation() {
        if (should_recompute) {
            should_recompute = false;
            recomputeProbabilities();
        }
    }
    
    private void recomputeProbabilities() {
        final Set<S> keys = counts.keySet();
        long sum = 0;
        for (S s : keys) {
            sum += count(s);
        }
        for (S s : keys) {
            final double prob = ((double) count(s)) / sum;
            setProbability(s, prob);
        }
    }
    
    private long count(S s) {
        if (!hasState(s)) {
            return 0;
        }
        return counts.get(s).value();
    }
    
    private void setProbability(S s, double prob) {
        assert prob >= 0.0;
        assert prob <= 1.0;
        probabilities.put(s, prob);
    }
    
    private boolean hasState(S s) {
        return counts.containsKey(s);
    }
}

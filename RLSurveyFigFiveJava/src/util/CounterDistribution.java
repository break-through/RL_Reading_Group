package util;

import java.util.*;

public class CounterDistribution<S> implements ICounterDistribution<S> {
    private final Map<S, Counter> counts;
    private final Map<S, Double> probabilities;
    private boolean should_recompute = true;
    private static final Random RAND = new Random();
    
    public CounterDistribution() {
        this.counts = new HashMap<>();
        this.probabilities = new HashMap<>();
    }
    
    @Override
    public void countUp(S s) {
        if (!hasState(s)) {
            counts.put(s, new Counter());
        }
        counts.get(s).increment();
        should_recompute = true;
    }
    
    @Override
    public double prob(S s) {
        if (!hasState(s)) {
            return 0.0;
        }
        if (should_recompute) {
            should_recompute = false;
            recomputeProbabilities();
        }
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
    public String toString() {
        return super.toString();
    }
    
    // Private Methods
    
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

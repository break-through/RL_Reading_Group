package util;

import java.util.*;

public final class EqualDistribution<S> implements IDistribution<S> {
    private final List<S> states;
    private final double prob;
    private final Random random = new Random();
    
     public EqualDistribution(Collection<S> states) {
         if (states.size() == 0) {
             throw new IllegalArgumentException(
                 "EqualDistribution requires at least one element"
             );
         }
         this.states = new ArrayList<>(states);
         prob = 1 / (double) size();
     }
    
    @Override
    public double prob(S s) {
         if (!states.contains(s)) {
             return 0.0;
         }
         return prob;
    }
    
    @Override
    public S sample() {
         final int index = random.nextInt(size());
         return states.get(index);
    }
    
    @Override
    public List<S> reachables() {
        return new ArrayList<>(states);
    }
    
    @Override
    public String toString() {
        return "EqualDistribution{}";
    }
    
    public EqualDistribution<S> add(S s) {
         // TODO: Victor implement this.
        throw new RuntimeException("not implemented");
    }
    
    private int size() {
         return this.states.size();
    }
}

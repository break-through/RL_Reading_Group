package util;

import java.util.*;

public final class EqualDistribution<S> implements IDistribution<S> {
    private final List<S> states;
    private double prob;
    private final Random random = new Random();

    // This is a constructor. Takes in arguments 'states'
    public EqualDistribution(Collection<S> states) {
        // if no states, i.e. states=[], throw argument
        if (states.size() == 0) {
            throw new IllegalArgumentException(
                 "EqualDistribution requires at least one element"
            );
        }
        // set the instantiated object's states to the ones passed in
        this.states = new ArrayList<>(states);
        // set prob to 1/size() (why not have prob as object property instead of class property?)
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

    @Override
    final public void add(S s) {
         // TODO: Victor implement this.
        // Questions:
           // since this operation makes sense with any distribution, describe it as an abstract method in IDistribution?
        this.states.add(s);
        prob = 1 / (double) size();
    }

    private int size() {
         return this.states.size();
    }
}

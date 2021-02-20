package util;

import java.util.List;

public interface IDistribution<S> {
    double prob(S s);
    S sample();
    void add(S s);
    // Returns a list of states such that this.prob(s) > 0 for all s
    // in that list
    List<S> reachables();
}

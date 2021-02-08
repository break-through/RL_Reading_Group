package util;

public interface IDistribution<S> {
    double prob(S s);
    S sample();
}

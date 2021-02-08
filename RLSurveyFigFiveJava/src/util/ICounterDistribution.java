package util;

public interface ICounterDistribution<S> extends IDistribution<S> {
    void countUp(S s);
}

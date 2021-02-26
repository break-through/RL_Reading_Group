package util;

import java.lang.Math.*;

public class GaussianDistribution implements ICounterDistribution<Double> {
    private final Map<S, Counter> counts;
    private final Random random = new Random();

    public GaussianDistribution(double mean, double variance) {
        final this.mean = mean;
        final this.stddev = sqrt(variance);
    }

    @Override
    public double prob(double x) {
        return 1/(this.stddev * sqrt(2*PI)) * pow(E, -0.5 * pow((x - this.mean)/this.stddev, 2));
    }
}

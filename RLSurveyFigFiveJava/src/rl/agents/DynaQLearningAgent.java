package rl.agents;

import rl.env.IEnvironment;
import rl.env.IExperience;
import rl.env.RLException;
import util.Pair;

import java.util.List;

public class DynaQLearningAgent<S, A> extends IQLearningAgent<S, A> {
    private final int k;
    public DynaQLearningAgent(
        IEnvironment<S, A> environment,
        double alpha,
        double gamma,
        int k
    ) {
        super(environment, alpha, gamma);
        this.k = k;
    }
    
    @Override
    A policy() {
        try {
            return bestActionAtState(getCurrentState());
        } catch (RLException e) {
            // TODO: Sample from an "EqualDistribution" that takes
            //  in the list of available actions at the current state.
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void updateReward(S s, A a, double r) {
        super.updateReward(s, a, r);
    }
    
    @Override
    void learn() {
        final IExperience<S, A> experience = lastExperience();
        fullBackup(experience);
        for (Pair<S, A> pair : sampleKStateActionPairs()) {
            fullBackup(pair);
        }
    }
    
    @Override
    double value(S s) {
        return maxQAtState(s);
    }
    
    private List<Pair<S, A>> sampleKStateActionPairs() {
        // TODO: Implement an "EqualDistribution" that takes in
        //  a list of states, and then sample from it k times. will
        //  need to add another method to IDistribution that's like
        //  `List<S> sample(int k)` so we can sample k things from it.
        throw new RuntimeException("Not implemented");
    }
}

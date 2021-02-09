package rl.agents;

import rl.env.IEnvironment;
import rl.env.RLException;
import util.EqualDistribution;
import util.Pair;

import java.util.*;

public final class DynaQLearningAgent<S, A> extends IQLearningAgent<S, A> {
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
    
    public int getK() {
        return k;
    }
    
    @Override
    A policy() {
        try {
            return bestActionAtState(getCurrentState());
        } catch (RLException e) {
            return new EqualDistribution<>(availableActions()).sample();
        }
    }
    
    @Override
    protected void updateReward(S s, A a, double r) {
        super.updateReward(s, a, r);
    }
    
    @Override
    void learn() {
        fullBackup(lastExperience());
        for (Pair<S, A> pair : sampleKStateActionPairs()) {
            fullBackup(pair);
        }
    }
    
    @Override
    double value(S s) {
        return maxQAtState(s);
    }
    
    private List<Pair<S, A>> sampleKStateActionPairs() {
        List<Pair<S, A>> state_action_pairs = new ArrayList<>(
            getObservedStateActionPairs()
        );
        if (state_action_pairs.size() < k) {
            return state_action_pairs;
        }
        Collections.shuffle(state_action_pairs);
        return state_action_pairs.subList(0, k);
    }
}

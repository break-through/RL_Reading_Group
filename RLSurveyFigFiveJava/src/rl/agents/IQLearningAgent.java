package rl.agents;

import rl.env.IEnvironment;
import rl.env.IExperience;
import rl.env.IReward;
import util.Pair;

import java.util.*;

public abstract class IQLearningAgent<S, A> extends IAgent<S, A> {
    private final Map<Pair<S, A>, Double> Q;
    public IQLearningAgent(IEnvironment<S, A> environment) {
        super(environment);
        this.Q = new HashMap<>();
    }
    
    final protected void setQ(S s, A a, double value) {
        Q.put(Pair.make(s, a), value);
    }
    
    final protected double getQ(S s, A a) {
        return Q.get(Pair.make(s, a));
    }
    
    final protected int sampleBackup(
        IExperience<S, A> experience,
        double gamma,
        double alpha
    ) {
        final S s = experience.getState();
        final A a = experience.getAction();
        final double r = experience.getReward().reward();
        final S s_prime = experience.getNextState();
        double new_value = alpha * (r + (gamma * maxQAtState(s_prime)) - getQ(s, a));
        setQ(s, a, new_value);
        return 1;
    }
    
    final protected double maxQAtState(S s) {
        final Set<Pair<S, A>> all_pairs = Q.keySet();
        final List<Pair<S, A>> okay_pairs = new ArrayList<>();
        for (Pair<S, A> pair : all_pairs) {
            if (!pair.getLeft().equals(s)) {
                continue;
            }
            okay_pairs.add(pair);
        }
        
        double best_pair_value = 0.0;
        if (okay_pairs.size() == 0) {
            return best_pair_value;
        }
        
        // We don't actually care what the best pair is, but if we
        // did we could store it in a variable.
        for (Pair<S, A> pair : okay_pairs) {
            double value = getQ(pair.getLeft(), pair.getRight());
            if (value <= best_pair_value) {
                continue;
            }
            best_pair_value = value;
        }
        return best_pair_value;
    }
}

package rl.env;

import util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Environment
 *
 * This represents a reinforcement learning (RL) environment in which
 * an agent can navigate in. The Agent finds themselves in states of
 * type S and can take actions of type A.
 * @see #getActionsForState(Object)
 *
 * When an agent is placed in this environment, they begin at the state
 * returned by the start state
 * @see #getStartState()
 *
 * In addition, whenever an agent steps through the environment, that
 * agent may earn a reward that is encoded in the StepResult.
 * @see #step(Object, Object)
 */
public abstract class IEnvironment<S, A> {
    public abstract S getStartState();
    public abstract List<S> getAllStates();
    public abstract List<A> getActionsForState(S state);
    public abstract IStepResult<S> step(S state, A action);
    
    final public List<Pair<S, A>> getAllStateActionPairs() {
        List<Pair<S, A>> all_pairs = new ArrayList<>();
        for (final S s : getAllStates()) {
            for (final A a : getActionsForState(s)) {
                all_pairs.add(Pair.make(s, a));
            }
        }
        return all_pairs;
    }
}

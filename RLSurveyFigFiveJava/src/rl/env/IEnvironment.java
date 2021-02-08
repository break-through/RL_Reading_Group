package rl.env;

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
public interface IEnvironment<S, A> {
    S getStartState();
    List<A> getActionsForState(S state);
    IStepResult<S> step(S state, A action);
}

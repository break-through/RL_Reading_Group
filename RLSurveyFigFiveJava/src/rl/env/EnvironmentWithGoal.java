package rl.env;

import java.util.List;

/**
 * EnvironmentWithGoal
 *
 * This is the same as an {@link Environment} except it has a goal state.
 * Through taking various actions, the agent may find themselves in
 * a given state such that isGoalState(s) is true for that state. In
 * this sense, this represents an environment that has one or many
 * goal states that an agent ideally is trying to get to.
 * @see #isGoalState(Object)
 */
public interface EnvironmentWithGoal<S, A, R> extends Environment<S, A, R> {
    boolean isGoalState(S state);
}

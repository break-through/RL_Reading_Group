package rl.env;

/**
 * EnvironmentWithGoal
 *
 * This is the same as an {@link IEnvironment} except it has a goal state.
 * Through taking various actions, the agent may find themselves in
 * a given state such that isGoalState(s) is true for that state. In
 * this sense, this represents an environment that has one or many
 * goal states that an agent ideally is trying to get to.
 * @see #isGoalState(Object)
 */
public interface IEnvironmentWithGoal<S, A> extends IEnvironment<S, A> {
    boolean isGoalState(S state);
}

package rl.env;

import rl.fig5.Action;
import rl.fig5.FigFiveEnvironment;
import rl.fig5.Reward;
import rl.fig5.State;

public final class EnvironmentFactory {
    public static EnvironmentWithGoal<State, Action, Reward> getFigFiveEnvironment(int n) {
        return new FigFiveEnvironment(n);
    }
}

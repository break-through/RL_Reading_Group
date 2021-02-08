package rl.env;

import rl.fig5.IAction;
import rl.fig5.FigFiveEnvironment;
import rl.fig5.IReward;
import rl.fig5.IState;

public final class EnvironmentFactory {
    public static IEnvironmentWithGoal<IState, IAction, IReward> getFigFiveEnvironment(int n) {
        return new FigFiveEnvironment(n);
    }
}

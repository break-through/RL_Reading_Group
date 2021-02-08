package util;

import rl.env.IEnvironmentWithGoal;
import rl.fig5.IAction;
import rl.fig5.FigFiveEnvironment;
import rl.env.IReward;
import rl.fig5.IState;

public final class EnvironmentFactory {
    public static IEnvironmentWithGoal<IState, IAction> getFigFiveEnvironment(int n) {
        return new FigFiveEnvironment(n);
    }
}

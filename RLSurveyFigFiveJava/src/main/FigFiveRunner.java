package main;

import rl.env.IStepResult;
import rl.fig5.IAction;
import rl.fig5.FigFiveEnvironment;
import rl.env.IReward;
import rl.fig5.IState;

public class FigFiveRunner {
    public static void main(String[] args) {
        final int N = 5;
        FigFiveEnvironment env = new FigFiveEnvironment(N);
        System.out.println(env.getStats());
        System.out.println(env);
        IState currentState = env.getStartState();
        System.out.println(env.getStats());
        System.out.println(env.toStringInState(currentState));
        System.out.println(env.getActionsForState(currentState));
        IAction action = env.getActionsForState(currentState).get(0);
        IStepResult<IState> stepResult = env.step(currentState, action);
        System.out.println(stepResult);
        currentState = stepResult.getState();
        System.out.println(env.toStringInState(currentState));
        System.out.println(env.getActionsForState(currentState));
    }
}

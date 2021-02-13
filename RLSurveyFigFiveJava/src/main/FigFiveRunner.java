package main;

import rl.agents.DynaQLearningAgent;
import rl.env.IStepResult;
import rl.fig5.IAction;
import rl.fig5.FigFiveEnvironment;
import rl.fig5.IState;

public class FigFiveRunner {
    public static void main(String[] args) {
        runFigFive();
        if (true) {
            System.exit(0);
        }
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
    
    private static void runFigFive() {
        final int N = 20;
        final double ALPHA = 0.1;
        final double GAMMA = 0.1;
        final int K = 4;
        final int STEPS = 20;
        FigFiveEnvironment env = new FigFiveEnvironment(N);
        DynaQLearningAgent<IState, IAction> agent = new DynaQLearningAgent<>(env, ALPHA, GAMMA, K);
    
        System.out.println("ENVIRONMENT STATS:");
        System.out.println("=========================================");
        System.out.printf("- getStats: %s", env.getStats());
        if (!agent.canStep()) {
            System.out.println("JUST STARTED BUT CAN'T EVEN STEP! STOPPING NOW.");
            return;
        }
        System.out.println("Starting State:");
        System.out.println(env.toStringInState(agent.getCurrentState()));
        for (int i = 0; i < STEPS; i++) {
            System.out.println("=========================================");
            System.out.printf("RUN #%d\n", i);
            System.out.println("=========================================");
            System.out.println("Stepping:");
            agent.step();
            System.out.printf("- lastExperience: %s \n", agent.lastExperience());
            System.out.println("State After Step:");
            System.out.println(env.toStringInState(agent.getCurrentState()));
            System.out.println("Agent Details After Step:");
            System.out.printf("- getNumSteps: %s \n", agent.getNumSteps());
            System.out.printf("- getAccumulatedRewards: %s \n", agent.getAccumulatedRewards());
            System.out.printf("- getNumFullBackups: %s \n", agent.getNumFullBackups());
            System.out.printf("- getNumSampleBackups: %s \n", agent.getNumSampleBackups());
            System.out.printf("- getTotalNumBackups: %s \n", agent.getTotalNumBackups());
            if (!agent.canStep()) {
                System.out.println("STOPPING BECAUSE CAN'T STEP ANYMORE");
                break;
            }
            System.out.println();
        }
    }
}

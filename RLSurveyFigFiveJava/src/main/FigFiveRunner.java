package main;

import rl.agents.DynaQLearningAgent;
import rl.agents.IQLearningAgent;
import rl.agents.PrioritizedSweepingAgent;
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
        final int N = 10;
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

        double GAMMA = .99;
        double ALPHA = .01;
        int K = 5;
        int MAX_ITER = 5000;
        int iter = 0;

        PrioritizedSweepingAgent<IState, IAction> ayoPS = new PrioritizedSweepingAgent<>(env, ALPHA, GAMMA, K);

        iter = 0;

        while(ayoPS.canStep() && iter < MAX_ITER){
            System.out.println("=========================================");
            System.out.printf("RUN #%d\n", iter);
            System.out.println("=========================================");
            System.out.println("Stepping:");
            ayoPS.step(); //Discuss order.
            System.out.printf("- lastExperience: %s \n", ayoPS.lastExperience());
            System.out.println("State After Step:");
            System.out.println(env.toStringInState(ayoPS.getCurrentState()));
            System.out.println("Agent Details After Step:");
            printAgentDetails(ayoPS);
            if (!ayoPS.canStep()) {
                System.out.println();
                System.out.println("STOPPING BECAUSE CAN'T STEP ANYMORE");
                break;
            }
            System.out.println();
            iter++;
        }

        System.out.println();
        System.out.println("OK DONE. Here are details one more time");
        System.out.println("=========================================");
        System.out.println("Environment:");
        System.out.printf("- getStats: %s\n", env.getStats());
        System.out.printf("- K: %s\n", K);
        System.out.printf("- N: %s\n", N);
        System.out.printf("- ALPHA: %s\n", ALPHA);
        System.out.printf("- GAMMA: %s\n", GAMMA);
        System.out.println("Agent:");
        System.out.printf("- lastExperience: %s \n", ayoPS.lastExperience());
        printAgentDetails(ayoPS);

        //multiagents :p?


    }
    
    private static void runFigFive() {
        final int N = 12;
        final double ALPHA = 0.1;
        final double GAMMA = 0.99;
        final int K = 8;
        final int STEPS = 100000;
        FigFiveEnvironment env = new FigFiveEnvironment(N);
        DynaQLearningAgent<IState, IAction> agent = new DynaQLearningAgent<>(env, ALPHA, GAMMA, K);
    
        System.out.println("=========================================");
        System.out.println("Environment:");
        System.out.printf("- getStats: %s\n", env.getStats());
        System.out.printf("- K: %s\n", K);
        System.out.printf("- N: %s\n", N);
        System.out.printf("- ALPHA: %s\n", ALPHA);
        System.out.printf("- GAMMA: %s\n", GAMMA);
        System.out.println("Agent:");
        printAgentDetails(agent);
        if (!agent.canStep()) {
            System.out.println("JUST STARTED BUT CAN'T EVEN STEP! STOPPING NOW.");
            return;
        }
        System.out.println("Starting Agent State:");
        System.out.println(env.toStringInState(agent.getCurrentState()));
        System.out.println("BEGIN RUNS");
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
            printAgentDetails(agent);
            if (!agent.canStep()) {
                System.out.println();
                System.out.println("STOPPING BECAUSE CAN'T STEP ANYMORE");
                break;
            }
            System.out.println();
        }
        System.out.println();
        System.out.println("OK DONE. Here are details one more time");
        System.out.println("=========================================");
        System.out.println("Environment:");
        System.out.printf("- getStats: %s\n", env.getStats());
        System.out.printf("- K: %s\n", K);
        System.out.printf("- N: %s\n", N);
        System.out.printf("- ALPHA: %s\n", ALPHA);
        System.out.printf("- GAMMA: %s\n", GAMMA);
        System.out.println("Agent:");
        System.out.printf("- lastExperience: %s \n", agent.lastExperience());
        printAgentDetails(agent);
    }
    
    private static void printAgentDetails(IQLearningAgent<IState, IAction> agent) {
        System.out.printf("- getNumSteps: %s \n", agent.getNumSteps());
        System.out.printf("- getAccumulatedRewards: %s \n", agent.getAccumulatedRewards());
        System.out.printf("- getNumFullBackups: %s \n", agent.getNumFullBackups());
        System.out.printf("- getNumSampleBackups: %s \n", agent.getNumSampleBackups());
        System.out.printf("- getTotalNumBackups: %s \n", agent.getTotalNumBackups());
        System.out.printf("- getAlpha: %s \n", agent.getAlpha());
    }
}

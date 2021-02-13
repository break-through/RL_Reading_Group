package main;

import rl.agents.DynaQLearningAgent;
import rl.agents.PrioritizedSweepingAgent;
import rl.env.IStepResult;
import rl.fig5.IAction;
import rl.fig5.FigFiveEnvironment;
import rl.fig5.IState;

public class FigFiveRunner {
    public static void main(String[] args) {
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

        double gamma = .99;
        double alpha = .01;
        int k = 5;
        DynaQLearningAgent<IState, IAction> theG = new DynaQLearningAgent<>(env, alpha, gamma, k);

        int MAX_ITER = 5;
        int iter = 0;
        while(theG.canStep() && iter < MAX_ITER){
            IState s = theG.getCurrentState(); //how is this supposed to happen? ie how do we 'step' the agent itself?
            //current methods are not public - I can't pass in the agents state/action to the env presently
            //the 'policy' method is out is what I mean. Isn't the agent supposed to:
            //see where it is
            //learn
            //get the optimal policy
            //then step?
            //this requires the agent to have 'learn' and 'policy' as visible methods; or it requires the env
            //automatically does that.
            //There may be misintrepetations here - let's discuss. I see that there is no 'limit' to what we can make
            //it - so
            iter++;

        }

        PrioritizedSweepingAgent<IState, IAction> thePriority = new PrioritizedSweepingAgent<>(env, alpha, gamma, k);
        //In my implementation of PS, there was no barrier to make it 'public' from the design so I've made this public.
        iter = 0;

        while(thePriority.canStep() && iter < MAX_ITER){
            IState curS = thePriority.getCurrentState();

            System.out.println(env.getStats());
            System.out.println(env.toStringInState(curS));
            System.out.println(env.getActionsForState(curS)); //each iteartion it will re update

            thePriority.learn();
            IAction acs = thePriority.policy();
            IStepResult<IState> sR = env.step(curS, acs);

        }

        IState finState = thePriority.getCurrentState();

        System.out.println(env.getStats());
        System.out.println(env.toStringInState(finState));
        System.out.println(env.getActionsForState(finState)); //each iteartion it will re update

        //multiagents :p?


    }
}

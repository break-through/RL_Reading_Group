package rl.agents;

import rl.env.ConcreteExperience;
import rl.env.IEnvironment;
import rl.env.IExperience;
import rl.env.IStepResult;
import util.Counter;
import util.Pair;

import java.util.*;

/**
 * This represents an agent that is moving through an environment. All of
 * the generic parameters *should* be hashable (at the time of writing this,
 * they *were* hashable, and if you add more, make sure they *are* hashable!),
 * which allows agent implementers to store them in hash-tables and such. In
 * addition, experiences ({@link IExperience}) are also hashable.
 *
 * @param <S> The kind of states you find in this environment
 * @param <A> The actions that this agent can take in this environment
 *
 * NOTE:
 * - DO NOT MODIFY THIS FILE UNLESS IT MAKES SENSE TO.
 * - I've made variables private EXPLICITLY. If it's private, it's meant
 *   to be private, because you shouldn't be able to access that variable
 *   in a subclass.
 */
public abstract class IAgent<S, A> {
    /*
     * The environment of this agent. This environment can be accessed
     * publicly, and it represents the environment of this agent.
     */
    protected final IEnvironment<S, A> environment;
    /*
     * The history of this agent. This allows you to retrace where this
     * agent has been.
     */
    private final List<IExperience<S, A>> history;
    
    /*
     * This counts the number of steps we've made so far!
     */
    private final Counter stepCounter;
    
    /*
     * This keeps track of the set of observed state action pairs so
     * far. This variable can be easily gotten from the history, but
     * this is extracting it here because it'll be more expensive to
     * get it from history.
     */
    private final Set<Pair<S, A>> observedStateActionPairs;
    
    /*
     * This represents the accumulated rewards
     */
    private double accumulatedRewards;
    
    public IAgent(IEnvironment<S, A> environment) {
        this.environment = environment;
        history = new ArrayList<>();
        stepCounter = new Counter();
        observedStateActionPairs = new HashSet<>();
        accumulatedRewards = 0.0;
    }
    
    // Public Methods that are Useful
    
    public IEnvironment<S, A> getEnvironment() {
        return environment;
    }
    
    public double getAccumulatedRewards() {
        return accumulatedRewards;
    }
    
    public Set<Pair<S, A>> getObservedStateActionPairs() {
        return new HashSet<>(observedStateActionPairs);
    }
    
    /**
     * Returns the current state that this agent is in this its environment
     */
    final public S getCurrentState() {
        return lastState();
    }
    
    /**
     * Returns the actions that this agent can take at its current state.
     * NOTE: if this returns an empty list, that means that the agent has
     * hit a point where there are no way back or forward. Essentially, it's
     * "stuck", and it's an "end state". In some cases, this may be a
     * goal-state.
     */
    final public List<A> availableActions() {
        return environment.getActionsForState(getCurrentState());
    }
    
    
    /**
     * Returns the most recent experience that this agent acquired by
     * having stepped into its environment.
     */
    final public IExperience<S, A> lastExperience() {
        return lastExperienceImpl();
    }
    
    final public List<IExperience<S, A>> history() {
        return new ArrayList<>(history);
    }
    
    final public boolean hasHistory() {
        return this.history.size() > 0;
    }
    
    
    /**
     * Policy returns the best action for the current state that this agent
     * is in. It's to be implemented by the class that implements this
     * interface. This policy is meant to take into accounts all the learnings
     * so far and use them to return the action that will lead to the highest
     * reward as seen by this agent.
     *
     * Useful methods (which should all be self explanatory):
     * - {@link IAgent#getCurrentState()}
     * - The history variable (keep in mind, it's READ-ONLY!)
     * - {@link IAgent#lastExperience()}
     * - {@link IAgent#availableActions()}
     * In your agent implementation, feel free to take store additional
     * variables!
     */
    abstract A policy();
    
    /**
     * This method should learn from the most recent experience and,
     * optionally, from all the previous experiences stored in the
     * history variables. You may store additional variables to make
     * this computation even easier (in fact you probably have to
     * because where are you gonna store all that "learning" anyways?).
     *
     * Useful methods are listed in the {@link IAgent#policy()} method.
     */
    abstract void learn();
    
    /**
     * Returns the optimal value of the state s
     */
    abstract double value(S s);
    
    /**
     * Make the agent step in the environment. This automatically
     * adds an experience to this agent's experience and "learns"
     * from the experience of taking that step.
     *
     * For implementers:
     * - What internally happens is that the agent takes an action
     *   based on calling {@link IAgent#policy()}.
     * - Then, this creates an experience based on taking that
     *   action in this agent's environment. Then, it calls
     *   {@link IAgent#learn()}. For the implemented, you may decide
     *   to learn from the last history (through
     *   {@link IAgent#lastExperience()}) or from the entire history
     *   (by accessing the history field). In addition, you may add
     *   additional fields to keep track of other things.
     * If curious as to how all that happens, take a look at its
     * implementation function. However, DO NOT MODIFY IT.
     *
     * NOTE:
     * - This will do nothing if the agent is "stuck", as explained in
     *   {@link IAgent#availableActions()}. This is encoded in the
     *   {@link IAgent#canStep()} method, which when it can't, this
     *   step function will do nothing.
     */
    final public void step() {
        stepImpl();
    }
    
    final public boolean canStep() {
        return availableActions().size() > 0;
    }
    
    final public long getNumSteps() {
        return stepCounter.value();
    }
    
    //
    // Protected Methods (that you can use in subclasses)
    //
    
    /**
     * For Model-Based algorithms, implement this method to update
     * the models right after taking an experience (i.e., an experience
     * tuple). This method is called in the step phase before calling
     * the {@link IAgent#learn()} method.
     *
     * Useful methods are listed in the {@link IAgent#policy()} method.
     */
    protected void updateModels() {
    }
    
    final protected S lastState() {
        if (hasHistory()) {
            return this.lastExperienceImpl().getNextState();
        }
        return environment.getStartState();
    }
    
    final protected IExperience<S, A> lastExperienceImpl() {
        if (!hasHistory()) {
            throw new RuntimeException("This agent doesn't have a history");
        }
        return this.history.get(this.history.size() - 1);
    }
    
    //
    // Private Methods (you can't use these in subclasses)
    //
    
    private void stepImpl() {
        if (canStep()) {
            // Do nothing if we can't take any actions at this state.
            return;
        }
        stepCounter.increment();
        final A action = policy();
        final S currentState = getCurrentState();
        final IStepResult<S> stepResult = environment.step(currentState, action);
        final IExperience<S, A> experience = new ConcreteExperience<>(
            currentState,
            action,
            stepResult.getReward(),
            stepResult.getState()
        );
        observedStateActionPairs.add(
            Pair.make(experience.getState(), experience.getAction())
        );
        accumulatedRewards += experience.getReward().reward();
        history.add(experience);
        updateModels();
        learn();
    }
}

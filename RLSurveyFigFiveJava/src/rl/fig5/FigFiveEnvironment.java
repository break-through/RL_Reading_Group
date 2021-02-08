package rl.fig5;

import rl.env.EnvironmentWithGoal;
import rl.env.StepResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final public class FigFiveEnvironment implements EnvironmentWithGoal<State, Action, Reward>, Iterable<State> {
    final private int n;
    final private EnvStats stats;
    
    public FigFiveEnvironment(int n) {
        this.n = n;
        this.stats = new ConcreteEnvStats(n);
    }
    
    public EnvStats getStats() {
        return this.stats;
    }
    
    @Override
    public State getStartState() {
        return START_STATE;
    }
    
    @Override
    public List<Action> getActionsForState(State state) {
        ArrayList<Action> actions = new ArrayList<>();
        if (state.getN() <= n) {
            actions.add(FORWARD);
        }
        if (state.getN() > 1 && state.getN() <= n) {
            actions.add(BACK);
        }
        return actions;
    }
    
    @Override
    public StepResult<State, Reward> step(State state, Action action) {
        raiseIfInvalidState(state);
        raiseIfInvalidActionForState(state, action);
        State nextState = action.equals(FORWARD)
            ? new ConcreteState(state.getN() + 1)
            : START_STATE;
        return new FigFiveStepResult(
            new ConcreteReward(nextState.equals(goalState()) ? 1 : 0),
            nextState
        );
    }
    
    @Override
    public boolean isGoalState(State state) {
        return state.equals(goalState());
    }
    
    public String toStringInState(State state) {
        raiseIfInvalidState(state);
        return toStringInStateImpl(state);
    }
    
    @Override
    public String toString() {
        return toStringImpl();
    }
    
    @Override
    public Iterator<State> iterator() {
        final int max = n;
        return new Iterator<>() {
            private final int n = max;
            private int next = 1;
            @Override
            public boolean hasNext() {
                return next <= n + 1;
            }
    
            @Override
            public State next() {
                if (next > n + 1) {
                    throw new NoSuchElementException();
                }
                next += 1;
                return new ConcreteState(next - 1);
            }
        };
    }
    
    //
    // Private Methods
    //
    
    final private static int START_N = 1;
    final private static Action FORWARD = new ConcreteAction("forward");
    final private static Action BACK = new ConcreteAction("back");
    final private static State START_STATE = new ConcreteState(1);
    final private static String NEWLINE = "\n";
    final private static int PRINT_PAD = 5;
    
    private State goalState() {
        return new ConcreteState(n + 1);
    }
    
    private void raiseIfInvalidState(State state) {
        if (state.getN() < START_N || state.getN() > goalState().getN()) {
            throw new IllegalArgumentException(String.format(
                "Invalid state received (%s)",
                state
            ));
        }
    }
    
    private void raiseIfInvalidActionForState(State state, Action action) {
        raiseIfInvalidState(state);
        if (isGoalState(state)) {
            throw new RuntimeException(
                "You cannot take an action at the goal state!"
            );
        }
        if (action.equals(BACK) && state.equals(START_STATE)) {
            throw new RuntimeException(
                "You cannot go back from the start state!"
            );
        }
    }
    
    private String toStringInStateImpl(State state) {
        String print = "";
        print += toStringImpl();
        print += NEWLINE;
        print += indicationRow(state);
        return print;
    }
    
    private String toStringImpl() {
        String print = "";
        print += row("<--");
        print += NEWLINE;
        print += row("|");
        print += NEWLINE;
        print += rowStates();
        return print;
    }
    
    private String row(String value) {
        return row(value, null, null, "", "");
    }
    
    private String indicationRow(State current) {
        return row(" ", current, "^", null, null);
    }
    
    private String row(
        String value,
        State current,
        String replacement,
        String first_state,
        String last_state
    ) {
        Iterator<State> it = iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            State state = it.next();
            if (state.equals(START_STATE) && first_state != null) {
                sb.append(pad(first_state, state, current, replacement));
                continue;
            }
            if (state.equals(goalState()) && last_state != null) {
                sb.append(pad(last_state, state, current, replacement));
                continue;
            }
            sb.append(pad(value, state, current, replacement));
        }
        return sb.toString();
    }
    
    private String rowStates() {
        Iterator<State> it = iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            State state = it.next();
            if (state.equals(START_STATE)) {
                sb.append(pad(String.format("%s", state.getN())));
                continue;
            }
            if (state.equals(goalState())) {
                sb.append(pad("-> G"));
                continue;
            }
            sb.append(pad(String.format("-> %s", state.getN())));
        }
        return sb.toString();
    }
    
    private String pad(String value) {
        return pad(value, null, null, null);
    }
    
    private String pad(String value, State state, State current, String replacement) {
        if (current == null) {
            return actualPad(value);
        }
        return actualPad(state.equals(current) ? replacement : value);
    }
    
    private String actualPad(String value) {
        // From https://www.baeldung.com/java-pad-string
        return String.format("%1$" + PRINT_PAD + "s", value);
    }
}

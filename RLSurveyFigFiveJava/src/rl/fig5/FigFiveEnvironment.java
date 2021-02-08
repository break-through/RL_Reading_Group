package rl.fig5;

import rl.env.ConcreteReward;
import rl.env.IEnvironmentWithGoal;
import rl.env.IStepResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

final public class FigFiveEnvironment implements IEnvironmentWithGoal<IState, IAction>, Iterable<IState> {
    final private int n;
    final private IEnvStats stats;
    
    public FigFiveEnvironment(int n) {
        this.n = n;
        this.stats = new ConcreteEnvStats(n);
    }
    
    public IEnvStats getStats() {
        return this.stats;
    }
    
    @Override
    public IState getStartState() {
        return START_STATE;
    }
    
    @Override
    public List<IAction> getActionsForState(IState state) {
        ArrayList<IAction> actions = new ArrayList<>();
        if (state.getN() <= n) {
            actions.add(FORWARD);
        }
        if (state.getN() > 1 && state.getN() <= n) {
            actions.add(BACK);
        }
        return actions;
    }
    
    @Override
    public IStepResult<IState> step(IState state, IAction action) {
        raiseIfInvalidState(state);
        raiseIfInvalidActionForState(state, action);
        IState nextState = action.equals(FORWARD)
            ? new ConcreteState(state.getN() + 1)
            : START_STATE;
        return new FigFiveStepResult(
            new ConcreteReward(nextState.equals(goalState()) ? 1 : 0),
            nextState
        );
    }
    
    @Override
    public boolean isGoalState(IState state) {
        return state.equals(goalState());
    }
    
    public String toStringInState(IState state) {
        raiseIfInvalidState(state);
        return toStringInStateImpl(state);
    }
    
    @Override
    public String toString() {
        return toStringImpl();
    }
    
    @Override
    public Iterator<IState> iterator() {
        final int max = n;
        return new Iterator<>() {
            private final int n = max;
            private int next = 1;
            @Override
            public boolean hasNext() {
                return next <= n + 1;
            }
    
            @Override
            public IState next() {
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
    final private static IAction FORWARD = new ConcreteAction("forward");
    final private static IAction BACK = new ConcreteAction("back");
    final private static IState START_STATE = new ConcreteState(1);
    final private static String NEWLINE = "\n";
    final private static int PRINT_PAD = 5;
    
    private IState goalState() {
        return new ConcreteState(n + 1);
    }
    
    private void raiseIfInvalidState(IState state) {
        if (state.getN() < START_N || state.getN() > goalState().getN()) {
            throw new IllegalArgumentException(String.format(
                "Invalid state received (%s)",
                state
            ));
        }
    }
    
    private void raiseIfInvalidActionForState(IState state, IAction action) {
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
    
    private String toStringInStateImpl(IState state) {
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
    
    private String indicationRow(IState current) {
        return row(" ", current, "^", null, null);
    }
    
    private String row(
        String value,
        IState current,
        String replacement,
        String first_state,
        String last_state
    ) {
        Iterator<IState> it = iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            IState state = it.next();
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
        Iterator<IState> it = iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            IState state = it.next();
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
    
    private String pad(String value, IState state, IState current, String replacement) {
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

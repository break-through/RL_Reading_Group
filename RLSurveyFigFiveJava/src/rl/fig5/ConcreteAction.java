package rl.fig5;

public class ConcreteAction extends IAction {
    private final String name;
    
    public ConcreteAction(String name) {
        this.name = name;
    }
    
    @Override
    String getName() {
        return name;
    }
}

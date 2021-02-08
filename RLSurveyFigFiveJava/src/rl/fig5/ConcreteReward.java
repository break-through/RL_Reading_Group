package rl.fig5;

public class ConcreteReward extends IReward {
    private final int reward;
    public ConcreteReward(int reward) {
        this.reward = reward;
    }
    @Override
    int reward() {
        return reward;
    }
}

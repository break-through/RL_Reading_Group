package rl.fig5;

public class ConcreteReward extends Reward {
    private final int reward;
    public ConcreteReward(int reward) {
        this.reward = reward;
    }
    @Override
    int reward() {
        return reward;
    }
}

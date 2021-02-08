package rl.fig5;

import rl.env.IReward;

public class ConcreteReward extends IReward {
    private final int reward;
    public ConcreteReward(int reward) {
        this.reward = reward;
    }
    @Override
    public int reward() {
        return reward;
    }
}

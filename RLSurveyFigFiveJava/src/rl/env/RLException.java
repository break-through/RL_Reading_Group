package rl.env;

public class RLException extends Exception {

    public RLException(String errMessage, Throwable err) {
        super("You have tarnished this epically designed reinforcement learning code." +
                "How dare you! You need some milk (cue meme music). " + errMessage, err);
    }
}

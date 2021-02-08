import functools
import random
from abc import abstractmethod
from typing import Dict, List, NamedTuple, Optional, Tuple


MAX_STATE = 25

TState = int

class Action(NamedTuple):
    # All these variables are private. You cannot access them.
    # Just treat them as an "abstract" action
    src: int
    dst: int
    reward: int


class EnvStats(NamedTuple):
    n: int
    current: int
    n_squared: int
    two_exp_n: int

class Stats(NamedTuple):
    steps: int
    backups: int

class Q:
    def __init__(self, gamma: float) -> None:
        self._gamma = gamma
        self._Q: Dict[Tuple[TState, Action], float] = {}
        self._n: Optional[int] = None

    def value(self, s: TState, a: Action) -> float:
        return self._Q[s, a]

    # Will be set by environment at initialization
    # DO NOT USE / CHANGE
    def set_n(self, n: int) -> None:
        self._n = n

    # Environment calls this at initialization.
    # Override this method to initialize the Q values.
    # This one does so randomly. See link in associated file 'DynaQueue.md'.
    # Promise: Environment will give all action pairs
    def set_all_actions(self, actions: Dict[TState, List[Action]]) -> None:
        for state in actions.keys():
            for action in actions[state]:
                self._Q[state, action] = random.random()
    

    # OVERRIDE MUST IMPLEMENT
    @abstractmethod
    def update(
        self,
        s: TState,
        a: Action,
        s_prime: TState,
        reward: int,
    ) -> int:
        """ Perform backups and return how many backups were performed """
        pass

    # Env will run updates using this
    # DO NOT USE / CHANGE
    def actual_update(
        self,
        s: TState,
        a: Action,
        s_prime: TState,
        reward: int,
    ) -> int:
        assert self._n is not None
        backups = self.update(s, a, s_prime, reward)
        return backups


class Env:
    def __init__(self, n: int, q: Q) -> None:
        assert n <= MAX_STATE, f"maximum state value is {MAX_STATE}"
        self._n = n
        self._state = 1
        self._steps = 0
        self._backups = 0

        # map from state to: map from action to # times its taken
        # for us, s' is embedded in the action
        self.transitions: Dict[TState, Dict[Action, int]] = {}
        self.rewards: Dict[Action, int] = {}
        self._Q = q
        self._initialize()

    def _initialize(self) -> None:
        self._Q.set_n(self._n)
        actions = {s: self.actions_for_state(s) for s in self}
        actions[self._n + 1] = []
        self._Q.set_all_actions(actions)

    def get_n(self) -> int:
        return self._n

    def state(self) -> int:
        return self._state

    def actions(self) -> List[Action]:
        return self.actions_for_state(self._state)

    def actions_for_state(self, state: int) -> List[Action]:
        actions = []
        if state < self._n:
            actions.append(Action(state, state + 1, 0))
        if state == self._n:
            actions.append(Action(state, state + 1, 1))
        if state > 1 and state <= self._n:
            actions.append(Action(state, 0, 0))
        return actions

    def is_goal_state(self, state: int) -> bool:
        return state == self._n

    def step(self, action: Action) -> Tuple[TState, int]:
        assert action in self.actions()
        self._steps += 1
        self._state = action.dst
        self.transitions.setdefault(action.src, {action: 0})
        self.transitions[action._src][action] += 1
        self.rewards[action] = action.reward
        self._backups += self._Q.actual_update(
            action.src,
            action,
            action.dst,
            action.reward,
        )
        return (self._state, action.reward)

    def get_transitions(self, state: TState) -> Dict[Action, int]:
        return self.transitions.get(state, {})

    def get_reward(self, action: Action) -> Optional[int]:
        return self.rewards.get(action, None)

    @functools.cached_property
    def env_stats(self) -> EnvStats:
        return EnvStats(
            self._n,
            self._state,
            self._n ** 2,
            2 ** self._n,
        )

    def stats(self) -> Stats:
        return Stats(
            self._steps,
            self._backups,
        )

    def __iter__(self) -> str:
        for state in range(1, self._n + 1):
            yield state

    def __repr__(self) -> str:
        return str(self)

    def __str__(self) -> str:
        lines = []
        lines.append(" ".join(
            Env._pad("<--", s, "") for s in self
        ))
        lines.append(" ".join(
            Env._pad("|", s, "") for s in self
        ))
        lines.append(" ".join(
            v for v in (
                [Env._pad(f"-> {s}", s, f"{s}") for s in self] +
                [Env._pad("-> G")]
            )
        ))
        lines.append(" ".join(
            [Env._pad(self._(" ", "^", s)) for s in self] +
            [Env._pad(self._(" ", "^", self._n + 1))]
        ))
        return "\n".join(lines)

    def _(self, no_state: str, yes_state: str, state: int) -> str:
        return yes_state if state == self._state else no_state

    @staticmethod
    def _pad(
            value: str,
            state: Optional[int] = None,
            replacement: Optional[str] = None,
        ) -> str:
        padding = 4
        if (state is None or state == 1) and (replacement is not None):
            return replacement.rjust(padding, " ")
        return value.rjust(padding, " ")


# If anyone wants, implement the CertaintyEquivalentQ
# it's dumb but writing it requires writing an algorithm
# to solve linear equations. ain't anybody got time for
# that. *cue remix* 

class DynaQ(Q):
    def __init__(self, gamma: float, k: int = 1) -> None:
        super().__init__(gamma)
        assert k > 0, "k must be positive"
        self._k = k

    def update(
        self,
        s: TState,
        a: Action,
        s_prime: TState,
        reward: int,
    ) -> int:
        backups = self.backup(s, a, s_prime, reward)
        # TODO: pick self._k random states and perform backups on them
        raise NotImplementedError
        return backups

    def backup(
        self,
        s: TState,
        a: Action,
        s_prime: TState,
        reward: int,
    ) -> int:
        transition_summation = 0
        # TODO: figure out how to do this transition summation
        raise NotImplementedError
        self._Q[s, a] = reward + gamma * transition_summation
        return 1

class PrioritizedSweepingQ(Q):
    def __init__(self, gamma: float) -> None:
        super().__init__(gamma)

    def update(
        self,
        s: TState,
        a: Action,
        s_prime: TState,
        reward: int,
    ) -> int:
        # TODO: implement this
        raise NotImplementedError



if __name__ == "__main__":
    dyna = DynaQ(5)
    env = Env(5, dyna)
    print(env)
    print(env.env_stats)
    print(env.stats())

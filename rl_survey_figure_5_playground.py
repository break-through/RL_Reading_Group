import functools
import random
from abc import abstractmethod
from typing import Dict, List, NamedTuple, Optional, Tuple


MAX_STATE = 25

TState = int #modified to be 'State' class.

"""
For QDyna, we require priority to be used w/ max heap and predecessors;
by default, priority can be '0' for truly random case (reg Dyna) and
predecessors can be an empty list
"""
class State(NamedTuple):
    state: int
    priority: Optional[int]
    #technically a Tuple of type 'State' but can't do that directly.
    predecessors: Optional[Tuple] #hashable; treat as list though
    value: Optional[int]

class Action(NamedTuple):
    # All these variables are private. You cannot access them.
    # Just treat them as an "abstract" action
    src: State
    dst: State
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
        self._Q: Dict[Tuple[State, Action], float] = {}
        self._n: Optional[int] = None

    def value(self, s: State, a: Action) -> float:
        return self._Q[s, a]

    # Will be set by environment at initialization
    # DO NOT USE / CHANGE
    def set_n(self, n: int) -> None:
        self._n = n

    # Environment calls this at initialization.
    # Override this method to initialize the Q values.
    # This one does so randomly. See link in associated file 'DynaQueue.md'.
    # Promise: Environment will give all action pairs
    def set_all_actions(self, actions: Dict[State, List[Action]]) -> None:
        for state in actions.keys():
            for action in actions[state]:
                self._Q[state, action] = random.random()
    

    # OVERRIDE MUST IMPLEMENT
    @abstractmethod
    def update(
        self,
        s: State,
        a: Action,
        s_prime: State,
        reward: int,
        states: Dict[int, State],
        transitions: Dict[Action, int],
    ) -> int:
        """ Perform backups and return how many backups were performed """
        pass

    # Env will run updates using this
    # DO NOT USE / CHANGE
    def actual_update(
        self,
        s: State,
        a: Action,
        s_prime: State,
        reward: int,
        states: Dict[int, State],
        transitions: Dict[Action, int],
    ) -> int:
        assert self._n is not None
        backups = self.update(s, a, s_prime, reward, states, transitions)
        return backups


class Env:
    def __init__(self, n: int, q: Q) -> None:
        assert n <= MAX_STATE, f"maximum state value is {MAX_STATE}"
        self._n = n
        self.all_states = self._initstates(self._n)
        self._state = self.all_states[1]
        self._steps = 0
        self._backups = 0

        # map from state to: map from action to # times its taken
        # for us, s' is embedded in the action
        self.transitions: Dict[State, Dict[Action, int]] = {}
        self.rewards: Dict[Action, int] = {}
        self._Q = q
        self._initialize()

    def _initstates(self, n: int) -> Dict[int, State]:
        states = {}
        for s in range(1,n+1):
            states[s] = State(s, 0, (), random.random())
        return states
    
    def _initialize(self) -> None:
        self._Q.set_n(self._n)
        actions = {}
        for s in self:
            state = self.all_states[s]
            actions[state] =  self.actions_for_state(state)
        #? where is 's' coming from? 
        #actions[self.all_states[s+1]] = [] Do we need this?
        self._Q.set_all_actions(actions)

    def get_n(self) -> int:
        return self._n

    def state(self) -> State:
        return self._state

    def get_states(self) -> Dict[int, State]:
        return self.all_states

    def actions(self) -> List[Action]:
        return self.actions_for_state(self._state)

    def actions_for_state(self, state: State) -> List[Action]:
        #boundaries? instant means from what we left right?
        #Thus self.n is the last state? Also is the first and third if
        #statement redundant?
        actions = []
        if state.state < self._n-1:
            actions.append(Action(state, self.all_states[state.state + 1], 0))
        if state.state == self._n-1:
            actions.append(Action(state, self.all_states[state.state + 1], 1))
        if state.state > 1 and state.state < self._n-1:
            actions.append(Action(state, 0, 0))
        return actions

    def is_goal_state(self, state: State) -> bool:
        return state.state == self._n

    def step(self, action: Action) -> Tuple[State, int]:
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
            self.get_transitions(action.src),
            self.all_states, #car insurance
        )
        return (self._state, action.reward)

    def get_transitions(self, state: State) -> Dict[Action, int]:
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

    def _(self, no_state: str, yes_state: str, state: State) -> str:
        return yes_state if state == self._state else no_state

    @staticmethod
    def _pad(
            value: str,
            state: Optional[State] = None,
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

    """Only the 'kth' highest"""
    def getPriorityOrder(self, sts: Dict[int, State]) -> List[State]:
        states = sts.values()
        states.sort(key = lambda s : s.priority, reverse = True)
        return states[:self._k]

    def update(
        self,
        s: State,
        a: Action,
        s_prime: State,
        reward: int,
        states: Dict[int, State],
        transitions: Dict[Action, int],
    ) -> int:
    
        maxQ = self.getPriorityOrder(sts)
        tot_backups = 0
        
        for s in maxQ:   
            V_old = s.value
            
            backups = self.backup(s, a, s_prime, reward, transitions)
            tot_backups += backups

            V_new = s.value #has auto modified
            s.priority = 0
            delta = abs(V_old-V_new)
            
            pre = s.predecessors
            
            #for s in pre: #update all other predecessor priorities
            #then reupdate priority queue I think TODO figure this out
                

        return tot_backups

    def backup(
        self,
        s: State,
        a: Action,
        s_prime: State, #TODO remove and just use 'transitions' below
        reward: int,
        transitions: Dict[Action, int],
    ) -> int:
        max_v_a = [float('-inf'), None]
        
        for a in transitions.key():
            v = 0
            transition_summation = 0
            # TODO: figure out how to do this transition summation
            #should this should be recursive? Or do we assume
            #via DP that other 'iterations' update this?
            #This should be recursive for high backup numbers...
            for s_p in transitions.key():
                Vspr = s_p.dst.value
                transition_summation += Vspr*1 
                #TODO get transition probability
            v = a.reward + self.gamma * transition_summation
            if v > max_v_a[0]:
                max_v_a[0] = v
                max_v_a[1] = a #argmax

        s.value = max_v_a[0]
        return 1

class PrioritizedSweepingQ(Q):
    def __init__(self, gamma: float) -> None:
        super().__init__(gamma)

    def update(
        self,
        s: State,
        a: Action,
        s_prime: State,
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

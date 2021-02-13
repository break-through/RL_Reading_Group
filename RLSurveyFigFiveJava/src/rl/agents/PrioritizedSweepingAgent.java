package rl.agents;

import rl.env.IEnvironment;
import rl.env.IExperience;
import rl.env.RLException;
import util.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Based on 5.3 of Kaelbling's Survey during discussion with the RL Reading Group
 * The algorithm is implemented *EXACTLY AS I PERCEVIED* from the Survey. This means it may have
 * inconsistencies with interpretation - please *READ THE COMMENTS CAREFULLY* esp regarding discussion points (feel
 * free to skip other comments though but be on the lookout for where I write 'discuss' - this indicates I
 * wish to *DICUSS* my interpretation at those points).
 */
public final class PrioritizedSweepingAgent<S,A> extends IQLearningAgent<S,A> {
    /**
     *
     Note that while IQLearningAgent contextually uses Q Learning in it's name, it can be applied to value
     iteration, and given it's usage of alpha, gamma, made the most sense according to the implementor of these
     interfaces (who I hear is a millionaire rapper, movie producer, actor, nobel laureate, photographer and has
     the legit time management skills to do this while working a full time job - what a legendary G)).

     A deep prayer:

     Professor Max Goldman, I apologize deeply for ignoring my Abstraction Function, and yes, a check rep would be
     the minimal amount of work I should do but I'm too lazy to do that too. Threadsafety isn't a concern here but
     for memories sake, considering it truly isn't neeeded here at *all* I'll pop it in.

     Threadsafe Argument() - This code is threadsafe.

     You know, that's about as much effort I put into the threadsafe arguments during class too when the code *was*
     actually at risk of thread issues. Anyways, Max, I hope you do well in life, and I apologize for not using your
     skills, but deeply admire your energy and the time you taught us to be bolder than functional programmers. Maybe
     someday I'll learn from this and heed your teachings - the little that stuck with me has really helped.

    */

    private static final Double DEFAULT_PRIORITY = 0.0;
    private final int k;

    private final PriorityQueue<RightComPair<S,Double>> maxPriority;
    private final PriorityQueue<RightComPair<S, Double>> minPriority;
    private final Map<S, RightComPair<S,Double>> easyFind;

    public PrioritizedSweepingAgent(IEnvironment<S, A> environment, double alpha, double gamma, int k) {
        super(environment, alpha, gamma);
        this.maxPriority = new PriorityQueue<>();
        this.minPriority = new PriorityQueue<>(Collections.reverseOrder());
        this.easyFind = new HashMap<>();
        this.k = k;
    }

    public int getK(){
        return this.k;
    }

    @Override
    public A policy() {
        try {
            return bestActionAtState(getCurrentState());
        } catch (RLException e) {
            return new EqualDistribution<>(availableActions()).sample();
        }
    }

    @Override
    public void learn() {
        priorityK(); //to initialize and add new states as we see them

        int iteration = 0;

        S cur = this.getCurrentState();

        RightComPair<S, Double> curPair = this.easyFind.get(cur);
        this.minPriority.remove(curPair);
        this.maxPriority.remove(curPair);
        this.easyFind.remove(curPair.getLeft());

        RightComPair<S, Double> improvedCurPair = RightComPair.make(cur, Double.POSITIVE_INFINITY);

        this.maxPriority.add(improvedCurPair);
        this.minPriority.add(improvedCurPair);
        this.easyFind.put(cur, improvedCurPair); //a little dance to get the current state always high up
        //can discuss if this is not proper - see coments below

        while (iteration < this.k+1) { //discuss here... (is it until 'k' heap is emptied? Or just k iterations?)
            //for now I've done 'k' iterations (plus the current state; see comment below)

            //discuss - the first state is always the present state, but Kaelbling doesn't explicitely say so for PS

            curPair = this.maxPriority.poll();
            this.easyFind.remove(curPair.getLeft());
            S curState = curPair.getLeft(); //comparator ensures maximal
            double v_old = value(curState);

            A a = null; //normally we'd throw an error below so this isn't a issue - TODO to change.
            try {
                a = bestActionAtState(curState);
            } catch (RLException e) {
                e.printStackTrace();
            }

            A finalA = a;

            S s_prime = this.history()
                    .stream()
                    .filter(q -> (q.getState().equals(curState) && q.getAction().equals(finalA)))
                    .map(c -> c.getNextState())
                    .findFirst()
                    .get(); //not sure but the paper describes s' - I don't use it though but can discuss.

            double V_old_sprime = value(s_prime); //disucss - is the value for s_prime known in the abstract class
            // implementation if we have just seen it? regardless, also not used; here in case interpretation of
            // Kaelbling is different during discussion

            fullBackup(curState, a);

            try { //since no RLException allowed in current implementation
                fullBackup(curState, bestActionAtState(curState));
            } catch (RLException e) {
                e.printStackTrace();
            }

            double v_new = value(curState); //backup has modified it
            RightComPair<S, Double> iteratedCurPair = RightComPair.make(curState, DEFAULT_PRIORITY);

            //re-initializing modified pairs
            this.maxPriority.add(iteratedCurPair);
            this.minPriority.add(iteratedCurPair);
            this.easyFind.put(curState, iteratedCurPair);

            double v_change = Math.abs(v_old - v_new);

            Set<S> predecessorStates = this.getObservedStateActionPairs() //builder pattern for streams
                    .stream()
                    .map(c -> c.getLeft())
                    .collect(Collectors.toSet());


            for(S s: predecessorStates){
                S s_pr_here = curState;
                //ah.. I see Kaelblings notation was confusing to interpret but she means s_above is s_prime, and these
                //s are the 's'. *Ignore* the s' concepts above (though we can still discuss them as this may be
                // still wrongly interpreted).
                RightComPair<S, Double> pair = this.easyFind.get(s); //gaurenteed to be there since we've seen it
                List<A> acs = this.getEnvironment().getActionsForState(pair.getLeft());
                for(A aa : acs) {
                    final IDistribution<S> dist = getTransitionDistribution(s, aa);
                    double cur_t_prob = dist.prob(s_pr_here);
                    if (cur_t_prob != 0){
                        double new_priority = v_change*cur_t_prob;
                        if (new_priority > pair.getRight()) {
                            this.maxPriority.remove(pair);
                            this.minPriority.remove(pair);
                            this.easyFind.remove(pair.getLeft());
                            RightComPair<S, Double> updatedPair = RightComPair.make(pair.getLeft(), new_priority);
                            //priority updated and now re-initialize the heap/priority queues
                            this.maxPriority.add(updatedPair);
                            this.minPriority.add(updatedPair);
                            this.easyFind.put(pair.getLeft(), updatedPair); //DRY probably but we meme on to another
                            //day. Max Goldman I'm sorry. You were super energectic; wish I could have what you have.

                        }
                    }
                }
            }

            iteration++;


        }
    }

    @Override
    double value(S s) {
        return maxQAtState(s);
    }

    /**
     * This adds new states to our priority queue if it doesn't have them yet and caps the pQ to be at most k
     * elements.
     */
    private void priorityK() {
        S curState = this.getCurrentState();

        if(!containsInternal(this.maxPriority, curState)){
            RightComPair<S, Double> thisPair = RightComPair.make(curState, DEFAULT_PRIORITY);
            this.easyFind.put(curState, thisPair); //add *all* states always.
            if(this.maxPriority.size() <= this.k) {
                this.maxPriority.add(thisPair); //always holds max priority
                this.minPriority.add(thisPair);
            }
            else {
                RightComPair<S, Double> minPair = this.minPriority.poll(); //likely no negative vals but just in case
                if (minPair.getRight() < DEFAULT_PRIORITY){
                    this.maxPriority.remove(minPair);
                    this.maxPriority.add(thisPair);
                    this.minPriority.add(thisPair);
                }
                //otherwise do nothing; no need to add in to the heaps since it's smaller than or equal to what's
                //already in the heap
            }
        }


    }

    private boolean containsInternal(PriorityQueue<RightComPair<S,Double>> pQ, S s){
        for(RightComPair<S,Double> p : pQ){
            if (p.getLeft() == s){
                return true;
            }
        }
        return false;
    }

}

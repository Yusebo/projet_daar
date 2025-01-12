package RegEx;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class DFABuilder {
	public static DFA buildDFAFromNFA(NFA nfa) {
	    DFA dfa = new DFA();
	    Map<Set<StateA>, StateA> dfaStateAs = new HashMap<>();
	
	    // Create the initial StateA of the DFA
	    Set<StateA> initialNFAStateAs = epsilonClosure(nfa.getInitialStateA());
	
	    StateA initialDFAStateA = new StateA(0);
	    dfa.setInitialStateA(initialDFAStateA);
	    dfaStateAs.put(initialNFAStateAs, initialDFAStateA);
	   
	    // Use a queue to perform a breadth-first search of the NFA
	    Queue<Set<StateA>> worklist = new LinkedList<>();
	    worklist.add(initialNFAStateAs);
	
	    while (!worklist.isEmpty()) {
	        Set<StateA> currentNFAStateAs = worklist.poll();
	        StateA currentDFAStateA = dfaStateAs.get(currentNFAStateAs);
	        
	        // Check if the current DFA StateA is a final StateA
	        if (currentNFAStateAs.stream().anyMatch(StateA -> StateA.isFinal())) {
	            dfa.addFinalStateA(currentDFAStateA);
	        }
	        // Compute the set of NFA StateAs that can be reached from the current DFA StateA by following each symbol
	        for (char c : nfa.getAlphabet()) {
	            Set<StateA> nextNFAStateAs =getNextSubStateAs(currentNFAStateAs, c);
	            // If this set of NFA StateAs is not already a DFA StateA, create a new DFA StateA for it
	            if(!nextNFAStateAs.isEmpty()) {
	              if (!dfaStateAs.containsKey(nextNFAStateAs)) {
	                  StateA nextDFAStateA = new StateA(0);
	                  dfa.addStateA(nextDFAStateA);
	                  dfaStateAs.put(nextNFAStateAs, nextDFAStateA);
	                  worklist.add(nextNFAStateAs);
	              }
	              // Add a transition from the current DFA StateA to the next DFA StateA on the current symbol
	              StateA nextDFAStateA = dfaStateAs.get(nextNFAStateAs);
	              currentDFAStateA.addTransition(c, nextDFAStateA);
	              }
	        }
	    }
	
	    return dfa;
	}
	
	// Compute the set of NFA StateAs that can be reached from a given NFA StateA by following epsilon transitions
	private static Set<StateA> epsilonClosure(StateA nfaStateA) {
	    Set<StateA> closure = new HashSet<>();
	    closure.add(nfaStateA);
	    Stack<StateA> stack = new Stack<>();
	    stack.push(nfaStateA);
	    while (!stack.isEmpty()) {
	        StateA currentStateA = stack.pop();
	        for (StateA nextStateA : currentStateA.getEpsilonTransitions()) {
	            if (!closure.contains(nextStateA)) {
	                closure.add(nextStateA);
	                stack.push(nextStateA);
	            }
	        }
	    }
	
	    return closure;
	}
	private static Set<StateA> getNextSubStateAs(Set<StateA> StateAs, Character c) {
	  Set<StateA> subset = new HashSet<>();
	  for (StateA StateA : StateAs) {
	     StateA new_StateA = StateA.getTransitions().get(c);
	     if(new_StateA != null) {
	       subset.addAll(epsilonClosure(new_StateA));
	     }
	     
	  }
	  return subset;
	}
}

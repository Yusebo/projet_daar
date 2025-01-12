package RegEx;
import java.util.HashSet;
import java.util.Set;

public class NFA {
  private StateA initialStateA;
  private StateA finalStateA;
  private Set<StateA> StateAs;
  private int StateAIdCounter;

public NFA() {
    this.StateAs = new HashSet<>();
}

public void setInitialStateA(StateA StateA) {
    this.initialStateA = StateA;
    StateA.setId(StateAIdCounter++);
    this.StateAs.add(StateA);
}

public void setFinalStateA(StateA StateA) {
    this.finalStateA = StateA;
    StateA.setId(StateAIdCounter++);
    StateA.setFinal(true);
    this.StateAs.add(StateA);
}



@Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("NFA:\n");
    sb.append("Initial StateA: ").append(initialStateA.id).append("\n");
    sb.append("Final StateA: ").append(finalStateA.id).append("\n");

    for (StateA StateA : StateAs) {
        sb.append("StateA ").append(StateA.id);
        if (StateA.isFinal()) {
            sb.append(" (Final)");
        }
        sb.append(":\n");
        for (Character symbol : StateA.getTransitions().keySet()) {
           StateA targetStateA = StateA.getTransitions().get(symbol);
           sb.append("  - Transition on '").append(symbol).append("' to StateA ")
              .append(targetStateA.id).append("\n");
           
        }
        for (StateA targetStateA : StateA.epsilontransitions) {
          sb.append("  - Transition on epsilon").append(" to StateA ")
          .append(targetStateA.id).append("\n");
        }
    }
    return sb.toString();
}

public StateA getFinalStateA() {
  return finalStateA;
}
public StateA getInitialStateA() {
  return initialStateA;
}
public Set<StateA> getStateAs() {
  return StateAs;
}

public void addStateA(StateA StateA) {
  StateA.setId(StateAIdCounter++);
  this.StateAs.add(StateA);
}

public void addAllStateAs(Set<StateA> StateAs) {
  for (StateA StateA : StateAs) {
      StateA.setId(StateAIdCounter++); 
      this.StateAs.add(StateA); 
  }
}
public void allisnotFinal() {
  for (StateA StateA : StateAs) {
      StateA.setFinal(false);
  }
}

public Set<Character> getAlphabet() {
  Set<Character> alphabet = new HashSet<>();
  for (StateA StateA : StateAs) {
      alphabet.addAll(StateA.getTransitions().keySet());
  }
  return alphabet;
}
}
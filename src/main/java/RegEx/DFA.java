package RegEx;
import java.util.HashSet;
import java.util.Set;

public class DFA {
  private StateA initialStateA;
  private Set<StateA> StateAs;
  private Set<StateA> finalStateAs;
  private int StateAIdCounter;
  
  public DFA() {
      this.StateAs = new HashSet<>();
      this.finalStateAs = new HashSet<>();
      this.StateAIdCounter = 0;
  }
  
  public void setInitialStateA(StateA StateA) {
      this.initialStateA = StateA;
      StateA.setId(StateAIdCounter++);
      this.StateAs.add(StateA);
  }
  
  public void addStateA(StateA StateA) {
      StateA.setId(StateAIdCounter++);
      this.StateAs.add(StateA);
  }
  
  public void addFinalStateA(StateA StateA) {
      StateA.isFinal = true;
      this.finalStateAs.add(StateA);
  }
  
  
  @Override
  public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("DFA:\n");
      sb.append("Initial StateA: ").append(initialStateA.id).append("\n");
      sb.append("Final StateAs: ");
      for (StateA StateA : finalStateAs) {
          sb.append(StateA.id).append(" ");
      }
      sb.append("\n");
  
      for (StateA StateA : StateAs) {
          sb.append("StateA ").append(StateA.id);
          if (finalStateAs.contains(StateA)) {
              sb.append(" (Final)");
          }
          sb.append(":\n");
          for (Character symbol : StateA.transitions.keySet()) {
              StateA targetStateA = StateA.transitions.get(symbol);
              sb.append("  - Transition on '").append(symbol).append("' to StateA ")
                .append(targetStateA.id).append("\n");
          }
      }
      return sb.toString();
  }
  public Set<Character> getAlphabet() {
    Set<Character> alphabet = new HashSet<>();
    for (StateA StateA : StateAs) {
        alphabet.addAll(StateA.getTransitions().keySet());
    }
    return alphabet;
  }
  
  public StateA getInitialStateA() {
      return initialStateA;
  }
  
  public Set<StateA> getStateAs() {
      return StateAs;
  }
  
  public Set<StateA> getFinalStateAs() {
      return finalStateAs;
  }
}
package RegEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;


public class StateA {
  int id;
  private boolean isFinal;
  HashMap<Character, StateA> transitions;
  Set<StateA> epsilontransitions;

  public StateA(int id) {
    this(id, false);
  }
  public StateA(int id, boolean isFinal) {
    this.id = id;
    this.setFinal(isFinal);
    this.transitions = new HashMap<>();
    this.epsilontransitions = new HashSet<>();
}

  public void addTransition(char symbol, StateA StateA) {
    transitions.put(symbol, StateA);
}

  // Ajoute une transition epsilon (chaîne "epsilon")
  public void addEpsilonTransition(StateA StateA) {
      epsilontransitions.add(StateA);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("StateA{" +
              "id=" + id +
              ", isFinal=" + isFinal() +
              ", transitions = " );
    for (var entry : this.transitions.entrySet()) {
      sb.append(entry.getKey()+ " " + entry.getValue() + " ");
    }
    sb.append("\n, epsilontransition = ");
    for (var StateA : this.epsilontransitions) {
      sb.append(StateA.id + " ");
    }
    sb.append('}');
    return  sb.toString();     
  }
  public void setId(int i) {
    this.id = i;
    
  }
  public Set<StateA> getEpsilonTransitions() {
    return epsilontransitions;
  }
  public HashMap<Character, StateA> getTransitions() {
    return transitions;
  }
  public StateA getTransition(Character input) {
    if(transitions.get(input) != null)
      return transitions.get(input);
    return transitions.get('.');
  }
public boolean isFinal() {
	return isFinal;
}
public void setFinal(boolean isFinal) {
	this.isFinal = isFinal;
}
}





package RegEx;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DFAMinimizer {

public static DFA minimizeDFA(DFA dfa) {
    // Étape 1 : Partitionner les états en états finaux et non-finaux
    Set<StateA> finalStateAs = dfa.getFinalStateAs();
    Set<StateA> nonFinalStateAs = new HashSet<>(dfa.getStateAs());
    nonFinalStateAs.removeAll(finalStateAs);

    // Liste des partitions (états finaux et non finaux)
    List<Set<StateA>> partitions = new ArrayList<>();
    if (!finalStateAs.isEmpty()) partitions.add(finalStateAs);
    if (!nonFinalStateAs.isEmpty()) partitions.add(nonFinalStateAs);

    boolean partitionsChanged;
    do {
        partitionsChanged = false;
        List<Set<StateA>> newPartitions = new ArrayList<>();

        for (Set<StateA> partition : partitions) {
            // Partitionner encore en fonction des transitions sur l'alphabet
            Map<Map<Character, Set<StateA>>, Set<StateA>> transitionToStateAsMap = new HashMap<>();
            
            for (StateA StateA : partition) {
                // Regrouper les états en fonction de leur transition
                Map<Character, Set<StateA>> transitionMap = new HashMap<>();
                for (Character symbol : dfa.getAlphabet()) {
                    StateA targetStateA = StateA.transitions.get(symbol);
                    if (targetStateA != null) {
                        Set<StateA> targetPartition = findPartition(targetStateA, partitions);
                        transitionMap.put(symbol, targetPartition);
                    }
                }

                // Regrouper les états ayant les mêmes transitions
                transitionToStateAsMap.computeIfAbsent(transitionMap, k -> new HashSet<>()).add(StateA);
            }

            // Ajouter les nouvelles partitions
            newPartitions.addAll(transitionToStateAsMap.values());
            if (transitionToStateAsMap.size() > 1) {
                partitionsChanged = true;
            }
        }

        partitions = newPartitions;
    } while (partitionsChanged);

    // Étape 3 : Construire le DFA minimal à partir des partitions
    Map<Set<StateA>, StateA> newStateAsMap = new HashMap<>();
    DFA minimizedDFA = new DFA();

    for (Set<StateA> partition : partitions) {
        StateA newStateA = new StateA(minimizedDFA.getStateAs().size());
        minimizedDFA.addStateA(newStateA);
        if (partition.stream().anyMatch(finalStateAs::contains)) {
            minimizedDFA.addFinalStateA(newStateA);
        }
        if (partition.contains(dfa.getInitialStateA())) {
            minimizedDFA.setInitialStateA(newStateA);
        }
        newStateAsMap.put(partition, newStateA);
    }

    // Créer les transitions pour le DFA minimisé
    for (Set<StateA> partition : partitions) {
        StateA newStateA = newStateAsMap.get(partition);
        StateA representative = partition.iterator().next();
        for (Character symbol : dfa.getAlphabet()) {
            StateA targetStateA = representative.transitions.get(symbol);
            if (targetStateA != null) {
                Set<StateA> targetPartition = findPartition(targetStateA, partitions);
                StateA newTargetStateA = newStateAsMap.get(targetPartition);
                newStateA.addTransition(symbol, newTargetStateA);
            }
        }
    }

    return minimizedDFA;
}

// Trouve la partition à laquelle appartient un état donné
private static Set<StateA> findPartition(StateA StateA, List<Set<StateA>> partitions) {
    for (Set<StateA> partition : partitions) {
        if (partition.contains(StateA)) {
            return partition;
        }
    }
    return null;
}
}
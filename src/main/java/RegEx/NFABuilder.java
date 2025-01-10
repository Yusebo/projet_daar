package RegEx;
import java.util.ArrayList;

public class NFABuilder {
  public static NFA buildNFAFromRegExTree(RegExTree tree) {
      NFA NFA = new NFA();
  
      // Si le nœud est une feuille (un caractère ou un point), on crée un automate simple
      if (tree.subTrees.isEmpty()) {
          StateA initialStateA = new StateA(0);
          StateA finalStateA = new StateA(1, true);
  
          // Si le nœud est un point (.), on ajoute une transition sur n'importe quel symbole
          if (tree.root == RegEx.DOT) {
              initialStateA.addTransition('.', finalStateA);
          } else {
              initialStateA.addTransition((char) tree.root, finalStateA);
          }
  
          NFA.setInitialStateA(initialStateA);
          NFA.setFinalStateA(finalStateA);
  
          return NFA;
      }
  
      // Gestion des opérateurs
      switch (tree.root) {
          case RegEx.CONCAT:
              return handleConcat(tree.subTrees);
          case RegEx.ALTERN:
              return handleAltern(tree.subTrees);
          case RegEx.ETOILE:
              return handleEtoile(tree.subTrees.get(0));
          default:
              throw new IllegalArgumentException("Unknown operator in RegExTree.");
      }
  }
  
  // Gérer la concaténation
  private static NFA handleConcat(ArrayList<RegExTree> subTrees) {
      NFA leftNFA = buildNFAFromRegExTree(subTrees.get(0));
      NFA rightNFA = buildNFAFromRegExTree(subTrees.get(1));
      
      StateA init =new StateA(0);
      StateA fin = new StateA(0);
      // Connecter l'état final de l'automate gauche à l'état initial de l'automate droit avec une epsilon-transition
      leftNFA.getFinalStateA().addEpsilonTransition(rightNFA.getInitialStateA());
      init.addEpsilonTransition(leftNFA.getInitialStateA());
      rightNFA.getFinalStateA().addEpsilonTransition(fin);
      
      leftNFA.allisnotFinal();
      rightNFA.allisnotFinal();
      
      // Créer un nouvel automate qui combine les deux automates
      NFA combinedNFA = new NFA();
      combinedNFA.setInitialStateA(init);
      combinedNFA.addAllStateAs(leftNFA.getStateAs());
      combinedNFA.addAllStateAs(rightNFA.getStateAs());
      combinedNFA.setFinalStateA(fin);
      return combinedNFA;
  }
  
  // Gérer l'alternation (|)
  private static NFA handleAltern(ArrayList<RegExTree> subTrees) {
      NFA leftNFA = buildNFAFromRegExTree(subTrees.get(0));
      NFA rightNFA = buildNFAFromRegExTree(subTrees.get(1));
      
      StateA init =new StateA(0);
      StateA fin = new StateA(0);
  
      // Ajouter des transitions epsilon du nouvel état initial vers les états initiaux des deux automates
      init.addEpsilonTransition(leftNFA.getInitialStateA());
      init.addEpsilonTransition(rightNFA.getInitialStateA());
      
      // Connecter les états finaux des deux automates au nouvel état final avec des epsilon-transitions
      leftNFA.getFinalStateA().addEpsilonTransition(fin);
      rightNFA.getFinalStateA().addEpsilonTransition(fin);
      
      leftNFA.allisnotFinal();
      rightNFA.allisnotFinal();
      
      NFA combinedNFA = new NFA();
      combinedNFA.setInitialStateA(init);
      combinedNFA.addAllStateAs(leftNFA.getStateAs());
      combinedNFA.addAllStateAs(rightNFA.getStateAs());
      combinedNFA.setFinalStateA(fin);
  
      return combinedNFA;
  }
  
  // Gérer l'étoile (*)
  private static NFA handleEtoile(RegExTree subTree) {
      NFA subNFA = buildNFAFromRegExTree(subTree);
  
      StateA init = new StateA(0);
      StateA fin = new StateA(1);
  
      init.addEpsilonTransition(subNFA.getInitialStateA());
      init.addEpsilonTransition(fin);
  
      subNFA.getFinalStateA().addEpsilonTransition(subNFA.getInitialStateA());
      subNFA.getFinalStateA().addEpsilonTransition(fin);
      subNFA.allisnotFinal();
  
      NFA starredNFA = new NFA();
      starredNFA.setInitialStateA(init);
      starredNFA.addAllStateAs(subNFA.getStateAs());
      starredNFA.setFinalStateA(fin);
  
      return starredNFA;
  }
}
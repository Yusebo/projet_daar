package RegEx;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.Exception;

public class RegEx {
  //MACROS
  static final int CONCAT = 0xC04CA7;
  static final int ETOILE = 0xE7011E;
  static final int ALTERN = 0xA17E54;
  static final int PROTECTION = 0xBADDAD;

  static final int PARENTHESEOUVRANT = 0x16641664;
  static final int PARENTHESEFERMANT = 0x51515151;
  static final int DOT = 0xD07;
 
  
  //CONSTRUCTOR
  public RegEx(){}
  
  public static void searchonfile(String regEx, File file) {
	    System.out.println("Welcome to the RegEx search.");
	    
	    if (regEx.length() < 1) {
	      System.err.println("  >> ERROR: empty regEx.");
	      return;
	    }
	    long startTime = System.currentTimeMillis();
	    DFA dfam = parseregex(regEx);
	    

	    System.out.println("  >> Parsing completed.");

	    int lineNumber = 0;
	    int resultNumber = 0;
	    StringBuilder sb = new StringBuilder();
	    String text = readToString(file);
	    String[] lines = text.split("\\n");

	    // Recherche des correspondances dans le fichier
	    for (String line : lines) {
	      lineNumber++;
	      if (search(dfam, dfam.getInitialStateA(), line, 0)) {
	        sb.append(lineNumber).append(" - ").append(line).append("\n");
	        resultNumber++;
	      }
	    }
	    System.out.println(sb.toString());
	    System.out.println(resultNumber + " lines matched found");
	    long searchEndTime = System.currentTimeMillis();
	    System.out.println("Time Used： " + (searchEndTime - startTime) + "ms");
	  }
  
  public static DFA parseregex(String regEx) {
	  DFA dfam = null;
	  if (regEx.length()<1) {
	        System.err.println("  >> ERROR: empty regEx.");
	      } else {
	        System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
	        for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
	        System.out.println("].");
	        try {
	          RegExTree ret = parse(regEx);

	          NFA nfa = NFABuilder.buildNFAFromRegExTree(ret);

	          DFA dfa = DFABuilder.buildDFAFromNFA(nfa);

	          dfam = DFAMinimizer.minimizeDFA(dfa);

	        } catch (Exception e) {
	          System.err.println("  >> ERROR: syntax error for regEx \""+regEx+"\".");
	        }
	      }
	  return dfam;
  }
  
  public static String readToString(File file) {
    long filelength = file.length();
    byte[] filecontent = new byte[(int) filelength];
    try {
        FileInputStream in = new FileInputStream(file);
        in.read(filecontent);
        in.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return new String(filecontent);
}
  
  public static boolean search(DFA dfa,StateA state, String line, int position) {
    if (state.isFinal())
        return true;

    if (position >= line.length())
        return false;

    Character input = line.charAt(position);

    StateA next = state.getTransition(input);

    if (next == null)
        return search(dfa, dfa.getInitialStateA(), line, position + 1);

    if (!search(dfa, next, line, position + 1))
      return search(dfa, dfa.getInitialStateA(), line, position + 1);

    return true;
}
  
  public static boolean searchsimple(DFA dfa,StateA state, String line, int position) {
	    if (state.isFinal())
	        return true;

	    if (position >= line.length())
	        return false;

	    Character input = line.charAt(position);

	    StateA next = state.getTransition(input);

	    if (next == null)
	        return false;

	    return searchsimple(dfa, next, line, position + 1);

	}

  //FROM REGEX TO SYNTAX TREE
  private static RegExTree parse(String regEx) throws Exception {
    //BEGIN DEBUG: set conditionnal to true for debug example
    if (false) throw new Exception();
    RegExTree example = exampleAhoUllman();
    if (false) return example;
    //END DEBUG

    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    for (int i=0;i<regEx.length();i++) result.add(new RegExTree(charToRoot(regEx.charAt(i)),new ArrayList<RegExTree>()));
    
    return parse(result);
  }
  private static int charToRoot(char c) {
    if (c=='.') return DOT;
    if (c=='*') return ETOILE;
    if (c=='|') return ALTERN;
    if (c=='(') return PARENTHESEOUVRANT;
    if (c==')') return PARENTHESEFERMANT;
    return (int)c;
  }
  private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
    while (containParenthese(result)) result=processParenthese(result);
    while (containEtoile(result)) result=processEtoile(result);
    while (containConcat(result)) result=processConcat(result);
    while (containAltern(result)) result=processAltern(result);

    if (result.size()>1) throw new Exception();

    return removeProtection(result.get(0));
  }
  private static boolean containParenthese(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==PARENTHESEFERMANT || t.root==PARENTHESEOUVRANT) return true;
    return false;
  }
  private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t: trees) {
      if (!found && t.root==PARENTHESEFERMANT) {
        boolean done = false;
        ArrayList<RegExTree> content = new ArrayList<RegExTree>();
        while (!done && !result.isEmpty())
          if (result.get(result.size()-1).root==PARENTHESEOUVRANT) { done = true; result.remove(result.size()-1); }
          else content.add(0,result.remove(result.size()-1));
        if (!done) throw new Exception();
        found = true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(parse(content));
        result.add(new RegExTree(PROTECTION, subTrees));
      } else {
        result.add(t);
      }
    }
    if (!found) throw new Exception();
    return result;
  }
  private static boolean containEtoile(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==ETOILE && t.subTrees.isEmpty()) return true;
    return false;
  }
  private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    for (RegExTree t: trees) {
      if (!found && t.root==ETOILE && t.subTrees.isEmpty()) {
        if (result.isEmpty()) throw new Exception();
        found = true;
        RegExTree last = result.remove(result.size()-1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        result.add(new RegExTree(ETOILE, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }
  private static boolean containConcat(ArrayList<RegExTree> trees) {
    boolean firstFound = false;
    for (RegExTree t: trees) {
      if (!firstFound && t.root!=ALTERN) { firstFound = true; continue; }
      if (firstFound) if (t.root!=ALTERN) return true; else firstFound = false;
    }
    return false;
  }
  private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    boolean firstFound = false;
    for (RegExTree t: trees) {
      if (!found && !firstFound && t.root!=ALTERN) {
        firstFound = true;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root==ALTERN) {
        firstFound = false;
        result.add(t);
        continue;
      }
      if (!found && firstFound && t.root!=ALTERN) {
        found = true;
        RegExTree last = result.remove(result.size()-1);
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(last);
        subTrees.add(t);
        result.add(new RegExTree(CONCAT, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }
  private static boolean containAltern(ArrayList<RegExTree> trees) {
    for (RegExTree t: trees) if (t.root==ALTERN && t.subTrees.isEmpty()) return true;
    return false;
  }
  private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
    ArrayList<RegExTree> result = new ArrayList<RegExTree>();
    boolean found = false;
    RegExTree gauche = null;
    boolean done = false;
    for (RegExTree t: trees) {
      if (!found && t.root==ALTERN && t.subTrees.isEmpty()) {
        if (result.isEmpty()) throw new Exception();
        found = true;
        gauche = result.remove(result.size()-1);
        continue;
      }
      if (found && !done) {
        if (gauche==null) throw new Exception();
        done=true;
        ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
        subTrees.add(gauche);
        subTrees.add(t);
        result.add(new RegExTree(ALTERN, subTrees));
      } else {
        result.add(t);
      }
    }
    return result;
  }
  private static RegExTree removeProtection(RegExTree tree) throws Exception {
    if (tree.root==PROTECTION && tree.subTrees.size()!=1) throw new Exception();
    if (tree.subTrees.isEmpty()) return tree;
    if (tree.root==PROTECTION) return removeProtection(tree.subTrees.get(0));

    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    for (RegExTree t: tree.subTrees) subTrees.add(removeProtection(t));
    return new RegExTree(tree.root, subTrees);
  }
  
  //EXAMPLE
  // --> RegEx from Aho-Ullman book Chap.10 Example 10.25
  private static RegExTree exampleAhoUllman() {
    RegExTree a = new RegExTree((int)'a', new ArrayList<RegExTree>());
    RegExTree b = new RegExTree((int)'b', new ArrayList<RegExTree>());
    RegExTree c = new RegExTree((int)'c', new ArrayList<RegExTree>());
    ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
    subTrees.add(c);
    RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
    subTrees = new ArrayList<RegExTree>();
    subTrees.add(b);
    subTrees.add(cEtoile);
    RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
    subTrees = new ArrayList<RegExTree>();
    subTrees.add(a);
    subTrees.add(dotBCEtoile);
    return new RegExTree(ALTERN, subTrees);
  }
}








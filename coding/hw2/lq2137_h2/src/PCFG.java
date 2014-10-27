import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;

/**
 * Calculates PCFGs
 * 
 * @author linanqiu
 * @file_name PCFG.java
 */

public class PCFG {

  public static final String RARETAG = "_RARE_";
  public static final String UNARYRULE = "UNARYRULE";
  public static final String NONTERMINAL = "NONTERMINAL";
  public static final String BINARYRULE = "BINARYRULE";

  private ArrayList<JSONArray> trees;

  private Hashtable<String, Integer> rareWordBucket;

  private Hashtable<String, Integer> wordCounts;
  private Hashtable<String, Integer> dictionary;

  // rule parameters
  private Hashtable<String, Integer> binaryCounts;
  private Hashtable<String, Integer> unaryCounts;
  private Hashtable<String, Integer> nonTerminalCounts;

  private Hashtable<String, ArrayList<String>> xyzProductions;


  /**
   * Sets overall counts
   * 
   * @param cfg_counts
   * @throws NumberFormatException
   * @throws IOException
   */
  public void setCounts(File cfg_counts) throws NumberFormatException,
      IOException {

    rareWordBucket = new Hashtable<String, Integer>();
    wordCounts = new Hashtable<String, Integer>();
    nonTerminalCounts = new Hashtable<String, Integer>();
    unaryCounts = new Hashtable<String, Integer>();
    binaryCounts = new Hashtable<String, Integer>();
    xyzProductions = new Hashtable<String, ArrayList<String>>();
    dictionary = new Hashtable<String, Integer>();

    BufferedReader bufferedReader = new BufferedReader(new FileReader(
        cfg_counts));

    String line;

    while ((line = bufferedReader.readLine()) != null) {
      // rare word counts
      String[] items = line.split(" ");
      int count = Integer.parseInt(items[0]);

      if (items[1].equals(UNARYRULE)) {
        String word = items[3];
        if (wordCounts.containsKey(word)) {
          wordCounts.put(word, wordCounts.get(word) + count);
        } else {
          wordCounts.put(word, count);
        }
      }

      // nonterminal, binary, unary counts
      if (items[1].equals(NONTERMINAL)) {
        if (nonTerminalCounts.containsKey(items[2])) {
          nonTerminalCounts.put(items[2],
              nonTerminalCounts.get(items[2] + count));
        } else {
          nonTerminalCounts.put(items[2], count);
        }
      } else if (items[1].equals(UNARYRULE)) {
        String unaryRule = items[2] + " " + items[3];
        if (unaryCounts.containsKey(unaryRule)) {
          unaryCounts.put(unaryRule, unaryCounts.get(unaryRule) + count);
        } else {
          unaryCounts.put(unaryRule, count);
        }
      } else if (items[1].equals(BINARYRULE)) {
        String binaryRule = items[2] + " " + items[3] + " " + items[4];
        if (binaryCounts.containsKey(binaryRule)) {
          binaryCounts.put(binaryRule, binaryCounts.get(binaryRule) + count);
        } else {
          binaryCounts.put(binaryRule, count);
        }

        if (xyzProductions.containsKey(items[2])) {
          xyzProductions.get(items[2]).add(binaryRule);
        } else {
          ArrayList<String> rules = new ArrayList<String>();
          rules.add(binaryRule);
          xyzProductions.put(items[2], rules);
        }
      }
    }
    bufferedReader.close();

    // initialize rarewordbuckets

    for (String key : wordCounts.keySet()) {
      if (wordCounts.get(key) < 5) {
        rareWordBucket.put(key, wordCounts.get(key));
      } else {
        dictionary.put(key, wordCounts.get(key));
      }
    }
  }

  /**
   * Parses trees
   * 
   * @param parse_train_dat
   * @throws IOException
   */
  public void setTrees(File parse_train_dat) throws IOException {

    BufferedReader bufferedReader = new BufferedReader(new FileReader(
        parse_train_dat));

    trees = new ArrayList<JSONArray>();

    String line;

    while ((line = bufferedReader.readLine()) != null) {
      JSONArray treeJson = new JSONArray(line);
      trees.add(treeJson);
    }

    bufferedReader.close();
  }

  /**
   * Does a DFS, replaces rare words
   * 
   * @throws IOException
   */
  public void replaceRare() throws IOException {
    TreeFunctionReplaceRare replaceRareFunction = new TreeFunctionReplaceRare(
        rareWordBucket);
    for (JSONArray tree : trees) {
      traverseTree(tree, replaceRareFunction);
    }
  }

  /**
   * Traverses tree. Assumes CNF
   * 
   * @param tree
   * @param function
   */
  private void traverseTree(JSONArray tree, TreeFunction function) {
    if (tree.length() == 3) {
      traverseTree(tree.getJSONArray(1), function);
      traverseTree(tree.getJSONArray(2), function);
    } else {
      function.treeFunction(tree);
    }
  }

  /**
   * Writes trees to file
   * 
   * @param parse_train_replaced_dat
   * @throws IOException
   */
  public void writeTree(File parse_train_replaced_dat) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
        parse_train_replaced_dat));

    for (JSONArray tree : trees) {
      bufferedWriter.write(tree.toString());
      bufferedWriter.write("\n");
    }

    bufferedWriter.close();
  }

  /**
   * Probability for binary 
   * 
   * @param rule
   * @return
   */
  public double binaryProbability(String rule) {
    String[] ruleComponents = rule.split(" ");
    // System.out.println(binaryCounts.get(rule));
    // System.out.println(nonTerminalCounts.get(ruleComponents[0]));
    if (!nonTerminalCounts.containsKey(ruleComponents[0])) {
      return 0;
    }

    // if (binaryCounts.containsKey(rule)) {
    double binaryCount = binaryCounts.get(rule);
    double nonTerminalCount = nonTerminalCounts.get(ruleComponents[0]);
    return binaryCount / nonTerminalCount;
    // } else {
    // return 0;
    // }
  }

  /**
   * Probability for unary
   * 
   * @param rule
   * @return
   */
  public double unaryProbability(String rule) {

    String[] ruleComponents = rule.split(" ");
    // System.out.println(unaryCounts.get(rule));
    // System.out.println(nonTerminalCounts.get(ruleComponents[0]));
    if (!nonTerminalCounts.containsKey(ruleComponents[0])) {
      return 0;
    }

    if (unaryCounts.containsKey(rule)) {
      double unaryCount = unaryCounts.get(rule);
      double nonTerminalCount = nonTerminalCounts.get(ruleComponents[0]);
      return unaryCount / nonTerminalCount;
    } else {
      return 0;
    }
  }

  /**
   * Run CKY on each line
   * 
   * @param parse_dev
   * @param parse_dev_predict
   * @throws IOException
   */
  public void predict(File parse_dev, File parse_dev_predict)
      throws IOException {
    BufferedReader bufferedReader = new BufferedReader(
        new FileReader(parse_dev));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
        parse_dev_predict));

    String line = "";
    int count = 0;

    while ((line = bufferedReader.readLine()) != null) {
      JSONArray parseTree = cky(line);
      bufferedWriter.write(parseTree.toString() + "\n");
      System.out.print("pcfg.jar: " + "Processed line: " + count + "\r");
      count++;
    }

    bufferedReader.close();
    bufferedWriter.close();
  }

  /**
   * Gets pitable and gets backpointer
   * 
   * @param line
   * @return
   */
  public JSONArray cky(String line) {
    PiTable piTable = buildTable(line);
    String[] items = line.split(" ");
    JSONArray maxJsonArray = getMaxJSONArray(piTable, items.length);
    return maxJsonArray;
  }

  /**
   * Utility function for getting maxjsonarray
   * 
   * @param piTable
   * @param length
   * @return
   */
  private JSONArray getMaxJSONArray(PiTable piTable, int length) {
    return piTable.getBackpointer(0, length - 1, piTable.getHead());
  }

  /**
   * Builds pitable
   * 
   * @param line
   * @return
   */
  private PiTable buildTable(String line) {
    String[] items = line.split(" ");

    PiTable piTable = new PiTable(items.length);

    // initialize
    for (int i = 0; i < items.length; i++) {
      String word = "";
      if (dictionary.containsKey(items[i])) {
        word = items[i];
      } else {
        word = RARETAG;
      }
      for (String nonTerminal : nonTerminalCounts.keySet()) {
        String unaryRule = nonTerminal + " " + word;
        double probability = unaryProbability(unaryRule);
        unaryRule = nonTerminal + " " + items[i];
        JSONArray jsonArray = new JSONArray(unaryRule.split(" "));

        piTable.putProbability(i, i, nonTerminal, probability);
        piTable.putBackpointer(i, i, nonTerminal, jsonArray);
      }

    }

    for (int l = 1; l < items.length; l++) {
      for (int i = 0; i < items.length - l; i++) {
        int j = i + l;

        for (String x : xyzProductions.keySet()) {

          double maxCandidate = Integer.MIN_VALUE;
          int maxS = 0;
          String maxY = "";
          String maxZ = "";

          for (String binaryRule : xyzProductions.get(x)) {
            String[] nonTerminals = binaryRule.split(" ");
            String y = nonTerminals[1];
            String z = nonTerminals[2];

            for (int s = i; s < j; s++) {
              // System.out.println("i: " + i);
              // System.out.println("s: " + s);
              // System.out.println("j: " + j);
              // System.out.println("l: " + l);
              // System.out.println("x: " + x);
              // System.out.println("y: " + y);
              // System.out.println("z: " + z);
              // System.out.println("length: " + items.length);
              // System.out.println(line);
              double candidate = binaryProbability(binaryRule)
                  * piTable.getProbability(i, s, y)
                  * piTable.getProbability(s + 1, j, z);
              if (candidate > maxCandidate) {
                maxCandidate = candidate;
                maxS = s;
                maxY = y;
                maxZ = z;
              }
            }
          }

          piTable.putProbability(i, j, x, maxCandidate);

          // backpointer stuff
          JSONArray jsonArray = new JSONArray();
          jsonArray.put(x);
          jsonArray.put(piTable.getBackpointer(i, maxS, maxY));
          jsonArray.put(piTable.getBackpointer(maxS + 1, j, maxZ));
          piTable.putBackpointer(i, j, x, jsonArray);
        }
      }
    }

    return piTable;
  }
}

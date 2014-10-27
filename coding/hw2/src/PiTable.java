import java.util.Hashtable;
import java.util.TreeMap;

import org.json.JSONArray;

public class PiTable {

  private Hashtable<Integer, Hashtable<String, Double>> values;
  private Hashtable<Integer, TreeMap<Double, String>> reverseValues;
  private Hashtable<Integer, Hashtable<String, JSONArray>> backpointers;
  private int[][] mapping;

  public PiTable(int size) {
    mapping = new int[size][size];
    values = new Hashtable<Integer, Hashtable<String, Double>>();
    reverseValues = new Hashtable<Integer, TreeMap<Double, String>>();
    backpointers = new Hashtable<Integer, Hashtable<String, JSONArray>>();

    int count = 0;
    for (int j = 0; j < size; j++) {
      for (int i = 0; i < size; i++) {
        values.put(count, new Hashtable<String, Double>());
        reverseValues.put(count, new TreeMap<Double, String>());
        backpointers.put(count, new Hashtable<String, JSONArray>());
        mapping[i][j] = count;
        count++;
      }
    }
  }

  public Hashtable<String, Double> getHashtable(int i, int j) {
    return values.get(mapping[i][j]);
  }

  public void putProbability(int i, int j, String nonTerminal,
      double probability) {
    values.get(mapping[i][j]).put(nonTerminal, probability);
    reverseValues.get(mapping[i][j]).put(probability, nonTerminal);
  }

  public double getProbability(int i, int j, String nonTerminal) {
    if (values.get(mapping[i][j]).containsKey(nonTerminal)) {
      return values.get(mapping[i][j]).get(nonTerminal);
    } else {
      return 0;
    }
  }

  public void putBackpointer(int i, int j, String nonTerminal,
      JSONArray jsonArray) {
    backpointers.get(mapping[i][j]).put(nonTerminal, jsonArray);
  }

  public boolean hasNonTerminal(int i, int j, String nonTerminal) {
    return (values.get(mapping[i][j]).get(nonTerminal) > 0);
  }

  public String getHead() {
    if (values.get(mapping[0][mapping.length - 1]).get("S") > 0) {
      return "S";
    } else {
      return reverseValues.get(mapping[0][mapping.length - 1]).lastEntry()
          .getValue();
    }
  }

  public JSONArray getBackpointer(int i, int j, String nonTerminal) {
    return backpointers.get(mapping[i][j]).get(nonTerminal);
  }

  public String toString() {
    String output = "";
    for (int j = 0; j < mapping.length; j++) {
      for (int i = 0; i < mapping.length; i++) {
        output += (i + ", " + j) + "\n";
        output += getHashtable(i, j).toString() + "\n";
      }
    }
    return output;
  }
}

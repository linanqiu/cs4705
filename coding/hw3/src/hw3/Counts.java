package hw3;

import java.util.ArrayList;
import java.util.Hashtable;

public class Counts {

  // c(e|f). f in outer, e in inner.
  private Hashtable<String, Hashtable<String, Integer>> cef;
  // c(e)
  private Hashtable<String, Integer> ce;

  // c(j|ilm)
  private Hashtable<ArrayList<Integer>, Hashtable<String, Integer>> cjilm;
  // c(ilm)
  private Hashtable<ArrayList<Integer>, Integer> cilm;

  public Counts() {
    cef = new Hashtable<String, Hashtable<String, Integer>>();
    ce = new Hashtable<String, Integer>();
    cjilm = new Hashtable<ArrayList<Integer>, Hashtable<String, Integer>>();
    cilm = new Hashtable<ArrayList<Integer>, Integer>();
  }

  public int getCef(String e, String f) {
    if (cef.containsKey(f)) {
      if (cef.get(f).containsKey(e)) {
        return cef.get(f).get(e);
      }
    }

    return 0;
  }

}

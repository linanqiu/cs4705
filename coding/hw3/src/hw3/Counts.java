package hw3;

import java.util.Hashtable;

public class Counts {

  // c(e|f). f in outer, e in inner.
  private Hashtable<String, Hashtable<String, Double>> cef;
  // c(e)
  private Hashtable<String, Double> ce;

  // c(j|ilm)
  private Hashtable<Integer, Hashtable<Integer, Double>> cjilm;
  // c(ilm)
  private Hashtable<Integer, Double> cilm;

  public Counts() {
    cef = new Hashtable<String, Hashtable<String, Double>>();
    ce = new Hashtable<String, Double>();
    cjilm = new Hashtable<Integer, Hashtable<Integer, Double>>();
    cilm = new Hashtable<Integer, Double>();
  }

  public double getCef(String e, String f) {
    if (cef.containsKey(f)) {
      if (cef.get(f).containsKey(e)) {
        return cef.get(f).get(e);
      }
    }

    return 0;
  }

  public double getCe(String e) {
    if (ce.containsKey(e)) {
      return ce.get(e);
    }
    return 0;
  }

  public double getCjilm(int j, int i, int l, int m) {
    int tripleHash = tripleHash(i, l, m);
    if (cjilm.containsKey(tripleHash)) {
      if (cjilm.get(tripleHash).containsKey(j)) {
        return cjilm.get(tripleHash).get(j);
      }
    }

    return 0;
  }

  public double getCilm(int i, int l, int m) {
    int tripleHash = tripleHash(i, l, m);
    if (cilm.containsKey(tripleHash)) {
      return cilm.get(tripleHash);
    }

    return 0;
  }

  public void putCef(String e, String f, double count) {
    if (cef.containsKey(f)) {
      cef.get(f).put(e, count);
    } else {
      Hashtable<String, Double> prob = new Hashtable<String, Double>();
      prob.put(e, count);
      cef.put(f, prob);
    }
  }

  public void putCe(String e, double count) {
    ce.put(e, count);
  }

  public void putCjilm(int j, int i, int l, int m, double count) {
    int tripleHash = tripleHash(i, l, m);
    if (cjilm.containsKey(tripleHash)) {
      cjilm.get(tripleHash).put(j, count);
    } else {
      Hashtable<Integer, Double> prob = new Hashtable<Integer, Double>();
      prob.put(j, count);
      cjilm.put(tripleHash, prob);
    }
  }

  public void putCilm(int i, int l, int m, double count) {
    int tripleHash = tripleHash(i, l, m);
    cilm.put(tripleHash, count);
  }

  public int tripleHash(int i, int l, int m) {
    int h = i;
    h = h * 31 + l;
    h = h * 31 + m;
    return h;
  }
}

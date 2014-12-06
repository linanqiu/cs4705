import java.util.Hashtable;

/**
 * Keeps track of counts used in EM. If not, it will be super messy to have so
 * many tables in the IBM classes. Getting and putting from them will be super
 * messy too and prone to errors.
 * 
 * @author linanqiu
 * @file_name Counts.java
 */
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

  /**
   * Gets c(e|f)
   * 
   * @param e
   *          english word
   * @param f
   *          german word
   * @return c(e|f) count of e given f
   */
  public double getCef(String e, String f) {
    if (cef.containsKey(f)) {
      if (cef.get(f).containsKey(e)) {
        return cef.get(f).get(e);
      }
    }

    return 0;
  }

  /**
   * Gets c(e)
   * 
   * @param e
   *          english word
   * @return c(e) count of e
   */
  public double getCe(String e) {
    if (ce.containsKey(e)) {
      return ce.get(e);
    }
    return 0;
  }

  /**
   * Gets c(j|i,l,m)
   * 
   * @param j
   * @param i
   * @param l
   * @param m
   * @return c(j|i,l.m)
   */
  public double getCjilm(int j, int i, int l, int m) {
    int tripleHash = tripleHash(i, l, m);
    if (cjilm.containsKey(tripleHash)) {
      if (cjilm.get(tripleHash).containsKey(j)) {
        return cjilm.get(tripleHash).get(j);
      }
    }

    return 0;
  }

  /**
   * Gets c(j|i,l,m). ilm represented using tripleHash(i,l,m)
   * 
   * @param j
   * @param tripleHash
   *          triplehash of i,l,m.
   * @return c(j|i,l,m)
   */
  public double getCjilm(int j, int tripleHash) {
    if (cjilm.containsKey(tripleHash)) {
      if (cjilm.get(tripleHash).containsKey(j)) {
        return cjilm.get(tripleHash).get(j);
      }
    }
    return 0;
  }

  /**
   * c(i,l,m)
   * 
   * @param i
   * @param l
   * @param m
   * @return c(i,l,m)
   */
  public double getCilm(int i, int l, int m) {
    int tripleHash = tripleHash(i, l, m);
    if (cilm.containsKey(tripleHash)) {
      return cilm.get(tripleHash);
    }

    return 0;
  }

  /**
   * c(i,l,m) with ilm represented using a tripleHash(i,l,m)
   * 
   * @param tripleHash
   * @return c(i,l,m)
   */
  public double getCilm(int tripleHash) {
    if (cilm.containsKey(tripleHash)) {
      return cilm.get(tripleHash);
    }

    return 0;
  }

  /**
   * Writes c(e|f) = count
   * 
   * @param e
   * @param f
   * @param count
   */
  public void putCef(String e, String f, double count) {
    if (cef.containsKey(f)) {
      cef.get(f).put(e, count);
    } else {
      Hashtable<String, Double> prob = new Hashtable<String, Double>();
      prob.put(e, count);
      cef.put(f, prob);
    }
  }

  /**
   * Writes c(e) = count
   * 
   * @param e
   * @param count
   */
  public void putCe(String e, double count) {
    ce.put(e, count);
  }

  /**
   * Writes c(j|ilm) = count
   * 
   * @param j
   * @param i
   * @param l
   * @param m
   * @param count
   */
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

  /**
   * Writes c(ilm) = count
   * 
   * @param i
   * @param l
   * @param m
   * @param count
   */
  public void putCilm(int i, int l, int m, double count) {
    int tripleHash = tripleHash(i, l, m);
    cilm.put(tripleHash, count);
  }

  /**
   * Hash of 3 integers
   * 
   * @param i
   * @param l
   * @param m
   * @return hash
   */
  private int tripleHash(int i, int l, int m) {
    int h = i;
    h = h * 31 + l;
    h = h * 31 + m;
    return h;
  }
}

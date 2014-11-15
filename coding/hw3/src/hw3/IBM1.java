package hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

public class IBM1 {

  private File corpusDe;
  private File corpusEn;

  public static final String NULL_TAG = "NULL";
  public static final int ITERATIONS = 5;

  // t(f|e) outer key e, inner key f
  private Hashtable<String, Hashtable<String, Double>> t;

  public IBM1(File corpusDe, File corpusEn) {
    this.corpusDe = corpusDe;
    this.corpusEn = corpusEn;
  }

  public void initializeT() throws IOException {

    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    t = new Hashtable<String, Hashtable<String, Double>>();

    String lineDe;
    String lineEn;

    // int lineCount = 0;

    while (((lineDe = brDe.readLine()) != null)
        && ((lineEn = brEn.readLine())) != null) {
      // lineCount++;
      // System.out.println(lineCount + " " + t.keySet().size());

      lineEn = NULL_TAG + " " + lineEn;
      String[] wordsDe = lineDe.split(" ");
      String[] wordsEn = lineEn.split(" ");

      for (String wordEn : wordsEn) {
        for (String wordDe : wordsDe) {
          if (t.containsKey(wordEn)) {
            t.get(wordEn).put(wordDe, (double) 0);
          } else {
            Hashtable<String, Double> inner = new Hashtable<String, Double>();
            inner.put(wordDe, (double) 0);
            t.put(wordEn, inner);
          }
        }
      }
    }

    // calculate ne
    for (String wordEn : t.keySet()) {
      int count = t.get(wordEn).size();
      for (String wordDe : t.get(wordEn).keySet()) {
        t.get(wordEn).put(wordDe, (double) 1 / count);
      }
    }
  }

  public void expMax() {
    for (int s = 0; s < ITERATIONS; s++) {

    }
  }

  public void printTValues() {
    for (String wordDe : t.keySet()) {
      for (String wordEn : t.get(wordDe).keySet()) {
        System.out.println(wordDe + "\t" + wordEn + "\t"
            + t.get(wordDe).get(wordEn));
      }
    }
  }

}

package hw3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.TreeMap;

public class IBM1 {

  private File corpusDe;
  private File corpusEn;

  public static final String NULL_TAG = "NULL";
  public static final int ITERATIONS = 5;
  public static final int RANKING_PRINT = 10;
  public static final int ALIGNMENT_SENTENCE_LIMIT = 20;

  // t(f|e) outer key e, inner key f
  private Hashtable<String, Hashtable<String, Double>> t;

  public IBM1(File corpusDe, File corpusEn) {
    this.corpusDe = corpusDe;
    this.corpusEn = corpusEn;
  }

  public void initializeT() throws IOException {

    System.out.println("Initializing");

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

    brDe.close();
    brEn.close();
  }

  public void expMax() throws IOException {
    System.out.println("EM Algorithm");

    // small corpus, so I'm reading it into memory. Don't kill me.
    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    ArrayList<String> corpusDeList = new ArrayList<String>();
    ArrayList<String> corpusEnList = new ArrayList<String>();

    String lineDeBr;
    String lineEnBr;

    while (((lineDeBr = brDe.readLine()) != null)
        && ((lineEnBr = brEn.readLine())) != null) {
      corpusDeList.add(lineDeBr);
      corpusEnList.add(lineEnBr);
    }

    for (int s = 0; s < ITERATIONS; s++) {
      System.out.println("Iteration " + s);
      Counts counts = new Counts();
      // all counts are set to zero in the class

      for (int a = 0; a < corpusDeList.size(); a++) {

        String lineEn = corpusEnList.get(a);
        String lineDe = corpusDeList.get(a);

        lineEn = NULL_TAG + " " + lineEn;
        String[] wordsDe = lineDe.split(" ");
        String[] wordsEn = lineEn.split(" ");

        for (int i = 0; i < wordsDe.length; i++) {

          for (int j = 0; j < wordsEn.length; j++) {

            String wordDe = wordsDe[i];
            String wordEn = wordsEn[j];

            double deltaTop = t.get(wordEn).get(wordDe);
            double deltaBottom = 0;
            for (int k = 0; k < wordsEn.length; k++) {
              deltaBottom += t.get(wordsEn[k]).get(wordDe);
            }
            double delta = deltaTop / deltaBottom;

            counts
                .putCef(wordEn, wordDe, counts.getCef(wordEn, wordDe) + delta);
            counts.putCe(wordEn, counts.getCe(wordEn) + delta);
            int l = wordsEn.length;
            int m = wordsDe.length;
            counts.putCjilm(j, i, l, m, counts.getCjilm(j, i, l, m) + delta);
            counts.putCilm(i, l, m, counts.getCilm(i, l, m) + delta);
          }
        }
      }

      for (String wordEn : t.keySet()) {
        for (String wordDe : t.get(wordEn).keySet()) {
          t.get(wordEn).put(wordDe,
              counts.getCef(wordEn, wordDe) / counts.getCe(wordEn));
        }
      }
    }

    brDe.close();
    brEn.close();
  }

  public void devwordsForeignRank(File devwords) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(devwords));
    String wordEn;

    while ((wordEn = br.readLine()) != null) {
      Hashtable<String, Double> foreign = t.get(wordEn);
      TreeMap<Double, String> ranking = new TreeMap<Double, String>();

      for (String wordDe : foreign.keySet()) {
        ranking.put(foreign.get(wordDe), wordDe);
      }

      System.out.println("=== " + wordEn + " ===");

      int rankingLength = Math.min(RANKING_PRINT, ranking.size());
      for (int i = 0; i < rankingLength; i++) {
        Entry<Double, String> firstEntry = ranking.pollFirstEntry();
        System.out.println(firstEntry);
      }
    }
    br.close();
  }

  public void findAlignments() throws IOException {
    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    String lineDe;
    String lineEn;

    // int lineCount = 0;

    while (((lineDe = brDe.readLine()) != null)
        && ((lineEn = brEn.readLine())) != null) {

      lineEn = NULL_TAG + " " + lineEn;
      String[] wordsDe = lineDe.split(" ");
      String[] wordsEn = lineEn.split(" ");

      int[] alignment = new int[wordsDe.length];

      for (int i = 0; i < wordsDe.length; i++) {

        String wordDe = wordsDe[i];

        int aMax = -1;
        double tMax = 0;

        for (int j = 0; j < wordsEn.length; j++) {
          String wordEn = wordsEn[j];
          double tCandidate = t.get(wordEn).get(wordDe);
          if (tCandidate > tMax) {
            tMax = tCandidate;
            aMax = j;
          }
        }
        alignment[i] = aMax;
      }

      for (int a : alignment) {
        System.out.print(a + " ");
      }
      System.out.println();
    }

    brDe.close();
    brEn.close();
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

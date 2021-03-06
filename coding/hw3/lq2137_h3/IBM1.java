import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * IBM1 Model
 * 
 * @author linanqiu
 * @file_name IBM1.java
 */
public class IBM1 {

  // corpus files
  protected File corpusDe;
  protected File corpusEn;

  public static final String NULL_TAG = "NULL";
  public static final int ITERATIONS = 5; // number of iterations for EM
                                          // algorithm
  public static final int RANKING_PRINT = 10; // number of devword lookups to
                                              // print
  public static final int ALIGNMENT_SENTENCE_LIMIT = 20; // number of sentences
                                                         // to align

  // t(f|e) outer key e, inner key f
  protected Hashtable<String, Hashtable<String, Double>> t;

  public IBM1(File corpusDe, File corpusEn) {
    this.corpusDe = corpusDe;
    this.corpusEn = corpusEn;
  }

  /**
   * Initializes t table values to 1/n(e)
   * 
   * @throws IOException
   */
  public void initializeT() throws IOException {

    System.out.println("IBM1: Initializing");

    // first reads in all english words and all german words possibly associated
    // with the english word

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

  /**
   * Expectation Maximization Algorithm. Since we have a small corpus, I read
   * the entire corpus into the RAM to save run time
   * 
   * @throws IOException
   */
  public void expMax() throws IOException {
    System.out.println("IBM1: EM Algorithm");

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
      System.out.println("IBM1: Iteration " + (s + 1));
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

      // sets t values
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

  /**
   * Completes question 4.2
   * 
   * Writes to ibm1_devwords_ranking.txt
   * 
   * @param devwords
   * @throws IOException
   */
  public void devwordsForeignRank(File devwords) throws IOException {
    BufferedWriter bw = new BufferedWriter(new FileWriter(
        "ibm1_devwords_ranking.txt"));

    BufferedReader br = new BufferedReader(new FileReader(devwords));
    String wordEn;

    while ((wordEn = br.readLine()) != null) {
      Hashtable<String, Double> foreign = t.get(wordEn);
      TreeMap<Double, String> ranking = new TreeMap<Double, String>();

      for (String wordDe : foreign.keySet()) {
        ranking.put(foreign.get(wordDe), wordDe);
      }

      bw.write(wordEn + "\n");
      bw.write("[");
      int rankingLength = Math.min(RANKING_PRINT, ranking.size());
      for (int i = 0; i < rankingLength; i++) {
        Entry<Double, String> lastEntry = ranking.pollLastEntry();
        bw.write("('" + lastEntry.getValue() + "', " + lastEntry.getKey() + ")");
        if(i != rankingLength - 1) {
          bw.write(", ");
        }
      }
      bw.write("]\n\n");
    }
    bw.close();
    br.close();
  }

  /**
   * Completes question 4.3
   * 
   * Writes to ibm1_alignment.txt
   * 
   * @throws IOException
   */
  public void findAlignments() throws IOException {
    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    BufferedWriter bw = new BufferedWriter(new FileWriter("ibm1_alignment.txt"));

    String lineDe;
    String lineEn;

    int lineCount = 0;

    while (((lineDe = brDe.readLine()) != null)
        && ((lineEn = brEn.readLine())) != null) {
      if (lineCount == ALIGNMENT_SENTENCE_LIMIT) {
        brDe.close();
        brEn.close();
        bw.close();
        return;
      }
      lineCount++;

      String lineEnOriginal = lineEn;

      lineEn = NULL_TAG + " " + lineEn;
      String[] wordsDe = lineDe.split(" ");
      String[] wordsEn = lineEn.split(" ");

      ArrayList<Integer> alignments = new ArrayList<Integer>();

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
        alignments.add(aMax);
      }

      bw.write(lineEnOriginal + "\n");
      bw.write(lineDe + "\n");
      bw.write(alignments + "\n");
      bw.write("\n");
    }
    brDe.close();
    brEn.close();
    bw.close();
  }

  /**
   * Serializes t table to a file so that we don't have to calculate it every
   * time. Testing f-yeah
   * 
   * @param tSerialize
   * @throws IOException
   */
  public void serializeT(File tSerialize) throws IOException {
    System.out.println("IBM1: Serializing t");
    FileOutputStream fileOut = new FileOutputStream(tSerialize);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(t);
    out.close();
    fileOut.close();
  }

  /**
   * Deserializes a t table from a file
   * 
   * @param tSerialize
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public void deserializeT(File tSerialize) throws IOException,
      ClassNotFoundException {

    System.out.println("IBM1: Deserializing t");

    FileInputStream fileIn = new FileInputStream(tSerialize);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    t = (Hashtable<String, Hashtable<String, Double>>) in.readObject();
    in.close();
    fileIn.close();
  }

  /**
   * Gets the t table
   * 
   * @return
   */
  public Hashtable<String, Hashtable<String, Double>> getT() {
    return t;
  }

  /**
   * Sets the t table
   * 
   * @param t
   */
  public void setT(Hashtable<String, Hashtable<String, Double>> t) {
    this.t = t;
  }
}

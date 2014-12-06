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

/**
 * IBM2 Model extended from Model 1
 * 
 * @author linanqiu
 * @file_name IBM2.java
 */
public class IBM2 extends IBM1 {

  // q(j|i, l, m), outer key is tripleHash(i, l, m), inner key is j, value is q
  // value
  protected Hashtable<Integer, Hashtable<Integer, Double>> q;
  protected int largeNegativeConstant;

  public static final int NEGATIVE_CONSTANT = -353916369;

  public IBM2(File corpusDe, File corpusEn) {
    super(corpusDe, corpusEn);
  }

  /**
   * Initialize Q parameters
   * 
   * @throws IOException
   */
  public void initializeQ() throws IOException {
    System.out.println("IBM2: Initializing q parameters");

    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    q = new Hashtable<Integer, Hashtable<Integer, Double>>();

    String lineDe;
    String lineEn;

    while (((lineDe = brDe.readLine()) != null)
        && ((lineEn = brEn.readLine())) != null) {

      lineEn = NULL_TAG + " " + lineEn;
      String[] wordsDe = lineDe.split(" ");
      String[] wordsEn = lineEn.split(" ");

      int l = wordsEn.length;
      int m = wordsDe.length;

      double qParam = (double) 1 / (l + 1);

      for (int i = 0; i < wordsDe.length; i++) {
        for (int j = 0; j < wordsEn.length; j++) {
          int tripleHash = tripleHash(i, l, m);
          if (q.containsKey(tripleHash)) {
            q.get(tripleHash).put(j, qParam);
          } else {
            Hashtable<Integer, Double> prob = new Hashtable<Integer, Double>();
            prob.put(j, qParam);
            q.put(tripleHash, prob);
          }
        }
      }
    }

    brDe.close();
    brEn.close();
  }

  /*
   * (non-Javadoc)
   * 
   * @see IBM1#initializeT()
   * 
   * Initialize T parameters using IBM1
   */
  public void initializeT() throws IOException {
    System.out.println("IBM2: Generating IBM1 t(f|e)");
    super.initializeT();
    super.expMax();
    super.devwordsForeignRank(new File("devwords.txt"));
    super.findAlignments();
  }

  /*
   * (non-Javadoc)
   * 
   * @see IBM1#expMax()
   * 
   * Train T and Q Parameters
   */
  public void expMax() throws IOException {
    System.out.println("IBM2: EM Algorithm");

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
      System.out.println("IBM2: Iteration " + (s + 1));
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

            int l = wordsEn.length;
            int m = wordsDe.length;

            double deltaTop = q.get(tripleHash(i, l, m)).get(j)
                * t.get(wordEn).get(wordDe);
            double deltaBottom = 0;
            for (int k = 0; k < wordsEn.length; k++) {
              deltaBottom += t.get(wordsEn[k]).get(wordDe);
            }
            double delta = deltaTop / deltaBottom;

            counts
                .putCef(wordEn, wordDe, counts.getCef(wordEn, wordDe) + delta);
            counts.putCe(wordEn, counts.getCe(wordEn) + delta);
            counts.putCjilm(j, i, l, m, counts.getCjilm(j, i, l, m) + delta);
            counts.putCilm(i, l, m, counts.getCilm(i, l, m) + delta);
          }
        }
      }

      // set t(f|e)
      for (String wordEn : t.keySet()) {
        for (String wordDe : t.get(wordEn).keySet()) {
          t.get(wordEn).put(wordDe,
              counts.getCef(wordEn, wordDe) / counts.getCe(wordEn));
        }
      }

      // set q(j|ilm)
      for (int tripleHash : q.keySet()) {
        for (int j : q.get(tripleHash).keySet()) {
          q.get(tripleHash).put(j,
              counts.getCjilm(j, tripleHash) / counts.getCilm(tripleHash));
        }
      }
    }

    brDe.close();
    brEn.close();
  }

  /**
   * Finds the best prob log for all alignmentes given two sentences
   * 
   * @param lineDe
   *          foreign language sentence
   * @param lineEn
   *          english sentence
   * @return best probability over all alignments
   */
  public double bestAlignmentProbLog(String lineDe, String lineEn) {
    double probLog = 0;

    lineEn = NULL_TAG + " " + lineEn;
    String[] wordsDe = lineDe.split(" ");
    String[] wordsEn = lineEn.split(" ");

    int l = wordsEn.length;
    int m = wordsDe.length;

    for (int i = 0; i < wordsDe.length; i++) {
      String wordDe = wordsDe[i];
      double tMax = 0;

      for (int j = 0; j < wordsEn.length; j++) {
        String wordEn = wordsEn[j];

        double tCandidate;

        try {
          tCandidate = q.get(tripleHash(i, l, m)).get(j)
              * t.get(wordEn).get(wordDe);
        } catch (NullPointerException e) {
          tCandidate = 0;
        }

        if (tCandidate > tMax) {
          tMax = tCandidate;
        }

      }

      if (tMax == 0) {
        probLog += NEGATIVE_CONSTANT;
      } else {
        probLog += Math.log(tMax);
      }
    }

    return probLog;
  }

  /*
   * (non-Javadoc)
   * 
   * @see IBM1#findAlignments()
   * 
   * Print alignments for 20 sentences
   */
  public void findAlignments() throws IOException {
    BufferedReader brDe = new BufferedReader(new FileReader(corpusDe));
    BufferedReader brEn = new BufferedReader(new FileReader(corpusEn));

    BufferedWriter bw = new BufferedWriter(new FileWriter("ibm2_alignment.txt"));

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

      int l = wordsEn.length;
      int m = wordsDe.length;

      for (int i = 0; i < wordsDe.length; i++) {
        String wordDe = wordsDe[i];
        int aMax = -1;
        double tMax = 0;

        for (int j = 0; j < wordsEn.length; j++) {
          String wordEn = wordsEn[j];

          double tCandidate = q.get(tripleHash(i, l, m)).get(j)
              * t.get(wordEn).get(wordDe);
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

  /*
   * (non-Javadoc)
   * 
   * @see IBM1#deserializeT(java.io.File)
   * 
   * Deserialieze the T table
   */
  @SuppressWarnings("unchecked")
  public void deserializeT(File tSerialize) throws IOException,
      ClassNotFoundException {

    System.out.println("IBM2: Deserializing t");

    FileInputStream fileIn = new FileInputStream(tSerialize);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    t = (Hashtable<String, Hashtable<String, Double>>) in.readObject();
    in.close();
    fileIn.close();
  }

  /**
   * Serialize T table
   * 
   * @param qSerialize
   * @throws IOException
   */
  public void serializeQ(File qSerialize) throws IOException {
    System.out.println("IBM2: Serializing q");
    FileOutputStream fileOut = new FileOutputStream(qSerialize);
    ObjectOutputStream out = new ObjectOutputStream(fileOut);
    out.writeObject(q);
    out.close();
    fileOut.close();
  }

  /**
   * Serialize Q Table
   * 
   * @param qSerialize
   * @throws IOException
   * @throws ClassNotFoundException
   */
  @SuppressWarnings("unchecked")
  public void deserializeQ(File qSerialize) throws IOException,
      ClassNotFoundException {
    System.out.println("IBM2: Deserializing q");

    FileInputStream fileIn = new FileInputStream(qSerialize);
    ObjectInputStream in = new ObjectInputStream(fileIn);
    q = (Hashtable<Integer, Hashtable<Integer, Double>>) in.readObject();
    in.close();
    fileIn.close();
  }

  /**
   * Gives a unique hash using 3 ints
   * 
   * @param i
   *          first int
   * @param l
   *          second int
   * @param m
   *          third int
   * @return unique hash
   */
  private int tripleHash(int i, int l, int m) {
    int h = i;
    h = h * 31 + l;
    h = h * 31 + m;
    return h;
  }
}

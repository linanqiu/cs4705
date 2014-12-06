import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Unscrambles two parallel corpi, with one scrambled
 * 
 * @author linanqiu
 * @file_name Unscramble.java
 */
public class Unscramble {

  private ArrayList<String> scrambledEn;
  private ArrayList<String> originalDe;
  private int[] unscrambledEn;
  private IBM2 ibm2;

  // private int[] answer;

  /**
   * Constructor for building IBM2 from scratch or from serializing harddisk t
   * table and q tables
   * 
   * @param scrambledEnFile
   *          scrambled english text
   * @param originalDeFile
   *          original german text
   * @param corpusDe
   *          development corpus for german for training t and q tables in IBM
   *          model
   * @param corpusEn
   *          devleopment corpus for english for training ta nd q tables in IBM
   *          model
   * @param serialize
   *          whether to read from ibm2_tserialze and ibm2_qserialize files, or
   *          to generate directly
   * @throws IOException
   * @throws ClassNotFoundException
   */
  public Unscramble(File scrambledEnFile, File originalDeFile, File corpusDe,
      File corpusEn, boolean serialize) throws IOException,
      ClassNotFoundException {
    BufferedReader brEn = new BufferedReader(new FileReader(scrambledEnFile));
    BufferedReader brDe = new BufferedReader(new FileReader(originalDeFile));

    scrambledEn = new ArrayList<String>();
    originalDe = new ArrayList<String>();

    String lineEn;
    String lineDe;

    while (((lineEn = brEn.readLine()) != null)
        && ((lineDe = brDe.readLine()) != null)) {
      scrambledEn.add(lineEn);
      originalDe.add(lineDe);
    }
    unscrambledEn = new int[scrambledEn.size()];

    ibm2 = new IBM2(corpusDe, corpusEn);

    if (serialize) {
      ibm2.deserializeT(new File("ibm2_tserialize"));
      ibm2.deserializeQ(new File("ibm2_qserialize"));
    } else {
      ibm2.initializeT();
      ibm2.initializeQ();
      ibm2.expMax();
    }
    ibm2.findAlignments();
  }

  /**
   * Constructor for taking in a trained IBM2 model directly
   * 
   * @param scrambledEnFile
   *          scrambled en parallel file
   * @param originalDeFile
   *          original german file
   * @param corpusDe
   *          german development parallel corpus
   * @param corpusEn
   *          english development parallel corpus
   * @param ibm2
   *          trained ibm2 model object
   * @throws IOException
   */
  public Unscramble(File scrambledEnFile, File originalDeFile, File corpusDe,
      File corpusEn, IBM2 ibm2) throws IOException {
    BufferedReader brEn = new BufferedReader(new FileReader(scrambledEnFile));
    BufferedReader brDe = new BufferedReader(new FileReader(originalDeFile));

    scrambledEn = new ArrayList<String>();
    originalDe = new ArrayList<String>();

    String lineEn;
    String lineDe;

    while (((lineEn = brEn.readLine()) != null)
        && ((lineDe = brDe.readLine()) != null)) {
      scrambledEn.add(lineEn);
      originalDe.add(lineDe);
    }
    unscrambledEn = new int[scrambledEn.size()];

    this.ibm2 = ibm2;
  }

  /**
   * Unscrambles by iterating through all english lines for each german line
   */
  public void unscramble() {
    System.out.println("Unscramble: Unscrambling");
    for (int i = 0; i < originalDe.size(); i++) {
      String lineDe = originalDe.get(i);
      int enIndex = findEnIndex(lineDe);
      unscrambledEn[i] = enIndex;
    }
  }

  /**
   * Writes unscrambled file based on the "sentence alignment" for the entire
   * english corpus
   * 
   * @throws IOException
   */
  public void writeUnscrambled() throws IOException {

    System.out.println("Unscramble: Writing answers");

    BufferedWriter bw = new BufferedWriter(new FileWriter("unscrambled.en"));

    for (int i : unscrambledEn) {
      String lineEn = scrambledEn.get(i);
      bw.write(lineEn + "\n");
    }
    bw.close();
  }

  //
  // public void tuneNegative() throws IOException {
  // int increments = 100000;
  // int initial = -353916369;
  // int finalNo = -303979461;
  // generateAnswer();
  //
  // int scoreMax = 0;
  // int optimal = 0;
  //
  // for (int i = initial; i < finalNo; i += increments) {
  // System.out.println(i);
  // ibm2.setLargeNegativeConstant(i);
  // unscramble();
  // int score = compareAnswer();
  // if (score > scoreMax) {
  // scoreMax = score;
  // optimal = i;
  // }
  // System.out.println(score);
  // System.out.println();
  // }
  //
  // System.out.println(scoreMax);
  // System.out.println(optimal);
  // }
  //
  // private int compareAnswer() {
  // int score = 0;
  //
  // for (int i = 0; i < answer.length; i++) {
  // if (answer[i] == unscrambledEn[i]) {
  // score++;
  // }
  // }
  // return score;
  // }
  //
  // private void generateAnswer() throws IOException {
  //
  // HashMap<String, Integer> scrambledDictionary = new HashMap<String,
  // Integer>();
  // for (int i = 0; i < scrambledEn.size(); i++) {
  // String lineEn = scrambledEn.get(i);
  // scrambledDictionary.put(lineEn, i);
  // }
  //
  // BufferedReader br = new BufferedReader(new FileReader(new File(
  // "original.en")));
  // String line;
  //
  // answer = new int[scrambledEn.size()];
  //
  // int lineCount = 0;
  // while ((line = br.readLine()) != null) {
  // answer[lineCount] = scrambledDictionary.get(line);
  // lineCount++;
  // }
  // br.close();
  // }

  /**
   * Finds the best english line for a given german line
   * 
   * @param lineDe
   * @return the index of the best english line in the scrambled english text
   */
  public int findEnIndex(String lineDe) {
    int index = 0;
    double bestProbLog = Integer.MIN_VALUE;

    for (int indexEn = 0; indexEn < scrambledEn.size(); indexEn++) {
      String lineEn = scrambledEn.get(indexEn);
      double probLog = ibm2.bestAlignmentProbLog(lineDe, lineEn);

      if (probLog > bestProbLog) {
        bestProbLog = probLog;
        index = indexEn;
      }
    }

    return index;
  }
}

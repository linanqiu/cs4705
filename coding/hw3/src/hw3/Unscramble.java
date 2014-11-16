package hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Unscramble {

  private ArrayList<String> scrambledEn;
  private ArrayList<String> originalDe;
  private int[] unscrambledEn;
  private IBM2 ibm2;

  private int[] answer;

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
      ibm2.expMax();
    }
  }

  public void unscramble() {
    System.out.println("Unscramble: Unscrambling");
    for (int i = 0; i < originalDe.size(); i++) {
      String lineDe = originalDe.get(i);
      int enIndex = findEnIndex(lineDe);
      unscrambledEn[i] = enIndex;
    }
  }

  public void writeUnscrambled() throws IOException {

    BufferedWriter bw = new BufferedWriter(new FileWriter("unscrambled.en"));

    for (int i : unscrambledEn) {
      String lineEn = scrambledEn.get(i);
      bw.write(lineEn + "\n");
    }
    bw.close();
  }

  public void tuneNegative() throws IOException {
    int increments = 100000;
    int initial = -353916369;
    int finalNo = -303979461;
    generateAnswer();

    int scoreMax = 0;
    int optimal = 0;

    for (int i = initial; i < finalNo; i += increments) {
      System.out.println(i);
      ibm2.setLargeNegativeConstant(i);
      unscramble();
      int score = compareAnswer();
      if (score > scoreMax) {
        scoreMax = score;
        optimal = i;
      }
      System.out.println(score);
      System.out.println();
    }

    System.out.println(scoreMax);
    System.out.println(optimal);
  }

  private int compareAnswer() {
    int score = 0;

    for (int i = 0; i < answer.length; i++) {
      if (answer[i] == unscrambledEn[i]) {
        score++;
      }
    }
    return score;
  }

  private void generateAnswer() throws IOException {

    HashMap<String, Integer> scrambledDictionary = new HashMap<String, Integer>();
    for (int i = 0; i < scrambledEn.size(); i++) {
      String lineEn = scrambledEn.get(i);
      scrambledDictionary.put(lineEn, i);
    }

    BufferedReader br = new BufferedReader(new FileReader(new File(
        "original.en")));
    String line;

    answer = new int[scrambledEn.size()];

    int lineCount = 0;
    while ((line = br.readLine()) != null) {
      answer[lineCount] = scrambledDictionary.get(line);
      lineCount++;
    }
    br.close();
  }

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

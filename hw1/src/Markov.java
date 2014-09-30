import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

public class Markov {

  public static final String GRAMTAG = "-GRAM";

  private Hashtable<String, Integer> unigramCounts;
  private Hashtable<String, Integer> bigramCounts;
  private Hashtable<String, Integer> trigramCounts;

  private Hashtable<Integer, Hashtable<String, Hashtable<String, Double>>> piTable;

  private EmissionParameters emissionParameters;

  public void readCounts(File ner_count) throws NumberFormatException,
      IOException {
    unigramCounts = new Hashtable<String, Integer>();
    bigramCounts = new Hashtable<String, Integer>();
    trigramCounts = new Hashtable<String, Integer>();

    BufferedReader bufferedReader = new BufferedReader(
        new FileReader(ner_count));
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      if (lineArray[1].indexOf(GRAMTAG) > -1) {
        int count = Integer.parseInt(lineArray[0]);
        int gramType = Integer.parseInt(lineArray[1].substring(0, 1));

        if (gramType == 1) {
          unigramCounts.put(lineArray[2], count);
        } else if (gramType == 2) {
          bigramCounts.put(lineArray[2] + " " + lineArray[3], count);
        } else if (gramType == 3) {
          trigramCounts.put(lineArray[2] + " " + lineArray[3] + " "
              + lineArray[4], count);
        } else {
          // ignore
        }
      }
    }
    bufferedReader.close();

    emissionParameters = new EmissionParameters();
    emissionParameters.setCounts(ner_count);
  }

  public double trigramBigramRatio(String tagm2, String tagm1, String tag)
      throws IOException {

    if (!bigramCounts.containsKey(tagm2 + " " + tagm1)) {
      return Integer.MIN_VALUE;
    } else {
      if (!trigramCounts.containsKey(tagm2 + " " + tagm1 + " " + tag)) {
        return Integer.MIN_VALUE;
      } else {
        double probability = trigramCounts.get(tagm2 + " " + tagm1 + " " + tag)
            / (double) bigramCounts.get(tagm2 + " " + tagm1);

        double logProbability = (Math.log(probability) / Math.log(2));
        return logProbability;
      }
    }
  }

  public Hashtable<ArrayList<String>, ArrayList<Double>> viterbi(
      String[] sentence) throws NumberFormatException, IOException {

    generatePiTable(sentence);

    ArrayList<String> tagSequence = new ArrayList<String>();
    ArrayList<Double> probabilitySequence = new ArrayList<Double>();

    for (int i = 0; i < sentence.length; i++) {
      int k = i + 1;

      double maxProbability = Integer.MIN_VALUE;
      String maxTag = "";

      for (String v : piTable.get(k).keySet()) {
        for (String u : piTable.get(k).get(v).keySet()) {
          double probability = piTable.get(k).get(v).get(u);
          if (probability > maxProbability) {
            maxProbability = probability;
            maxTag = v;
          }
        }
      }
      probabilitySequence.add(maxProbability);
      tagSequence.add(maxTag);
    }

    Hashtable<ArrayList<String>, ArrayList<Double>> returnObject = new Hashtable<ArrayList<String>, ArrayList<Double>>();
    returnObject.put(tagSequence, probabilitySequence);
    return returnObject;
  }

  public void writeProbabilities(File ner_dev) throws IOException {
    // reads off the highest log conditional probability for each word
    String fileName = ner_dev.getName().split("\\.")[0] + "_prediction.dat";
    File prediction = new File(fileName);

    BufferedReader bufferedReader = new BufferedReader(new FileReader(ner_dev));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
        prediction));
    String line;

    ArrayList<String> sentence = new ArrayList<String>();

    while ((line = bufferedReader.readLine()) != null) {

      if (line.isEmpty()) {
        String[] sentenceArray = new String[sentence.size()];
        for (int i = 0; i < sentence.size(); i++) {
          sentenceArray[i] = sentence.get(i);
        }

        sentence = new ArrayList<String>();

        Hashtable<ArrayList<String>, ArrayList<Double>> viterbiResults = viterbi(sentenceArray);

        for (int i = 0; i < sentenceArray.length; i++) {
          String output = sentenceArray[i] + " "
              + viterbiResults.keySet().iterator().next().get(i) + " "
              + viterbiResults.values().iterator().next().get(i);
          bufferedWriter.write(output + "\n");
        }
        bufferedWriter.write("\n");
      } else {
        sentence.add(line);
      }
    }
    bufferedReader.close();
    bufferedWriter.close();
  }

  /**
   * Generates a pi table that looks up using v then u, returns the pi value
   * associated. log probability.
   * 
   * @param sentence
   * @throws IOException
   */
  public void generatePiTable(String[] sentence) throws IOException {
    sentence = sentencePad(sentence);

    Set<String> tagSet = unigramCounts.keySet();

    piTable = new Hashtable<Integer, Hashtable<String, Hashtable<String, Double>>>();

    // k = 0
    String w = "";
    String u = "*";
    String v = "*";

    Hashtable<String, Double> k0u = new Hashtable<String, Double>();
    k0u.put(u, (double) 0);
    Hashtable<String, Hashtable<String, Double>> k0 = new Hashtable<String, Hashtable<String, Double>>();
    k0.put(v, k0u);
    piTable.put(0, k0);

    // k = 1
    w = "*";
    u = "*";

    Hashtable<String, Hashtable<String, Double>> k1 = new Hashtable<String, Hashtable<String, Double>>();

    for (String tag : tagSet) {

      Hashtable<String, Double> k1u = new Hashtable<String, Double>();
      v = tag;
      double pi = piTable.get(0).get(u).get(w) + trigramBigramRatio(w, u, v)
          + emissionParameters.getWordProbability(sentence[1], v);

      k1u.put(u, pi);
      k1.put(v, k1u);
    }
    piTable.put(1, k1);

    if (sentence.length > 2) {
      // k = 2
      w = "*";

      Hashtable<String, Hashtable<String, Double>> k2 = new Hashtable<String, Hashtable<String, Double>>();

      String word = sentence[2];

      for (String tag : tagSet) {
        Hashtable<String, Double> k2u = new Hashtable<String, Double>();
        v = tag;
        for (String tag2 : tagSet) {
          u = tag2;
          double pi = piTable.get(1).get(u).get(w)
              + trigramBigramRatio(w, u, v)
              + emissionParameters.getWordProbability(word, v);
          if (pi > 0) {
            pi = Integer.MIN_VALUE;
          }
          k2u.put(u, pi);
        }
        k2.put(v, k2u);
      }
      piTable.put(2, k2);
    }

    if (sentence.length > 3) {

      // k >= 3
      for (int k = 3; k < sentence.length; k++) {

        String word = sentence[k];

        Hashtable<String, Hashtable<String, Double>> krow = new Hashtable<String, Hashtable<String, Double>>();

        for (String tag : tagSet) {
          Hashtable<String, Double> ku = new Hashtable<String, Double>();
          v = tag;

          for (String tag2 : tagSet) {
            u = tag2;
            double maxPi = Integer.MIN_VALUE;

            for (String tag3 : tagSet) {
              w = tag3;

              double pi = piTable.get(k - 1).get(u).get(w)
                  + trigramBigramRatio(w, u, v)
                  + emissionParameters.getWordProbability(word, v);

              if (pi > maxPi) {
                maxPi = pi;
              }
            }
            ku.put(u, maxPi);
          }
          krow.put(v, ku);
        }
        piTable.put(k, krow);
      }
    }
  }

  public String[] sentencePad(String[] unpadded) {
    String[] sentencePad = new String[unpadded.length + 1];
    sentencePad[0] = "*";
    for (int i = 0; i < unpadded.length; i++) {
      sentencePad[i + 1] = unpadded[i];
    }

    return sentencePad;
  }

  public void examinePiTable() {
    for (Integer key : piTable.keySet()) {
      for (String v : piTable.get(key).keySet()) {
        for (String u : piTable.get(key).get(v).keySet()) {
          System.out.print(key + "\t");
          System.out.print(v + "\t");
          System.out.print(u + "\t");
          System.out.print(piTable.get(key).get(v).get(u) + "\n");
        }
      }
    }
  }
}

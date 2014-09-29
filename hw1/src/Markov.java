import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class Markov {

  public static final String GRAMTAG = "-GRAM";

  private Hashtable<String, Integer> unigramCounts;
  private Hashtable<String, Integer> bigramCounts;
  private Hashtable<String, Integer> trigramCounts;

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
  }

  public double trigramBigramRatio(String tagm2, String tagm1, String tag)
      throws IOException {

    if (!bigramCounts.containsKey(tagm2 + " " + tagm1)) {
      return -1;
    } else {
      if (!trigramCounts.containsKey(tagm2 + " " + tagm1 + " " + tag)) {
        return 0;
      } else {
        double probability = trigramCounts.get(tagm2 + " " + tagm1 + " " + tag)
            / (double) bigramCounts.get(tagm2 + " " + tagm1);
        double logProbability = (Math.log(probability) / Math.log(2));
        return logProbability;
      }
    }
  }
}

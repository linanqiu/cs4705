import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

public class EmissionParameters {

  public static final String RARETAG = "_RARE_";
  public static final String WORDTAG = "WORDTAG";

  public static void replaceRare(File ner_train, File ner_train_count)
      throws IOException {
    Hashtable<String, Integer> rares = new Hashtable<String, Integer>();

    // count all words since rare tags are non tag specific
    BufferedReader bufferedReader = new BufferedReader(new FileReader(
        ner_train_count));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      int count = Integer.parseInt(lineArray[0]);
      if (lineArray[1].equals(WORDTAG)) {
        if (rares.containsKey(lineArray[3])) {
          rares.put(lineArray[3], rares.get(lineArray[3]) + count);
        } else {
          rares.put(lineArray[3], count);
        }
      }
    }

    // eliminate all non-rare words
    Iterator<String> ite = rares.keySet().iterator();
    while (ite.hasNext()) {
      String key = ite.next();
      if (!(rares.get(key) < 5)) {
        ite.remove();
      }
    }

    bufferedReader.close();

    // replace rare words with RARETAG
    String fileName = ner_train.getName().split("\\.")[0] + "_replaced.dat";
    File replaced = new File(fileName);

    bufferedReader = new BufferedReader(new FileReader(ner_train));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(replaced));

    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      if (rares.containsKey(lineArray[0])) {
        line = RARETAG + " " + lineArray[1];
      }
      bufferedWriter.write(line + "\n");
    }

    bufferedReader.close();
    bufferedWriter.close();
  }

  public static void namedEntityTagger(File ner_train_replaced_count,
      File ner_dev) throws IOException {
    // total counts
    Hashtable<String, Integer> counts = new Hashtable<String, Integer>();
    // conditional counts
    Hashtable<String, Integer> conditionalCounts = new Hashtable<String, Integer>();

    BufferedReader bufferedReader = new BufferedReader(new FileReader(
        ner_train_replaced_count));
    String line;
    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      if (lineArray[1].equals(WORDTAG)) {
        int count = Integer.parseInt(lineArray[0]);
        if (counts.containsKey(lineArray[2])) {
          counts.put(lineArray[1], counts.get(lineArray[1]) + count);
        } else {
          counts.put(lineArray[1], count);
        }
      }
    }
  }
}

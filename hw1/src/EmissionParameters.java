import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

public class EmissionParameters {

  public static final String RARETAG = "_RARE_";
  public static final String WORDTAG = "WORDTAG";

  // maps a tag to its total count C(y_i)
  private Hashtable<String, Integer> tagCounts;

  // maps a word to its possible tags and log probabilities
  // x_i -> {log P(x_i | y_a), y_i}, {log P(x_i | y_b), y_b} etc
  private Hashtable<String, TreeMap<Double, String>> wordProbabilities;

  // maps a word to its possible log probabilities and tags. for lookup
  // convenience. abuse ram yay.
  // x_i -> {y_i, log P(x_i | y_a)}, {y_b, log P(x_i | y_b)} etc
  private Hashtable<String, Hashtable<String, Double>> wordProbabilitiesTags;

  /**
   * Sets the overall count of C(y_i) and C(x_i) of the entire object given a
   * certain count file. Rewrites over existing data.
   * 
   * Then sets wordProbabilities and wordProbabilitiesTags
   * 
   * @param ner_count
   *          count file generated from the python script
   * @throws NumberFormatException
   * @throws IOException
   */
  public void setCounts(File ner_count) throws NumberFormatException,
      IOException {
    tagCounts = new Hashtable<String, Integer>();

    // probabilities for each word ordered using treemap
    wordProbabilities = new Hashtable<String, TreeMap<Double, String>>();
    wordProbabilitiesTags = new Hashtable<String, Hashtable<String, Double>>();

    BufferedReader bufferedReader = new BufferedReader(
        new FileReader(ner_count));
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      if (lineArray[1].equals(WORDTAG)) {
        int count = Integer.parseInt(lineArray[0]);

        if (tagCounts.containsKey(lineArray[2])) {
          tagCounts.put(lineArray[2], tagCounts.get(lineArray[2]) + count);
        } else {
          tagCounts.put(lineArray[2], count);
        }
      }
    }

    bufferedReader.close();

    bufferedReader = new BufferedReader(new FileReader(ner_count));

    while ((line = bufferedReader.readLine()) != null) {
      String[] lineArray = line.split(" ");
      if (lineArray[1].equals(WORDTAG)) {
        int total = tagCounts.get(lineArray[2]);
        int count = Integer.parseInt(lineArray[0]);
        double probability = count / (double) total;
        double logProbability = (Math.log(probability) / Math.log(2));

        if (wordProbabilities.containsKey(lineArray[3])) {

          // wordprobabilities first
          TreeMap<Double, String> orderedProbabilities = wordProbabilities
              .get(lineArray[3]);
          orderedProbabilities.put(logProbability, lineArray[2]);
          wordProbabilities.put(lineArray[3], orderedProbabilities);

          // wordprobabilitiestags
          Hashtable<String, Double> probabilities = wordProbabilitiesTags
              .get(lineArray[3]);
          probabilities.put(lineArray[2], logProbability);
          wordProbabilitiesTags.put(lineArray[3], probabilities);

        } else {

          // wordprobabilitiesfirst
          TreeMap<Double, String> orderedProbabilities = new TreeMap<Double, String>();
          orderedProbabilities.put(logProbability, lineArray[2]);
          wordProbabilities.put(lineArray[3], orderedProbabilities);

          // wordprobabilitiestags
          Hashtable<String, Double> probabilities = new Hashtable<String, Double>();
          probabilities.put(lineArray[2], logProbability);
          wordProbabilitiesTags.put(lineArray[3], probabilities);
        }
      }
    }

    bufferedReader.close();
  }

  /**
   * Replaces rare words with RARETAG
   * 
   * @param ner_train
   *          training data
   * @param ner_count
   *          count data of the training set
   * @throws IOException
   */
  public void replaceRare(File ner_train, File ner_count) throws IOException {
    Hashtable<String, Integer> rares = new Hashtable<String, Integer>();

    // count all words since rare tags are non tag specific
    BufferedReader bufferedReader = new BufferedReader(
        new FileReader(ner_count));
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
        if (lineArray.length > 1) {
          line = RARETAG + " " + lineArray[1];
        } else {
          line = RARETAG;
        }
      }
      bufferedWriter.write(line + "\n");
    }

    bufferedReader.close();
    bufferedWriter.close();
  }

  /**
   * Gets the emission log probability of a word and a tag. If word doesn't
   * appear, replaces word with RARETAG
   * 
   * @param word
   *          word x to be looked up
   * @param tag
   *          tag y to be looked up
   * @return maximum log probability
   */
  public double getWordProbability(String word, String tag) {
    Hashtable<String, Double> probabilities;

    if (wordProbabilitiesTags.containsKey(word)) {
      probabilities = wordProbabilitiesTags.get(word);
    } else {
      probabilities = wordProbabilitiesTags.get(RARETAG);
    }

    if (probabilities.containsKey(tag)) {
      return probabilities.get(tag);
    } else {
      return Integer.MIN_VALUE;
    }
  }

  /**
   * Get the maximum log probability for a word. If word doesn't appear, replace
   * with RARETAG
   * 
   * @param word
   *          word x to be looked up
   * @return maximum log probability
   */
  public double getMaxWordProbability(String word) {
    if (wordProbabilities.containsKey(word)) {
      Entry<Double, String> entry = wordProbabilities.get(word).lastEntry();
      double probability = entry.getKey();
      return probability;
    } else {
      Entry<Double, String> entry = wordProbabilities.get(RARETAG).lastEntry();
      double probability = entry.getKey();
      return probability;
    }
  }

  /**
   * Gets the tag y corresponding to the maximum probability for a word. If word
   * doesn't appear, replace with RARETAG
   * 
   * @param word
   *          word x to be looked up
   * @return tag corresponding to maximum log probability
   */
  public String getMaxWordProbabilityTag(String word) {
    if (wordProbabilities.containsKey(word)) {
      Entry<Double, String> entry = wordProbabilities.get(word).lastEntry();
      String type = entry.getValue();
      return type;
    } else {
      Entry<Double, String> entry = wordProbabilities.get(RARETAG).lastEntry();
      String type = entry.getValue();
      return type;
    }
  }

  /**
   * Writes probabilities to file based on the development data. Writes to
   * <ner_dev file name w/o extension>_prediction.dat
   * 
   * @param ner_dev
   *          development data clean
   * @throws IOException
   */
  public void writeProbabilities(File ner_dev) throws IOException {
    // reads off the highest log conditional probability for each word
    String fileName = ner_dev.getName().split("\\.")[0] + "_prediction.dat";
    File prediction = new File(fileName);

    BufferedReader bufferedReader = new BufferedReader(new FileReader(ner_dev));
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
        prediction));
    String line;

    while ((line = bufferedReader.readLine()) != null) {
      // skips over blank lines

      if (!(line.length() < 1)) {
        double probability = getMaxWordProbability(line);
        String tag = getMaxWordProbabilityTag(line);

        line = line + " " + tag + " " + probability;

      }
      bufferedWriter.write(line + "\n");
    }
    bufferedReader.close();
    bufferedWriter.close();
  }

  public boolean isRare(String word) {
    return !wordProbabilities.containsKey(word);
  }
  
  public String rareWordBucket(String word) {
    
  }
}

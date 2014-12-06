package hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tagger using perceptron
 * 
 * @author linanqiu
 * @file_name Tagger.java
 */
public class Tagger {

  public static final String BIGRAM = "BIGRAM";
  public static final String TAG = "TAG";
  public static final String SUFFIX = "SUFFIX";

  public static final int PERCEPTRON_COUNT = 5;
  public static final int SUFFIX_COUNT = 3;
  public static final int PREFIX_COUNT = 4;

  public static final String TWODIGITNUM = "TWODIGITNUM";
  public static final String FOURDIGITNUM = "FOURDIGITNUM";
  public static final String CONTAINSDIGITANDALPHA = "CONTAINSDIGITANDALPHA";
  public static final String CONTAINSDIGITANDDASH = "CONTAINSDIGITANDDASH";
  public static final String CONTAINSDIGITANDSLASH = "CONTAINSDIGITANDSLASH";
  public static final String CONTAINSDIGITANDCOMMA = "CONTAINSDIGITANDCOMMA";
  public static final String CONTAINSDIGITANDPERIOD = "CONTAINSDIGITANDPERIOD";
  public static final String OTHERNUM = "OTHERNUM";
  public static final String ALLCAPS = "ALLCAPS";
  public static final String INITCAP = "INITCAP";
  public static final String LOWERCASE = "LOWERCASE";
  public static final String OTHER = "OTHER";

  private HashMap<String, Double> v; // v vector
  private ArrayList<ArrayList<String>> sentences; // read sentence into RAM for
                                                  // faster access

  // python wrappers
  private PyTaggerHistoryGenerator pyTaggerHistoryGenerator;
  private PyTaggerDecoder pyTaggerDecoder;

  public Tagger(String pyTaggerHistoryGeneratorName, String pyTaggerDecoderName)
      throws IOException {
    pyTaggerHistoryGenerator = new PyTaggerHistoryGenerator(
        pyTaggerHistoryGeneratorName);
    pyTaggerDecoder = new PyTaggerDecoder(pyTaggerDecoderName);
  }

  /**
   * Sets the v vector using a model file
   * 
   * @param tagModel
   *          model file for v vector
   * @throws NumberFormatException
   * @throws IOException
   */
  public void setV(File tagModel) throws NumberFormatException, IOException {
    v = new HashMap<String, Double>();

    BufferedReader bufferedReader = new BufferedReader(new FileReader(tagModel));

    String line;

    while ((line = bufferedReader.readLine()) != null) {
      String[] elements = line.split("\\s");
      v.put(elements[0], Double.valueOf(elements[1]));
    }

    bufferedReader.close();
  }

  /**
   * Sets the tag_dev.dat sentences
   * 
   * @param tagDev
   *          tag_dev.dat
   * @throws IOException
   */
  public void setSentences(File tagDev) throws IOException {
    sentences = new ArrayList<ArrayList<String>>();
    BufferedReader bufferedReader = new BufferedReader(new FileReader(tagDev));

    String line;

    ArrayList<String> tempSentence = new ArrayList<String>();

    while ((line = bufferedReader.readLine()) != null) {
      if (line.length() != 0) {
        tempSentence.add(line);
      } else {
        sentences.add(tempSentence);
        tempSentence = new ArrayList<String>();
      }
    }

    bufferedReader.close();
  }

  /**
   * Tags, producing an output tag_dev.out
   * 
   * @param tagDevOutput
   *          path. should be tag_dev.out
   * @throws IOException
   */
  public void tag(File tagDevOutput) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
        tagDevOutput));

    for (ArrayList<String> sentence : sentences) {

      ArrayList<String> highestTaggings = bigF(sentence);

      for (String highestTag : highestTaggings) {
        String[] elements = highestTag.split("\\s+");
        int wordIndex = Integer.parseInt(elements[0]) - 1;
        if (wordIndex < sentence.size()) {
          String tagCurrent = elements[2];
          bufferedWriter.append(sentence.get(wordIndex) + " " + tagCurrent
              + "\n");
        }
      }

      bufferedWriter.append("\n");
    }
    bufferedWriter.close();
  }

  /**
   * Gets highest tagging for a sentence using v
   * 
   * @param sentence
   *          a sentence of [TAG, TAG, WORD]s
   * @return highest value tagging of the sentence
   * @throws IOException
   */
  public ArrayList<String> bigF(ArrayList<String> sentence) throws IOException {
    String argument = sentenceToArgument(sentence);
    ArrayList<String> enumHists = pyTaggerHistoryGenerator.pyEnum(argument);
    LinkedHashMap<String, Double> histsWeighted = new LinkedHashMap<String, Double>();
    for (String hist : enumHists) {
      String[] elements = hist.split("\\s+");

      double totalWeight = 0;

      ArrayList<String> features = generateFeatures(elements, sentence);

      for (String feature : features) {
        totalWeight += v.containsKey(feature) ? v.get(feature) : 0;
      }

      histsWeighted.put(hist, totalWeight);
    }

    // weight histories
    String histArgument = histsToArgument(histsWeighted);
    ArrayList<String> highestTaggings = pyTaggerDecoder.pyHistory(histArgument);
    return highestTaggings;
  }

  /**
   * Generates suffixes given a word
   * 
   * @param word
   *          word to generate suffix
   * @return suffixes
   */
  protected String[] generateSuffixes(String word) {
    int maxSuffixLength = Math.min(word.length(), SUFFIX_COUNT);

    String[] suffixes = new String[maxSuffixLength];
    for (int i = 0; i < maxSuffixLength; i++) {
      suffixes[i] = word.substring(word.length() - i, word.length());
    }
    return suffixes;
  }

  /**
   * Generates prefixes given a word
   * 
   * @param word
   *          word to generate prefix
   * @return prefixes
   */
  protected String[] generatePrefixes(String word) {
    int maxPrefixLength = Math.min(word.length(), PREFIX_COUNT);

    String[] prefixes = new String[maxPrefixLength];
    for (int i = 0; i < maxPrefixLength; i++) {
      prefixes[i] = word.substring(0, i + 1);
    }
    return prefixes;
  }

  /**
   * Converts a sentence to an argument. Mainly by adding \n after each line.
   * 
   * @param sentence
   *          sentence of [TAG, TAG, WORD]
   * @return argument to be fed to python files
   */
  public String sentenceToArgument(ArrayList<String> sentence) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String word : sentence) {
      stringBuilder.append(word + "\n");
    }

    return stringBuilder.toString().trim();
  }

  /**
   * Converts hists to arguments
   * 
   * @param enumHists
   *          set of [TAG TAG WORD, Score]
   * @return formatted argument for scoring
   */
  public String histsToArgument(LinkedHashMap<String, Double> enumHists) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String hist : enumHists.keySet()) {
      stringBuilder.append(hist + " " + enumHists.get(hist) + "\n");
    }

    return stringBuilder.toString().trim();
  }

  /**
   * Perceptron algorithm
   * 
   * @param tagTrain
   *          tag_train.dat
   * @param vOutput
   *          output file for v
   * @throws IOException
   */
  public void perceptron(File tagTrain, File vOutput) throws IOException {
    ArrayList<LinkedHashMap<String, String>> trainingSentences = new ArrayList<LinkedHashMap<String, String>>();

    BufferedReader bufferedReader = new BufferedReader(new FileReader(tagTrain));

    String line;

    LinkedHashMap<String, String> tempSentence = new LinkedHashMap<String, String>();

    while ((line = bufferedReader.readLine()) != null) {
      if (line.length() != 0) {
        String[] elements = line.split("\\s+");
        tempSentence.put(elements[0], elements[1]);
      } else {
        trainingSentences.add(tempSentence);
        tempSentence = new LinkedHashMap<String, String>();
      }
    }

    bufferedReader.close();

    v = new HashMap<String, Double>();

    for (int perceptronCount = 0; perceptronCount < PERCEPTRON_COUNT; perceptronCount++) {
      System.out.println("Perceptron Iteration: " + (perceptronCount + 1));
      for (int i = 0; i < trainingSentences.size(); i++) {
        LinkedHashMap<String, String> sentence = trainingSentences.get(i);

        ArrayList<String> gold = pyTaggerHistoryGenerator
            .pyGold(sentenceToArgument(trainingSentenceToListWordTag(sentence)));
        ArrayList<String> z = bigF(trainingSentenceToListWord(sentence));

        updateV(gold, z, trainingSentenceToListWord(sentence));
      }
    }

    writeV(vOutput);
  }

  /**
   * Writes v vector to disk
   * 
   * @param vOutput
   *          v file on disk
   * @throws IOException
   */
  private void writeV(File vOutput) throws IOException {
    BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(vOutput));
    for (String feature : v.keySet()) {
      bufferedWriter.write(feature + " " + v.get(feature) + "\n");
    }
    bufferedWriter.close();
  }

  /**
   * Because training files come in WORD TAG, and not just WORD, we need to
   * handle this specially. The two methods trainingSentenceToListWordTag() and
   * trainingSentenceToListWord() are both utility methods to do that
   * 
   * @param trainingSentence
   *          represents a sentence from the training file, key being TAG, value
   *          being WORD
   * @return ArrayList of WORD + TAG
   */
  private ArrayList<String> trainingSentenceToListWordTag(
      LinkedHashMap<String, String> trainingSentence) {
    ArrayList<String> sentence = new ArrayList<String>();
    for (String word : trainingSentence.keySet()) {
      sentence.add(word + " " + trainingSentence.get(word));
    }
    return sentence;
  }

  /**
   * Another utility method. Returns an arraylist of just the keys (TAGs) of the
   * trainng sentence
   * 
   * @param trainingSentence
   *          represents a sentence from the training file, key being TAG, value
   *          being WORD
   * @return Arraylist of WORDs
   */
  private ArrayList<String> trainingSentenceToListWord(
      LinkedHashMap<String, String> trainingSentence) {
    return new ArrayList<String>(trainingSentence.keySet());
  }

  /**
   * Generates the feature vector
   * 
   * @param elements
   *          TAG, TAG, WORD
   * @param sentence
   *          entire sentence
   * @return feature vector
   */
  protected ArrayList<String> generateFeatures(String[] elements,
      ArrayList<String> sentence) {
    int wordIndex = Integer.parseInt(elements[0]) - 1;
    String word = sentence.get(wordIndex);
    String tagPrev = elements[1];
    String tagCurrent = elements[2];

    String bigramFeature = BIGRAM + ":" + tagPrev + ":" + tagCurrent;
    String tagFeature = TAG + ":" + word + ":" + tagCurrent;

    ArrayList<String> features = new ArrayList<String>();
    features.add(bigramFeature);
    features.add(tagFeature);

    String[] suffixes = generateSuffixes(word);
    for (String suffix : suffixes) {
      String suffixFeature = SUFFIX + ":" + suffix + ":" + tagCurrent;
      features.add(suffixFeature);
    }

    // additional features start here
    // words
    String currentWordFeature = "CURRENTWORD" + ":" + word + ":" + tagCurrent;
    features.add(currentWordFeature);
    if (wordIndex > 0) {
      String previousWordFeature = "PREVIOUSWORD" + ":"
          + sentence.get(wordIndex - 1) + ":" + tagCurrent;
      features.add(previousWordFeature);
    }
    if (wordIndex > 1) {
      String wordTwoBackFeature = "WORDTWOBACK" + ":"
          + sentence.get(wordIndex - 2) + ":" + tagCurrent;
      features.add(wordTwoBackFeature);
    }
    if (wordIndex < sentence.size() - 1) {
      String nextWordFeature = "NEXTWORD" + ":" + sentence.get(wordIndex + 1)
          + ":" + tagCurrent;
      features.add(nextWordFeature);
    }
    if (wordIndex < sentence.size() - 2) {
      String wordTwoAheadFeature = "WORDTWOAHEAD" + ":"
          + sentence.get(wordIndex + 2) + ":" + tagCurrent;
      features.add(wordTwoAheadFeature);
    }

    // word bigrams
    if (wordIndex > 1) {
      String wordBigram1Feature = "WORDBIGRAM1" + ":"
          + sentence.get(wordIndex - 2) + ":" + sentence.get(wordIndex - 1)
          + ":" + tagCurrent;
      features.add(wordBigram1Feature);
    }
    if (wordIndex > 0) {
      String wordBigram2Feature = "WORDBIGRAM2" + ":"
          + sentence.get(wordIndex - 1) + ":" + sentence.get(wordIndex) + ":"
          + tagCurrent;
      features.add(wordBigram2Feature);
    }
    if (wordIndex < sentence.size() - 1) {
      String wordBigram3Feature = "WORDBIGRAM3" + ":" + sentence.get(wordIndex)
          + ":" + sentence.get(wordIndex + 1) + ":" + tagCurrent;
      features.add(wordBigram3Feature);
    }
    if (wordIndex < sentence.size() - 2) {
      String wordBigram4Feature = "WORDBIGRAM4" + ":"
          + sentence.get(wordIndex + 1) + ":" + sentence.get(wordIndex + 2)
          + ":" + tagCurrent;
      features.add(wordBigram4Feature);
    }

    // prefix
    String[] prefixes = generatePrefixes(word);
    for (String prefix : prefixes) {
      String prefixFeature = "PREFIX" + ":" + prefix + ":" + tagCurrent;
      features.add(prefixFeature);
    }

    // bickel's buckets to derive form
    String rareWordFeature = wordFormBucket(word) + ":" + tagCurrent;
    features.add(rareWordFeature);

    return features;
  }

  /**
   * Updates v features using modifier term
   * 
   * @param features
   * @param modifier
   */
  private void updateVFeatures(ArrayList<String> features, double modifier) {
    for (String feature : features) {
      if (v.containsKey(feature)) {
        v.put(feature, v.get(feature) + modifier);
      } else {
        v.put(feature, modifier);
      }
    }
  }

  /**
   * Updates v parameter given a gold feature and a z feature
   * 
   * @param gold
   * @param z
   * @param sentence
   */
  private void updateV(ArrayList<String> gold, ArrayList<String> z,
      ArrayList<String> sentence) {
    for (int i = 0; i < gold.size(); i++) {
      String[] goldElements = gold.get(i).split("\\s+");
      String[] zElements = z.get(i).split("\\s+");

      ArrayList<String> goldFeatures = generateFeatures(goldElements, sentence);
      ArrayList<String> zFeatures = generateFeatures(zElements, sentence);

      updateVFeatures(goldFeatures, 1);
      updateVFeatures(zFeatures, -1);
    }
  }

  /**
   * Word form sorting
   * 
   * @param word
   * @return
   */
  protected String wordFormBucket(String word) {
    Pattern pattern = Pattern.compile("^[\\,\\.\\!\\?]+$");
    Matcher matcher = pattern.matcher(word);

    if (matcher.find()) {
      return OTHER;
    }

    pattern = Pattern.compile("^\\d{2}$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return TWODIGITNUM;
    }

    pattern = Pattern.compile("^\\d{4}$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return FOURDIGITNUM;
    }

    pattern = Pattern.compile("^[0-9]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return OTHERNUM;
    }

    pattern = Pattern.compile("^[A-Z]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return ALLCAPS;
    }

    pattern = Pattern.compile("^[A-Z][a-z]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return INITCAP;
    }

    pattern = Pattern.compile("^[a-z]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return LOWERCASE;
    }

    pattern = Pattern.compile("^[0-9\\-]*[A-z]+[0-9\\-]*[A-z]*$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return CONTAINSDIGITANDALPHA;
    }

    pattern = Pattern.compile("^[0-9\\-]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return CONTAINSDIGITANDDASH;
    }

    pattern = Pattern.compile("^[0-9\\/]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return CONTAINSDIGITANDSLASH;
    }

    pattern = Pattern.compile("^[0-9\\.]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return CONTAINSDIGITANDPERIOD;
    }

    pattern = Pattern.compile("^[0-9\\,\\.]+$");
    matcher = pattern.matcher(word);

    if (matcher.find()) {
      return CONTAINSDIGITANDCOMMA;
    }

    return OTHER;
  }

  /**
   * Closes all python processes
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    pyTaggerHistoryGenerator.close();
    pyTaggerDecoder.close();
  }
}

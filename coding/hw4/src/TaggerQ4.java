

import java.io.IOException;
import java.util.ArrayList;

/**
 * Watered down wrapper for the purpose of Q4
 * 
 * @author linanqiu
 * @file_name TaggerQ4.java
 */
public class TaggerQ4 extends Tagger {

  public TaggerQ4(String pyTaggerHistoryGeneratorName,
      String pyTaggerDecoderName) throws IOException {
    super(pyTaggerHistoryGeneratorName, pyTaggerDecoderName);
    // TODO Auto-generated constructor stub
  }

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

    return features;
  }
}

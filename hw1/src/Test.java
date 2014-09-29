import java.io.File;
import java.io.IOException;

public class Test {
  public static void main(String[] args) {
    try {

      // Q4.1
      EmissionParameters emissionParameters = new EmissionParameters();
      emissionParameters.setCounts(new File("ner.counts"));
      double emission = emissionParameters.getWordProbability("Germany",
          "I-LOC");
      // these two should be the same
      System.out.println(emission);
      System.out.println(Math.log(142 / (double) 8286) / Math.log(2));

      // Q4.2
      // emissionParameters.replaceRare(new File("ner_train.dat"), new File(
      // "ner.counts"));

      // Q4.3
      emissionParameters.setCounts(new File("ner_replaced.count"));
      emissionParameters.writeProbabilities(new File("ner_dev.dat"));

      // double markov = Markov.markov("I-PER", "I-PER", "I-PER", new File(
      // "ner_replaced.count"));
      // System.out.println(markov);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

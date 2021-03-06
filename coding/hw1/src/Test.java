import java.io.File;

public class Test {
  public static void main(String[] args) {
    try {

      // Q4.1
//       EmissionParameters emissionParameters = new EmissionParameters();
//       emissionParameters.setCounts(new File("ner.counts"));
//       double emission = emissionParameters.getWordProbability("Germany",
//       "I-LOC");
      // these two should be the same
      // System.out.println(emission);
      // System.out.println(Math.log(142 / (double) 8286) / Math.log(2));

      // Q4.2
      // emissionParameters.replaceRare(new File("ner_train.dat"), new File(
      // "ner.counts"));

      // you will have to run the python counter script here

      // Q4.3
      // emissionParameters.setCounts(new File("ner_replaced.count"));
      // emissionParameters.writeProbabilities(new File("ner_dev.dat"));

      // Q5.1
      // Markov markov = new Markov();
      // markov.readCounts(new File("ner_replaced.count"));
      // double ratio = markov.trigramBigramRatio("I-ORG", "I-ORG", "O");
      // these two should be the same
      // System.out.println(ratio);mar
      // System.out.println(Math.log(2400 / (double) 3704) / Math.log(2));

      // Q5.2
      // markov.writeProbabilities(new File("ner_dev.dat"));

      // Q6
      EmissionParameters emissionParameters = new EmissionParameters();
      emissionParameters.replaceRare(new File("ner_train.dat"), new File(
          "ner.counts"));
      // run python counter before proceeding
      Markov markov = new Markov();
      markov.readCounts(new File("ner_replaced.count"));
      markov.writeProbabilities(new File("ner_dev.dat"));

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

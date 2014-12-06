import java.io.File;

public class TaggerTest {

  public static void main(String[] args) throws Exception {

    if (args[0].equals("4")) {
      Tagger tagger = new TaggerQ4("tagger_history_generator.py",
          "tagger_decoder.py");
      tagger.setV(new File("tag.model"));
      tagger.setSentences(new File("tag_dev.dat"));
      tagger.tag(new File("tag_dev_q4.out"));
    } else if (args[0].equals("5")) {
      Tagger tagger = new TaggerQ5("tagger_history_generator.py",
          "tagger_decoder.py");
      tagger.perceptron(new File("tag_train.dat"), new File(
          "suffix_tagger.model"));
      tagger.setSentences(new File("tag_dev.dat"));
      tagger.tag(new File("tag_dev_q5.out"));
      tagger.close();
    } else if (args[0].equals("q6")) {
      Tagger tagger = new Tagger("tagger_history_generator.py",
          "tagger_decoder.py");
      tagger.perceptron(new File("tag_train.dat"), new File(
          "additional_tagger.model"));
      tagger.setSentences(new File("tag_dev.dat"));
      tagger.tag(new File("tag_dev_q6.out"));
      tagger.close();
    }
  }
}

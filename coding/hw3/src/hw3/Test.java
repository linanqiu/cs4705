package hw3;

import java.io.File;
import java.io.IOException;

public class Test {
  public static void main(String[] args) throws IOException,
      ClassNotFoundException {
    final long startTime = System.currentTimeMillis();

    // IBM1 ibm1 = new IBM1(new File("corpus.de"), new File("corpus.en"));
    // ibm1.deserializeT(new File("ibm1_tserialize"));
    // ibm1.initializeT();
    // ibm1.expMax();
    // ibm1.devwordsForeignRank(new File("devwords.txt"));
    // ibm1.findAlignments();
    // ibm1.serializeT(new File("ibm1_tserialize"));

    // IBM2 ibm2 = new IBM2(new File("corpus.de"), new File("corpus.en"));
    // ibm2.deserializeT(new File("ibm2_tserialize"));
    // ibm2.deserializeQ(new File("ibm2_qserialize"));
    // // ibm2.initializeT();
    // // ibm2.initializeQ();
    // // ibm2.expMax();
    // // ibm2.serializeT(new File("ibm2_tserialize"));
    // // ibm2.serializeQ(new File("ibm2_qserialize"));
    // ibm2.findAlignments();

    Unscramble unscramble = new Unscramble(new File("scrambled.en"), new File(
        "original.de"), new File("corpus.de"), new File("corpus.en"), true);
    unscramble.unscramble();
    unscramble.writeUnscrambled();
    // unscramble.testIndex();
    // unscramble.tuneNegative();

    final long endTime = System.currentTimeMillis();
    System.out.println("Total execution time: " + (endTime - startTime));
  }
}

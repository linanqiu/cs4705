import java.io.File;
import java.io.IOException;

/**
 * Test class for IBM1, IBM2 and Unscramble
 * 
 * Currently runs the entire suite required for Q4, Q5 and Q6
 * 
 * @author linanqiu
 * @file_name Test.java
 */
public class Test {
  public static void main(String[] args) throws IOException,
      ClassNotFoundException {
    final long startTime = System.currentTimeMillis();

    System.out.println("Q4");
    IBM1 ibm1 = new IBM1(new File("corpus.de"), new File("corpus.en"));
    // ibm1.deserializeT(new File("ibm1_tserialize"));
    ibm1.initializeT();
    ibm1.expMax();
    ibm1.devwordsForeignRank(new File("devwords.txt"));
    ibm1.findAlignments();
    // ibm1.serializeT(new File("ibm1_tserialize"));

    System.out.println("Q5");
    IBM2 ibm2 = new IBM2(new File("corpus.de"), new File("corpus.en"));
    ibm2.setT(ibm1.getT());
    // ibm2.deserializeT(new File("ibm2_tserialize"));
    // ibm2.deserializeQ(new File("ibm2_qserialize"));
    // // ibm2.initializeT();
    ibm2.initializeQ();
    ibm2.expMax();
    // // ibm2.serializeT(new File("ibm2_tserialize"));
    // // ibm2.serializeQ(new File("ibm2_qserialize"));
    ibm2.findAlignments();

    System.out.println("Q6");
    Unscramble unscramble = new Unscramble(new File("scrambled.en"), new File(
        "original.de"), new File("corpus.de"), new File("corpus.en"), ibm2);
    unscramble.unscramble();
    unscramble.writeUnscrambled();
  }
}

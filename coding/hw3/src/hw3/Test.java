package hw3;

import java.io.File;
import java.io.IOException;

public class Test {
  public static void main(String[] args) throws IOException {
    final long startTime = System.currentTimeMillis();

    IBM1 ibm1 = new IBM1(new File("corpus.de"), new File("corpus.en"));
    ibm1.initializeT();
    final long endTime = System.currentTimeMillis();

    System.out.println("Total execution time: " + (endTime - startTime));
  }
}

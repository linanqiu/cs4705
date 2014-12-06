package hw4;

import java.io.File;
import java.util.Scanner;

/**
 * Checks wrong lines
 * 
 * @author linanqiu
 * @file_name Eval.java
 */
public class Eval {

  public static void eval(File key, File out) throws Exception {
    Scanner scannerKey = new Scanner(key);
    Scanner scannerOut = new Scanner(out);

    while (scannerKey.hasNext() && scannerOut.hasNext()) {
      String keyLine = scannerKey.nextLine();
      String outLine = scannerOut.nextLine();

      if (keyLine.equals(outLine) && keyLine.length() == 0) {

      } else {

        String[] keyElements = keyLine.split("\\s+");
        String[] outElements = outLine.split("\\s+");

        if (!keyElements[0].equals(outElements[0])) {
          scannerKey.close();
          scannerOut.close();
          throw new Exception("Word Mismatch");
        } else {
          if (!keyElements[1].equals(outElements[1])) {
            System.out.println("Key:\t" + keyElements[0] + "\t"
                + keyElements[1]);
            System.out.println("Out:\t" + outElements[0] + "\t"
                + outElements[1]);
            System.out.println();
          }
        }
      }
    }

    scannerKey.close();
    scannerOut.close();
  }
}

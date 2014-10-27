import java.io.File;
import java.io.IOException;

public class Test {
  public static final String USAGE_REPLACE = "pcfg.jar: usage: java -jar pcfg.jar replace <cfg.counts> <parse_train.dat> <parse_train_replaced.dat>";
  public static final String USAGE_CKY = "pcfg.jar: usage: java -jar pcfg.jar cky <cfg_replaced.counts> <parse_dev.dat> <parse_dev_predict.dat>";

  public static void main(String[] args) throws NumberFormatException,
      IOException {

    if (args.length == 4) {
      if (args[0].equals("replace")) {
        PCFG pcfg = new PCFG();
        System.out.println("pcfg.jar: Starting replacement");
        pcfg.setCounts(new File(args[1]));
        pcfg.setTrees(new File(args[2]));
        pcfg.replaceRare();
        pcfg.writeTree(new File(args[3]));
        System.out.println("pcfg.jar: Replacement done. Run count on "
            + args[3]);
      } else if (args[0].equals("cky")) {
        PCFG pcfg = new PCFG();
        System.out.println("pcfg.jar: Starting CKY");
        pcfg.setCounts(new File(args[1]));
        pcfg.predict(new File(args[2]), new File(args[3]));
        System.out.println("pcfg.jar CKY done");
      } else {
        System.out.println(USAGE_REPLACE);
        System.out.println(USAGE_CKY);
      }
    } else {

      System.out.println(USAGE_REPLACE);
      System.out.println(USAGE_CKY);
    }
    //
    // // PCFG pcfg = new PCFG();
    // // pcfg.setCounts(new File("cfg_replaced.counts"));
    // // pcfg.setTrees(new File("parse_train.dat"));
    // // pcfg.replaceRare();
    // // pcfg.writeTree(new File("parse_train_replaced.dat"));
    //
    // // System.out.println(pcfg.binaryProbability("VP VERB NP+PRON"));
    // // System.out.println(pcfg.binaryProbability("VP ADVP+ADV ADVP+PRT"));
    // //
    // // System.out.println(pcfg.unaryProbability("VERB The"));
    // // System.out.println(pcfg.unaryProbability("X+X The"));
    // // System.out.println(pcfg.unaryProbability("DET The"));
    //
    // // pcfg.predict(new File("parse_dev.dat"), new
    // // File("parse_dev_predict.dat"));
    //
    // PCFG pcfgVert = new PCFG();
    // // pcfgVert.setCounts(new File("cfg_vert.counts"));
    // // pcfgVert.setTrees(new File("parse_train_vert.dat"));
    // // pcfgVert.replaceRare();
    // // pcfgVert.writeTree(new File("parse_train_vert_replaced.dat"));
    // pcfgVert.setCounts(new File("cfg_vert_replaced.counts"));
    // pcfgVert.predict(new File("parse_dev.dat"), new File(
    // "parse_dev_vert_predict.dat"));
  }
}

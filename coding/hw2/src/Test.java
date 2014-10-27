import java.io.File;
import java.io.IOException;

public class Test {
  public static void main(String[] args) throws NumberFormatException,
      IOException {
    // PCFG pcfg = new PCFG();
    // pcfg.setCounts(new File("cfg_replaced.counts"));
    // pcfg.setTrees(new File("parse_train.dat"));
    // pcfg.replaceRare();
    // pcfg.writeTree(new File("parse_train_replaced.dat"));

    // System.out.println(pcfg.binaryProbability("VP VERB NP+PRON"));
    // System.out.println(pcfg.binaryProbability("VP ADVP+ADV ADVP+PRT"));
    //
    // System.out.println(pcfg.unaryProbability("VERB The"));
    // System.out.println(pcfg.unaryProbability("X+X The"));
    // System.out.println(pcfg.unaryProbability("DET The"));

    // pcfg.predict(new File("parse_dev.dat"), new
    // File("parse_dev_predict.dat"));

    PCFG pcfgVert = new PCFG();
    // pcfgVert.setCounts(new File("cfg_vert.counts"));
    // pcfgVert.setTrees(new File("parse_train_vert.dat"));
    // pcfgVert.replaceRare();
    // pcfgVert.writeTree(new File("parse_train_vert_replaced.dat"));
    pcfgVert.setCounts(new File("cfg_vert_replaced.counts"));
    pcfgVert.predict(new File("parse_dev.dat"), new File(
        "parse_dev_vert_predict.dat"));
  }
}

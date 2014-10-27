import java.util.Hashtable;

import org.json.JSONArray;

public class TreeFunctionReplaceRare implements TreeFunction {

  private Hashtable<String, Integer> rareWordBucket;

  public TreeFunctionReplaceRare(Hashtable<String, Integer> rareWordBucket) {
    this.rareWordBucket = rareWordBucket;
  }

  @Override
  public JSONArray treeFunction(JSONArray tree) {
    if (rareWordBucket.containsKey(tree.getString(1))) {
      tree.put(1, PCFG.RARETAG);
    }
    return tree;
  }
}

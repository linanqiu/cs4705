import java.util.Hashtable;

import org.json.JSONArray;

/**
 * Wrapper for a traversal function because we're not using Java 1.8
 * 
 * @author linanqiu
 * @file_name TreeFunctionReplaceRare.java
 */
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

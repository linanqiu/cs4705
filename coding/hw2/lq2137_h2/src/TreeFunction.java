import org.json.JSONArray;

/**
 * Wrapper because we're not using Java 1.8 with lambdas
 * 
 * @author linanqiu
 * @file_name TreeFunction.java
 */
public interface TreeFunction {

  public JSONArray treeFunction(JSONArray tree);
}

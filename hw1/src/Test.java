import java.io.File;
import java.io.IOException;

public class Test {
	public static void main(String[] args) {
		try {
			EmissionParameters.replaceRare(new File("ner_train.dat"), new File(
					"ner.counts"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

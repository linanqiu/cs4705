package hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Wrapper for tagger_decoder.py
 * 
 * @author linanqiu
 * @file_name TaggerDecoder.java
 */
/**
 * @author linanqiu
 * @file_name PyTaggerDecoder.java
 */
/**
 * @author linanqiu
 * @file_name PyTaggerDecoder.java
 */
public class PyTaggerDecoder {
  public static final String PYTHON = "python";
  public static final String HISTORY = "HISTORY";

  private ProcessBuilder historyProcessBuilder;
  private Process historyProcess;
  private BufferedReader historyReader;
  private BufferedWriter historyWriter;

  /**
   * Starts process and listens
   * 
   * @param tagger_decoder_py
   * @throws IOException
   */
  public PyTaggerDecoder(String tagger_decoder_py) throws IOException {
    historyProcessBuilder = new ProcessBuilder(PYTHON, tagger_decoder_py,
        HISTORY);
    historyProcess = historyProcessBuilder.start();
    historyReader = new BufferedReader(new InputStreamReader(
        historyProcess.getInputStream()));
  }

  /**
   * Sends argument to tagger_history_generator.py and receives output
   * 
   * @param argument
   *          arguments for the python process
   * @return output from python process
   * @throws IOException
   */
  public ArrayList<String> pyHistory(String argument) throws IOException {
    historyWriter = new BufferedWriter(new OutputStreamWriter(
        historyProcess.getOutputStream()));

    historyWriter.write(argument);
    historyWriter.write("\n\n");
    historyWriter.flush();

    String output;
    ArrayList<String> outputHist = new ArrayList<String>();

    while ((output = historyReader.readLine()).length() != 0) {
      outputHist.add(output);
    }

    return outputHist;
  }

  /**
   * Close everything
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    historyReader.close();
  }
}

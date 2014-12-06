package hw4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Wrapper for tagger_history_generator.py in Java
 * 
 * @author linanqiu
 * @file_name TaggerHistoryGenerator.java
 */
public class PyTaggerHistoryGenerator {

  public static final String PYTHON = "python";
  public static final String ENUM = "ENUM";
  public static final String GOLD = "GOLD";

  private ProcessBuilder goldProcessBuilder;
  private Process goldProcess;
  private BufferedReader goldReader;
  private BufferedWriter goldWriter;

  private ProcessBuilder enumProcessBuilder;
  private Process enumProcess;
  private BufferedReader enumReader;
  private BufferedWriter enumWriter;

  /**
   * Starts processes using processbuilder and pipes reader and writer
   * 
   * @param tagger_history_generator_py
   * @throws IOException
   */
  public PyTaggerHistoryGenerator(String tagger_history_generator_py)
      throws IOException {
    goldProcessBuilder = new ProcessBuilder(PYTHON,
        tagger_history_generator_py, GOLD);
    goldProcess = goldProcessBuilder.start();
    goldReader = new BufferedReader(new InputStreamReader(
        goldProcess.getInputStream()));
    goldWriter = new BufferedWriter(new OutputStreamWriter(
        goldProcess.getOutputStream()));

    enumProcessBuilder = new ProcessBuilder(PYTHON,
        tagger_history_generator_py, ENUM);
    enumProcess = enumProcessBuilder.start();
    enumReader = new BufferedReader(new InputStreamReader(
        enumProcess.getInputStream()));
    enumWriter = new BufferedWriter(new OutputStreamWriter(
        enumProcess.getOutputStream()));
  }

  /**
   * Sends argument to tagger_history_generator.py and receives output
   * 
   * @param argument
   *          arguments for the python process
   * @return output from python process
   * @throws IOException
   */
  public ArrayList<String> pyGold(String argument) throws IOException {

    goldWriter.write(argument);
    goldWriter.write("\n\n");
    goldWriter.flush();

    String output;
    ArrayList<String> goldHist = new ArrayList<String>();
    while ((output = goldReader.readLine()).length() != 0) {
      goldHist.add(output);
    }

    return goldHist;
  }

  /**
   * Sends argument to tagger_history_generator.py and receives output
   * 
   * @param argument
   *          arguments for the python process
   * @return output from python process
   * @throws IOException
   */
  public ArrayList<String> pyEnum(String argument) throws IOException {

    enumWriter.write(argument);
    enumWriter.write("\n\n");
    enumWriter.flush();

    String output;
    ArrayList<String> enumHist = new ArrayList<String>();

    while ((output = enumReader.readLine()).length() != 0) {
      enumHist.add(output);
    }

    return enumHist;
  }

  /**
   * Closes everthing
   * 
   * @throws IOException
   */
  public void close() throws IOException {
    goldReader.close();
    enumReader.close();
    goldWriter.close();
    enumWriter.close();
  }
}

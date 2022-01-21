package view;

import java.io.IOException;

/**
 * This interface outlines the basic things that the view should. ITextView allows for
 * interactive and file-based textual scripting as well as displaying output.
 */
public interface ITextView {

  /**
   * Returns the input by the user.
   *
   * @return String
   * @throws IOException if I/O error occurs
   */
  String getInput() throws IOException;

  /**
   * Appends given String to Appendable.
   *
   * @param output String
   * @throws IOException if I/O error occurs
   */
  void displayOutput(String output) throws IOException;

}

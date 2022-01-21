package view;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class represents a TextView of this image processing program. It allows
 * parsing of user input and displaying output that may be any errors caused by the user's input.
 */

public class TextView implements ITextView {

  private final Scanner sc;
  private final Appendable ap;

  /**
   * creates a TextView object.
   *
   * @param rd Readable object
   * @param ap Appendable object.
   * @throws IllegalArgumentException if arguments are null.
   */
  public TextView(Readable rd, Appendable ap) throws IllegalArgumentException {
    if (rd == null || ap == null) {
      throw new IllegalArgumentException("Arguments are invalid.");
    }
    this.ap = ap;
    sc = new Scanner(rd);
  }

  @Override
  public String getInput() throws IOException {
    this.displayOutput("Enter input: ");
    if (sc.hasNextLine()) {
      return sc.nextLine();
    } else {
      this.displayOutput("Unable to read input.");
      return null;
    }
  }

  @Override
  public void displayOutput(String output) throws IOException {
    this.ap.append(output);
  }
}

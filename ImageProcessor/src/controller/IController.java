package controller;

import java.io.IOException;

/**
 * The IController interface creates an implementation that will allow for the handling of user
 * input, the saving of a state as a file, and the loading of a state based on the file type.
 */
public interface IController {

  /**
   * Method that starts controller.
   *
   * @throws IOException when input or output fail
   */
  void start() throws IOException;

}

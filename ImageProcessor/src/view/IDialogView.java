package view;

import java.util.List;

/**
 * This interface allows us to get the results that the view gets (i.e. apply mosaic 1000). This
 * interface will return the number of seeds that the user inputs in the GUI.
 */
public interface IDialogView {

  /**
   * returns any input the user types and enters.
   *
   * @return List of String
   */
  List<String> getResults();
}

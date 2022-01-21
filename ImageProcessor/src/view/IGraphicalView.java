package view;

import layermodel.IROLayer;

import java.awt.event.ActionListener;
import java.util.List;

/**
 * This interface represents a GUI view. This interface creates methods that will allow further
 * implementation of a GUI. We have created methods that allow the rendering of a GUI, the ability
 * to refresh a GUi, the ability to show error messages, the ability to update the current read-only
 * model, the ability to deal will button commands
 */
public interface IGraphicalView {

  /**
   * Renders the GUI display.
   */
  void display();

  /**
   * This refreshes the GUI any time a the IROLayer is changed in the controller.
   */
  void refresh();

  /**
   * Displays error message.
   *
   * @param message String
   */
  void alert(String message);

  /**
   * Allows for the update of the IROLayer.
   *
   * @param model read-only Layer model
   */
  void setModel(IROLayer model);

  /**
   * Determines which button has been pushed and returns appropriate visual.
   *
   * @param identifier String that represents the buttons
   * @return List of String
   */
  List<String> dialogHandler(String identifier);

  /**
   * Sets an action listener.
   *
   * @param listener ActionListener
   */
  void setListener(ActionListener listener);

}

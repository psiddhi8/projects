package layermodel;

import imagemodel.IImage;

import java.util.List;

/**
 * This interface was created in order to prevent the view from being able to manipulate a model.
 * This Read-Only Layer allows one to get any layer image, get the current image, get the list of
 * visible images, get the properties of the layer, and determine if the layer has a current. These
 * methods will help the view render the model with a readable instance of the model.
 */
public interface IROLayer {

  /**
   * returns the IImage of the layer corresponding to the index.
   *
   * @param index that corresponds to the layer
   * @return an IImage of the layer
   * @throws IllegalArgumentException if index is out of bounds
   */
  IImage getLayer(int index) throws IllegalArgumentException;

  /**
   * Produces the IImage at the current index.
   *
   * @return the current IImage
   * @throws IllegalArgumentException if index is out of bounds
   */
  IImage getCurrent() throws IllegalArgumentException;

  /**
   * An observer for all visible images in this ILayer.
   *
   * @return A list of IImage
   */
  List<IImage> getVisible();

  /**
   * Creates a list of the number of layers, width, height, depth, and current index of the ILayer.
   *
   * @return a list of Integers
   */
  List<Integer> getProps();

  /**
   * Determines if a Layer has a current image.
   *
   * @return boolean
   */
  boolean hasCurrent();

  /**
   * Returns the image topmost visible image.
   *
   * @return IImage
   */
  IImage getCurrentVisible();

}

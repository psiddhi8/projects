package filter;

import imagemodel.IImage;
import imagemodel.IPixel;

import java.util.List;

/**
 * The IModifier interface contains all publicly accessible and implementable methods for modifiers.
 * Modifiers represent both filters and transformers.
 */
public interface IModifier {

  /**
   * Modify an image with this IModifier.
   *
   * @param image the image to be modified
   * @return the list of modified pixels after applying a modifier.
   */
  List<IPixel> modify(IImage image);

}

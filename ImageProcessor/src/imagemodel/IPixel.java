package imagemodel;

import java.util.List;

/**
 * Represents a pixel. This interface allows future use to apply a modifier to all channels,
 * and apply a modifier to the r, g, and b value. One will also be able to observe its coordinaties
 * and rgb values;
 */
public interface IPixel {

  /**
   * Apply a modifier to all channels.
   *
   * @param modifier the modifier
   * @return the list of R, G and B values after modifying each of them.
   */
  List<Double> applyToAllChannels(double modifier);

  /**
   * Apply a given modifier to the R channel of the pixel. The modifier is multiplied by the
   * current r value and is then clamped to be between 0 and max color depth.
   *
   * @param modifier the modifier to be applied
   * @return the modified R value
   */
  double applyToR(double modifier);

  /**
   * Apply the modifier to the G channel.
   *
   * @param modifier the modifier
   * @return the modified G value
   */
  double applyToG(double modifier);

  /**
   * Apply the modifier to the B channel.
   *
   * @param modifier the modifier
   * @return the modified B value
   */
  double applyToB(double modifier);

  /**
   * Gets the coordinates as (x, y) of the given pixel.
   *
   * @return the coordinates.
   */
  List<Integer> getCoords();

  /**
   * Gets the color as (r, g, b) of the given pixel.
   *
   * @return the color.
   */
  List<Integer> getColor();

}

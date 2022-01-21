package imagemodel;

import filter.IModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * The Checkerboard image contains a representation of a simple checkerboard image.
 * It contains all methods needed to create a black and white checkerboard pattern.
 * extends the Image Class.
 */
public class Checkerboard extends Image {

  /**
   * Instantiates a new Checkerboard image.
   *
   * @param width  the width of the image
   * @param height the height
   * @param depth  the color depth (normally 255)
   * @throws IllegalArgumentException if width, height, or depth are less than 0
   */
  public Checkerboard(int width, int height, int depth, int size) {
    super(generateCheckerboard(width, height, depth, size), width, height, depth);
  }

  @Override
  public void applyFilter(IModifier iModifier) {
    throw new UnsupportedOperationException("Cannot apply filter to Checkerboard");
  }

  protected static List<IPixel> generateCheckerboard(int width, int height, int depth, int size) {
    if (size == 0 || width % size != 0 || height % size != 0) {
      throw new IllegalArgumentException("Invalid size, pixels will not fit");
    }

    List<IPixel> pixels = new ArrayList<>();
    for (int i = 0; i < width / size; i++) {
      for (int j = 0; j < height / size; j++) {
        if ((i + j) % 2 == 0) {
          pixels.add(new Pixel(i, j, 0, 0, 0));
        } else {
          pixels.add(new Pixel(i, j, depth, depth, depth));
        }
      }
    }
    return pixels;
  }
}

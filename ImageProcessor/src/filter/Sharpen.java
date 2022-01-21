package filter;

/**
 * The Sharpen class is an AFilter which has a constructor of the Kernel to be applied to an IImage
 * that one wishes to sharpen. There is also a toString which returns the class name.
 */
public class Sharpen extends AFilter {

  /**
   * Instantiates a new Sharpen filter with a kernel that is known to be valid.
   */
  public Sharpen() {
    super(new double[][]{
            {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0},
            {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
            {-1.0 / 8.0, 1.0 / 4.0, 1, 1.0 / 4.0, -1.0 / 8.0},
            {-1.0 / 8.0, 1.0 / 4.0, 1.0 / 4.0, 1.0 / 4.0, -1.0 / 8.0},
            {-1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0, -1.0 / 8.0}
    });
  }

  /**
   * Allows sharpening of image with input kernel but dimensions must be odd.
   *
   * @param kernel 2d array of doubles
   * @throws IllegalArgumentException if dimensions of array are not odd
   */
  public Sharpen(double[][] kernel) {
    super(kernel);
  }

  @Override
  public String toString() {
    return "sharpen";
  }

}

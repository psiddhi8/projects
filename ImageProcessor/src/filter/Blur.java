package filter;

/**
 * The Blur class is an AFilter which has a constructor of the Kernel to be applied to an IImage
 * that one wishes to blur. There is also a toString which returns the class name.
 */
public class Blur extends AFilter {

  /**
   * Instantiates a new Blur kernel. This kernel is known to be valid.
   */
  public Blur() {
    super(new double[][]{
            {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0},
            {1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0},
            {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0}
    });
  }

  /**
   * Allows blurring of image with input kernel but dimensions must be odd.
   *
   * @param kernel 2d array of doubles
   * @throws IllegalArgumentException if dimensions of array are not odd
   */
  public Blur(double[][] kernel) {
    super(kernel);
  }

  @Override
  public String toString() {
    return "blur";
  }
}

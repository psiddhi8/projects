package filter;

/**
 * The Sepia class is an ATransform which has a constructor of the Kernel to be applied to an
 * IImage that one wishes to transformed linearly into an image with the Sepia filter.
 * There is also a toString which returns the class name.
 */
public class Sepia extends ATransform {

  /**
   * Instantiates a new Sepia transform with a valid kernel.
   */
  public Sepia() {
    super(new double[][]{
            {0.393, 0.769, 0.189},
            {0.349, 0.686, 0.168},
            {0.272, 0.534, 0.131}
    });
  }

  /**
   * Allows sepia transformation of image with input kernel but dimensions must be odd.
   *
   * @param kernel 2d array of doubles
   * @throws IllegalArgumentException if dimensions of array are not odd
   */
  public Sepia(double[][] kernel) {
    super(kernel);
  }

  @Override
  public String toString() {
    return "sepia";
  }
}

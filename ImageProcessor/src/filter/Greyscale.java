package filter;

/**
 * The Greyscale class is an ATransform which has a constructor of the Kernel to be applied to an
 * IImage that one wishes to transformed linearly into greyscale or some may call monochrome.
 * There is also a toString which returns the class name.
 */
public class Greyscale extends ATransform {

  /**
   * Instantiates a new Greyscale object with a valid kernel.
   */
  public Greyscale() {
    super(new double[][]{
            {0.2126, 0.7152, 0.0722},
            {0.2126, 0.7152, 0.0722},
            {0.2126, 0.7152, 0.0722}
    });
  }

  /**
   * Allows greyscale of image with input kernel but dimensions must be odd.
   *
   * @param kernel 2d array of doubles
   * @throws IllegalArgumentException if dimensions of array are not odd
   */
  public Greyscale(double[][] kernel) {
    super(kernel);
  }

  @Override
  public String toString() {
    return "greyscale";
  }
}

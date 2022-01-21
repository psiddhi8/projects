package filter;

import imagemodel.IImage;
import imagemodel.IPixel;

import java.util.ArrayList;
import java.util.List;

/**
 * The AModifier abstract class implements methods used by both AFilters and ATransforms.
 * It extends the IModifier interface which created the modify method that allows this IModifier
 * to .
 */
public abstract class AModifier implements IModifier {
  protected List<IPixel> pixels;
  protected double[][] kernel;
  protected List<Double> crushedKernel;

  /**
   * Instantiates a new Modifier.
   *
   * @param kernel the kernel which is validated to be an odd x odd configuration
   * @throws IllegalArgumentException if kernel does not have odd dimensions
   */
  public AModifier(double[][] kernel) {
    if (!this.isValidKernel(kernel)) {
      throw new IllegalArgumentException("Invalid kernel");
    }
    this.pixels = new ArrayList<>();
    this.kernel = kernel;
    this.crushedKernel = this.flattenKernel(kernel);
  }

  /**
   * Determines if the kernel has odd dimensions.
   *
   * @param kernel input kernel
   * @return true if kernel has odd dimensions
   */
  private boolean isValidKernel(double[][] kernel) {
    if (kernel.length % 2 == 0) {
      return false;
    }

    for (double[] row : kernel) {
      if (row.length % 2 == 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates a list of the contents of the kernel.
   *
   * @param kernel 2d array of doubles that will be the modifier
   * @return List of the contents of the kernel
   */
  protected List<Double> flattenKernel(double[][] kernel) {
    List<Double> crushedKernel = new ArrayList<>();
    for (double[] row : kernel) {
      for (double n : row) {
        crushedKernel.add(n);
      }
    }
    return crushedKernel;
  }

  @Override
  public List<IPixel> modify(IImage image) {
    List<IPixel> origPixels = image.getPixels();
    int width = image.getProps().get(0);
    int height = image.getProps().get(1);

    origPixels.forEach((pixel) -> {
      this.pixels.add(this.applyToPixel(origPixels, pixel, width, height));
    });

    return this.pixels;
  }

  /**
   * Applies the filter to a Pixel and returns a new pixel with the same x,
   * same y, and new RGB values.
   *
   * @param pixels original list of pixels of the image
   * @param pixel  the pixel that we want the new RBG values of
   * @param width  the width of the image
   * @param height the height of the image
   * @return a new pixel with the same x, same y, and new RBG values
   */
  protected abstract IPixel applyToPixel(List<IPixel> pixels, IPixel pixel, int width, int height);
}

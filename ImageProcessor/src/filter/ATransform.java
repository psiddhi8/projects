package filter;

import imagemodel.IPixel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import imagemodel.Pixel;

/**
 * The abstract ATransform class contains the methods implemented by all filters (ex: Sepia).
 * This class specifically overrides the AModifier applyToPixel method for kernel configuration
 * where each pixel gets a new RBG value through a linear transformation.
 * It also extends AModifier.
 */
public class ATransform extends AModifier {

  /**
   * Instantiates a new Transform.
   *
   * @param kernel the kernel which is validated in the superclass.
   */
  public ATransform(double[][] kernel) {
    super(kernel);
  }

  /**
   * Creates a new list of RGB values that are a result of linear transformation with
   * matrix multiplication with the kernel.
   *
   * @param pixel current pixel that we want to apply the kernel on
   * @return new list of RBG values with kernel multiplication applied
   */
  private List<Double> generateNewRGB(IPixel pixel) {
    List<Double> newRGB = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
    newRGB.set(0, pixel.applyToR(crushedKernel.get(0))
            + pixel.applyToG(crushedKernel.get(1))
            + pixel.applyToB(crushedKernel.get(2)));
    newRGB.set(1, pixel.applyToR(crushedKernel.get(3))
            + pixel.applyToG(crushedKernel.get(4))
            + pixel.applyToB(crushedKernel.get(5)));
    newRGB.set(2, pixel.applyToR(crushedKernel.get(6))
            + pixel.applyToG(crushedKernel.get(7))
            + pixel.applyToB(crushedKernel.get(8)));

    return newRGB;
  }

  @Override
  protected IPixel applyToPixel(List<IPixel> pixels, IPixel pixel, int width, int height) {
    //get pixel with new rgb values
    return new Pixel(pixel.getCoords(), this.generateNewRGB(pixel));
  }
}

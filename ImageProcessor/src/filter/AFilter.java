package filter;

import imagemodel.IPixel;
import imagemodel.Pixel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The abstract AFilter class contains the methods implemented by all filters (ex: Blur).
 * This class specifically overrides the AModifier applyToPixel method for kernel configuration
 * where each pixel is the center of the kernel and the math is applied to each channel.
 * It also extends AModifier.
 */
public abstract class AFilter extends AModifier {

  /**
   * Instantiates a new Filter.
   *
   * @param kernel the kernel which needs to be a odd x odd configuration.
   */
  public AFilter(double[][] kernel) {
    super(kernel);
  }

  /**
   * Determines what the new RBG values are for inputted pixel based on the kernel. The math for
   * each pixel channel is done by making the pixel the center and multiplying the kernel but the
   * rbg values that correspond to surrounding pixels. If the pixel doesn't have a neighbor it will
   * skip that kernel.
   *
   * @param origPixels the list of pixels of the original image
   * @param pixel      the pixel that we want to find the new RGB values of
   * @param width      the width of the image
   * @param height     the height of the Image
   * @return A new list of RBG values that is the result of the kernel math
   */
  private List<Double> generateNewRGB(List<IPixel> origPixels, IPixel pixel, int width,
                                      int height) {
    List<Double> newRGB = new ArrayList<>(Arrays.asList(0.0, 0.0, 0.0));
    int x = pixel.getCoords().get(0);
    int y = pixel.getCoords().get(1);

    int key = (kernel.length - 1) / 2;
    int kIndex = 0;
    for (int i = -1 * key; i <= key; i++) {
      for (int j = -1 * key; j <= key; j++) {
        int index = ((y + i) * width) + (x + j);
        if (!(index < 0 || index >= origPixels.size() || (x + j < 0) || (y + i < 0)
                || (x + j) > width - 1 || (y + i) > height - 1)) {
          List<Double> thisRGB = origPixels.get(index).applyToAllChannels(
                  crushedKernel.get(kIndex));
          for (int k = 0; k < newRGB.size(); k++) {
            newRGB.set(k, newRGB.get(k) + thisRGB.get(k));
          }
        }
        kIndex++;
      }
    }
    return newRGB;
  }

  @Override
  protected IPixel applyToPixel(List<IPixel> origPixels, IPixel pixel, int width, int height) {
    //get pixel with new rgb values
    return new Pixel(pixel.getCoords(), this.generateNewRGB(origPixels, pixel, width, height));
  }
}

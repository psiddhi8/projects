package filter;

import imagemodel.IImage;
import imagemodel.IPixel;
import imagemodel.Pixel;

import java.util.ArrayList;
import java.util.List;


/**
 * This class modifies an image by downsizing it to a new width and height. It has a constructor
 * that takes in an int height and int width. In order to DownScale to modify an IImage, both must
 * be less than or equal to the current width and height. This class has a toString method for its
 * title.
 */
public class DownScale implements IModifier {
  private int width;
  private int height;

  /**
   * Creates a Downscale obj with a width and height.
   *
   * @param width  int
   * @param height int
   */
  public DownScale(int width, int height) {
    this.width = width;
    this.height = height;
  }


  @Override
  public String toString() {
    return "downscale";
  }

  @Override
  public List<IPixel> modify(IImage image) {
    List<IPixel> newPixels = new ArrayList<>();
    final int currWidth = image.getProps().get(0);
    final int currHeight = image.getProps().get(1);

    float xRatio = (1.0f * currWidth) / this.width;
    float yRatio = (1.0f * currHeight) / this.height;

    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        int x = Math.round(j * xRatio);
        int y = Math.round(i * yRatio);
        if (x < currWidth && y < currHeight) {
          List<Integer> c = image.getPixel(x, y).getColor();
          IPixel p = new Pixel(j, i, c.get(0), c.get(1), c.get(2));
          newPixels.add(p);
        }
      }
    }
    image.changeCanvasSize(this.width, this.height);
    return newPixels;

  }
}

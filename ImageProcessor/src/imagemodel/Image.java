package imagemodel;

import filter.IModifier;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The Image class that contains methods to print Image as a ppm file, modify an image, and create a
 * bufferedImage of this Image, change the canvas size, get the pixels, get a pixel at a certain
 * coordinate, and get the properties of this Image. This class also implements the IImage
 * interface.
 */
public class Image implements IImage {

  private List<IPixel> pixels;
  private int width;
  private int height;
  private final int depth;

  /**
   * Instantiates a new Image.
   *
   * @param pixels the pixels that make up the image (note this is 1D not 2D)
   * @param width  the width of the image
   * @param height the height
   * @param depth  the color depth (usually 255)
   * @throws IllegalArgumentException if pixels is null or if width, height, or
   *                                  depth are less than 1
   */
  public Image(List<IPixel> pixels, int width, int height, int depth) {
    if (pixels == null || width <= 0 || height <= 0 || depth <= 0) {
      throw new IllegalArgumentException("Invalid Parameters.");
    }
    this.pixels = pixels;
    this.width = width;
    this.height = height;
    this.depth = depth;
  }

  @Override
  public String toString() {
    StringBuilder matrix = new StringBuilder(width + "\n" + height + "\n" + depth + "\n");
    for (IPixel p : this.pixels) {
      matrix.append(p.toString());
    }
    return matrix.append("\n").toString();
  }

  @Override
  public BufferedImage createImage() {
    BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (IPixel p : pixels) {
      Color rgb = new Color(p.getColor().get(0), p.getColor().get(1), p.getColor().get(2));
      b.setRGB(p.getCoords().get(0), p.getCoords().get(1), rgb.getRGB());
    }
    return b;
  }

  @Override
  public void changeCanvasSize(int width, int height) throws IllegalArgumentException {
    if (width > this.width || height > this.height) {
      throw new IllegalArgumentException("Width and height must be less than current width and "
              + "height.");
    }
    this.width = width;
    this.height = height;
  }

  @Override
  public void applyFilter(IModifier iModifier) {
    if (iModifier == null) {

      throw new IllegalArgumentException("Illegal modifier");
    }
    this.pixels = iModifier.modify(this);
  }

  @Override
  public List<IPixel> getPixels() {
    return new ArrayList<>(this.pixels);
  }

  @Override
  public IPixel getPixel(int x, int y) {
    return this.pixels.get((y * width) + x);
  }

  @Override
  public List<Integer> getProps() {
    return new ArrayList<>(Arrays.asList(this.width, this.height, this.depth));
  }
}

package layermodel;

import filter.IModifier;
import imagemodel.IImage;
import imagemodel.IPixel;
import imagemodel.Image;
import imagemodel.Pixel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class represents an implementation of {@link ILayer}. It takes in a List of {@link IImage},
 * a width, a height, and a depth. From this, it automatically sets all layers as visible.
 * One can also set an image in the {@link ILayer} as the current, which has a default of 0.
 * When a {@code Layer} is set as current, you can modify it if you wish (i.e. Blur, Sepia, etc.).
 * One can also make a layer invisible and add more layers. One property that must be kept
 * is that any image loaded as a layer must have the same width, height, and depth as those
 * already in the Layer.
 */
public class Layer implements ILayer {

  private final List<IImage> layers;
  private Map<IImage, Boolean> visibility;
  private final List<Integer> currentHistory;
  private int current;
  private int height;
  private int width;
  private int depth;

  public Layer() {
    this(new ArrayList<>(), -1, -1, -1);
  }

  public Layer(List<Integer> props) {
    this(new ArrayList<>(), props.get(0), props.get(1), props.get(2));
  }

  /**
   * Constructor that takes in a list of the IImage properties.
   *
   * @param images List of IImages that become separate layers
   * @param props  List of integers that represent the width, height, and depth
   */
  public Layer(List<IImage> images, List<Integer> props) {
    this(images, props.get(0), props.get(1), props.get(2));
  }

  /**
   * Constructor for a Layer object.
   *
   * @param images List of IImages that become separate layers
   * @param width  of the Layer
   * @param height of the Layer
   * @param depth  the maximum color value of the Layer
   * @throws IllegalArgumentException when list of images is null or has an image with diff
   *                                  properties
   */
  public Layer(List<IImage> images, int width, int height, int depth) {
    if (images == null || !this.isValidImages(images)) {
      throw new IllegalArgumentException("Invalid list of images provided");
    }
    this.layers = images;
    this.visibility = this.createMappedVisibility(images);
    this.height = height;
    this.width = width;
    this.depth = depth;
    this.current = 0;
    this.currentHistory = new ArrayList<>(Collections.singleton(0));
  }

  private Map<IImage, Boolean> createMappedVisibility(List<IImage> images) {
    Map<IImage, Boolean> vis = new HashMap<>();
    for (IImage img : images) {
      vis.put(img, true);
    }
    return vis;
  }

  private boolean canAcceptImage(IImage image) {
    if (this.width == -1 || this.height == -1 || this.depth == -1) {
      this.width = image.getProps().get(0);
      this.height = image.getProps().get(1);
      this.depth = image.getProps().get(2);
      return true;
    }

    return this.width == image.getProps().get(0)
            && this.height == image.getProps().get(1)
            && this.depth == image.getProps().get(2);
  }

  private boolean isValidImages(List<IImage> images) {
    if (images.size() == 0) {
      return true;
    }

    List<Integer> props = images.get(0).getProps();
    for (IImage img : images) {
      if (!img.getProps().equals(props)) {
        return false;
      }
    }
    return true;
  }

  private void updateHistory(int idx) {
    if (this.currentHistory.contains(idx)) {
      this.currentHistory.remove(Integer.valueOf(idx));
    }
    this.currentHistory.add(0, idx);
  }

  @Override
  public void addLayer(IImage image) throws IllegalArgumentException {
    if (!canAcceptImage(image)) {
      throw new IllegalArgumentException("Image must have same properties as the one in the layer"
              + " below");
    }
    this.currentHistory.add(this.currentHistory.size());
    this.layers.add(image);
    this.visibility.put(image, true);
  }

  @Override
  public IImage getLayer(int index) throws IllegalArgumentException {
    if (index <= 0 || index > this.layers.size()) {
      throw new IllegalArgumentException("Invalid index provided");
    }
    return this.layers.get(index - 1);
  }

  @Override
  public IImage blend() {
    List<IPixel> pixels = new ArrayList<>();
    int numOfVisibleLayers = this.getVisible().size();

    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        double avgR = 0.0;
        double avgG = 0.0;
        double avgB = 0.0;

        for (IImage layer : this.layers) {
          if (visibility.get(layer)) {
            IPixel p = layer.getPixel(i, j);
            avgR += p.getColor().get(0);
            avgG += p.getColor().get(1);
            avgB += p.getColor().get(2);
          }
        }
        pixels.add(new Pixel(i, j, avgR / numOfVisibleLayers, avgG / numOfVisibleLayers,
                avgB / numOfVisibleLayers));
      }
    }
    return new Image(pixels, width, height, depth);
  }

  @Override
  public void setCurrent(int index) throws IllegalArgumentException {
    if (index <= 0 || index > this.layers.size()) {
      throw new IllegalArgumentException("Image at " + index + " does not exist.");
    } else {
      this.current = index - 1;
    }
    this.updateHistory(index - 1);
  }

  @Override
  public IImage getCurrent() throws IllegalArgumentException {
    if (this.layers.isEmpty()) {
      throw new IllegalArgumentException("No current image to get!");
    }
    return this.layers.get(current);
  }

  @Override
  public void toggleVisibility(int index) throws IllegalArgumentException {
    if (index <= 0 || index > this.layers.size()) {
      throw new IllegalArgumentException("Image at " + index + " does not exist.");
    } else {
      IImage img = this.layers.get(index - 1);
      this.visibility.replace(img, !this.visibility.get(img));
    }
  }

  @Override
  public List<IImage> getVisible() {
    List<IImage> images = new ArrayList<>();
    for (Map.Entry<IImage, Boolean> img : this.visibility.entrySet()) {
      if (img.getValue()) {
        images.add(img.getKey());
      }
    }
    return images;
  }

  @Override
  public void applyToCurrent(IModifier modifier) {
    if (layers.size() == 0) {
      throw new IllegalArgumentException("No image to apply filter to.");
    } else {
      this.layers.get(current).applyFilter(modifier);
    }
  }

  @Override
  public String toString() {
    StringBuilder matrix = new StringBuilder("LAYER\n" + this.layers.size() + "\n"
            + width + "\n" + height + "\n" + depth + "\n");
    for (IImage img : this.layers) {
      matrix.append(this.visibility.get(img)).append("\n").append(img.toString());
    }
    return matrix.toString();
  }

  @Override
  public List<Integer> getProps() {
    return new ArrayList<>(Arrays.asList(this.layers.size(), this.height, this.width, this.depth,
            this.current));
  }

  @Override
  public boolean hasCurrent() {
    return this.layers.size() > 0;
  }

  @Override
  public IImage getCurrentVisible() {
    try {
      IImage current = this.getCurrent();
      if (this.visibility.get(current)) {
        return current;
      } else {
        for (int i = 1; i < this.currentHistory.size() - 1; i++) {
          current = this.layers.get(this.currentHistory.get(i));
          if (this.visibility.get(current)) {
            return current;
          }
        }
      }
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  public void alterLayer(IModifier modifier, int width, int height)
          throws IllegalArgumentException {
    if (width > this.width || height > this.height) {
      throw new IllegalArgumentException("\"Width and height must be less than current width "
              + "and height.");
    }
    if (layers.size() == 0) {
      throw new IllegalArgumentException("No image to apply filter to.");
    } else {
      for (IImage img : this.layers) {
        img.applyFilter(modifier);
      }
    }
    this.width = width;
    this.height = height;
  }


}

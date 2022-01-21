package filter;

import imagemodel.IImage;
import imagemodel.IPixel;
import imagemodel.Image;
import imagemodel.Pixel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the sub-class of {@link ATransform}, {@link Sepia}, and extends
 * {@link ATransformTest}. This class tests the methods that create an object that is an
 * {@link ATransform} and an {@link IModifier}. It also tests the modify and toString method on this
 * Sepia object.
 */
public class SepiaTest extends ATransformTest {
  List<IPixel> pixels;
  List<IPixel> pixelsSepia;
  IImage img;
  double[][] kernelEvenRows;
  double[][] kernelEvenColumns;

  @Before
  public void initData() {
    pixels = new ArrayList<>();
    pixels.add(new Pixel(0, 0, 100, 100, 100));
    pixels.add(new Pixel(0, 1, 100, 100, 100));
    pixels.add(new Pixel(0, 2, 100, 100, 100));
    pixels.add(new Pixel(1, 0, 100, 100, 100));
    pixels.add(new Pixel(1, 1, 100, 100, 100));
    pixels.add(new Pixel(1, 2, 100, 100, 100));
    pixels.add(new Pixel(2, 0, 100, 100, 100));
    pixels.add(new Pixel(2, 1, 100, 100, 100));
    pixels.add(new Pixel(2, 2, 100, 100, 100));

    pixelsSepia = new ArrayList<>();
    pixelsSepia.add(new Pixel(0, 0, 135, 120, 93));
    pixelsSepia.add(new Pixel(0, 1, 135, 120, 93));
    pixelsSepia.add(new Pixel(0, 2, 135, 120, 93));
    pixelsSepia.add(new Pixel(1, 0, 135, 120, 93));
    pixelsSepia.add(new Pixel(1, 1, 135, 120, 93));
    pixelsSepia.add(new Pixel(1, 2, 135, 120, 93));
    pixelsSepia.add(new Pixel(2, 0, 135, 120, 93));
    pixelsSepia.add(new Pixel(2, 1, 135, 120, 93));
    pixelsSepia.add(new Pixel(2, 2, 135, 120, 93));

    img = new Image(pixels, 3, 3, 255);
    kernelEvenRows = new double[][]{
            {0.393, 0.769, 0.189},
            {0.349, 0.686, 0.168}
    };

    kernelEvenColumns = new double[][]{
            {0.393, 0.769},
            {0.349, 0.686},
            {0.272, 0.534}
    };
  }

  @Override
  public IModifier objectCreator() {
    return new Sepia();
  }

  @Override
  public ATransform objectTransformCreator() {
    return new Sepia();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenRows() {
    new Sepia(kernelEvenRows);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenColumns() {
    new Sepia(kernelEvenColumns);
  }

  @Test
  public void testModifier() {
    assertEquals(pixelsSepia.toString(), this.objectCreator().modify(img).toString());
  }

  @Test
  public void testToString() {
    assertEquals("sepia", this.objectTransformCreator().toString());
  }
}
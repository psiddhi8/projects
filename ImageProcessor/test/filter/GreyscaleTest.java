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
 * This class tests the sub-class of {@link ATransform}, {@link Greyscale}, and extends
 * {@link ATransformTest}. This class tests the methods that create an object that is an
 * {@link ATransform} and an {@link IModifier}. It also tests the modify and toString method on this
 * Greyscale object.
 */
public class GreyscaleTest extends ATransformTest {
  List<IPixel> pixels;
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

    img = new Image(pixels, 3, 3, 255);
    kernelEvenRows = new double[][]{
            {0.2126, 0.7152, 0.0722},
            {0.2126, 0.7152, 0.0722}
    };

    kernelEvenColumns = new double[][]{
            {0.2126, 0.7152},
            {0.2126, 0.7152},
            {0.2126, 0.7152}
    };
  }

  @Override
  public IModifier objectCreator() {
    return new Greyscale();
  }

  @Override
  public ATransform objectTransformCreator() {
    return new Greyscale();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenRows() {
    new Greyscale(kernelEvenRows);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenColumns() {
    new Greyscale(kernelEvenColumns);
  }

  @Test
  public void testModifier() {
    assertEquals(pixels.toString(), this.objectCreator().modify(img).toString());
  }

  @Test
  public void testToString() {
    assertEquals("greyscale", this.objectTransformCreator().toString());
  }
}
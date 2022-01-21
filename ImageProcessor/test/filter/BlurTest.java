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
 * This class tests the sub-class of {@link AFilter}, {@link Blur}, and extends {@link AFilterTest}.
 * This class tests the methods that create an object that is an {@link AFilter} and
 * {@link IModifier}. It also tests the modify and toString method on this Blur object.
 */
public class BlurTest extends AFilterTest {
  List<IPixel> pixels;
  List<IPixel> pixelsBlurred;
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

    pixelsBlurred = new ArrayList<>();
    pixelsBlurred.add(new Pixel(0, 0, 56, 56, 56));
    pixelsBlurred.add(new Pixel(0, 1, 75, 75, 75));
    pixelsBlurred.add(new Pixel(0, 2, 56, 56, 56));
    pixelsBlurred.add(new Pixel(1, 0, 75, 75, 75));
    pixelsBlurred.add(new Pixel(1, 1, 100, 100, 100));
    pixelsBlurred.add(new Pixel(1, 2, 75, 75, 75));
    pixelsBlurred.add(new Pixel(2, 0, 56, 56, 56));
    pixelsBlurred.add(new Pixel(2, 1, 75, 75, 75));
    pixelsBlurred.add(new Pixel(2, 2, 56, 56, 56));
    img = new Image(pixels, 3, 3, 255);

    kernelEvenRows = new double[][]{
            {1.0 / 16.0, 1.0 / 8.0, 1.0 / 16.0},
            {1.0 / 8.0, 1.0 / 4.0, 1.0 / 8.0}
    };

    kernelEvenColumns = new double[][]{
            {1.0 / 16.0, 1.0 / 8.0},
            {1.0 / 8.0, 1.0 / 4.0},
            {1.0 / 16.0, 1.0 / 8.0}
    };
  }

  @Override
  public IModifier objectCreator() {
    return new Blur();
  }

  @Override
  public AFilter objectFilterCreator() {
    return new Blur();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenRows() {
    new Blur(kernelEvenRows);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidKernelWithEvenColumns() {
    new Blur(kernelEvenColumns);
  }

  @Test
  public void testModifier() {
    assertEquals(pixelsBlurred.toString(), this.objectCreator().modify(img).toString());
  }

  @Test
  public void testToString() {
    assertEquals("blur", this.objectFilterCreator().toString());
  }
}
package imagemodel;

import filter.Blur;
import filter.Greyscale;
import filter.Sepia;
import filter.Sharpen;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the Image class. These tests ensure that the
 * Image is implemented in the correct way and returns the Image as a String in p3 ppm format.
 */
public class ImageTest {
  List<IPixel> nullList;
  List<IPixel> pixels;
  List<IPixel> pixelsBlurred;
  List<IPixel> pixelsSharpened;
  List<IPixel> pixelsSepia;
  List<IPixel> pixelsGreyscale;
  int widthInvalid;
  int width;
  int heightInvalid;
  int height;
  int depthInvalid;
  int depth;
  IImage img;
  IImage imgBlurred;
  IImage imgSharpened;
  IImage imgSepia;
  String sepiaString;
  IImage imgGreyscale;

  @Before
  public void setUp() {
    nullList = null;
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
    widthInvalid = -200;
    width = 3;
    heightInvalid = 0;
    height = 3;
    depthInvalid = -3;
    depth = 255;
    img = new Image(pixels, width, height, depth);

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
    imgBlurred = new Image(pixelsBlurred, width, height, depth);

    pixelsSharpened = new ArrayList<>();
    pixelsSharpened.add(new Pixel(0, 0, 112, 112, 112));
    pixelsSharpened.add(new Pixel(0, 1, 187, 187, 187));
    pixelsSharpened.add(new Pixel(0, 2, 112, 112, 112));
    pixelsSharpened.add(new Pixel(1, 0, 187, 187, 187));
    pixelsSharpened.add(new Pixel(1, 1, 255, 255, 255));
    pixelsSharpened.add(new Pixel(1, 2, 187, 187, 187));
    pixelsSharpened.add(new Pixel(2, 0, 112, 112, 112));
    pixelsSharpened.add(new Pixel(2, 1, 187, 187, 187));
    pixelsSharpened.add(new Pixel(2, 2, 112, 112, 112));
    imgSharpened = new Image(pixelsSharpened, width, height, depth);

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
    imgSepia = new Image(pixelsSepia, width, height, depth);
    sepiaString = "P3\n"
            + "3\n"
            + "3\n"
            + "255\n"
            + "135 120 93  135 120 93  135 120 93  135 120 93  "
            + "135 120 93  135 120 93  135 120 93  135 120 93  135 120 93  \n";
    pixelsGreyscale = new ArrayList<>();
    pixelsGreyscale.add(new Pixel(0, 0, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(0, 1, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(0, 2, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(1, 0, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(1, 1, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(1, 2, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(2, 0, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(2, 1, 100, 100, 100));
    pixelsGreyscale.add(new Pixel(2, 2, 100, 100, 100));
    imgGreyscale = new Image(pixelsGreyscale, width, height, depth);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPixels() {
    new Image(nullList, width, height, depth);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWidth() {
    new Image(pixels, widthInvalid, height, depth);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidHeight() {
    new Image(pixels, width, heightInvalid, depth);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDepth() {
    new Image(pixels, width, height, depthInvalid);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidApplyFilter() {
    img.applyFilter(null);
  }

  @Test
  public void testValidApplyBlur() {
    assertEquals(pixels.toString(), img.getPixels().toString());
    img.applyFilter(new Blur());
    assertEquals(pixelsBlurred.toString(), img.getPixels().toString());
  }

  @Test
  public void testValidApplySharpen() {
    assertEquals(pixels.toString(), img.getPixels().toString());
    img.applyFilter(new Sharpen());
    assertEquals(pixelsSharpened.toString(), img.getPixels().toString());
  }

  @Test
  public void testValidApplySepia() {
    assertEquals(pixels.toString(), img.getPixels().toString());
    img.applyFilter(new Sepia());
    assertEquals(pixelsSepia.toString(), img.getPixels().toString());
  }

  @Test
  public void testValidApplyGreyscale() {
    assertEquals(pixels.toString(), img.getPixels().toString());
    img.applyFilter(new Greyscale());
    assertEquals(pixelsGreyscale.toString(), img.getPixels().toString());
  }

  @Test
  public void testGetPixels() {
    assertEquals(pixels.toString(), img.getPixels().toString());
    img.applyFilter(new Blur());
    assertEquals(pixelsBlurred.toString(), img.getPixels().toString());
  }

  @Test
  public void testGetPixel() {
    assertEquals("100 100 100  ", img.getPixel(0, 0).toString());
    img.applyFilter(new Blur());
    assertEquals("56 56 56  ", img.getPixel(0, 0).toString());
  }

  @Test
  public void testGetProps() {
    assertEquals("[3, 3, 255]", img.getProps().toString());
  }

  @Test
  public void testChangeCanvasSize() {
    assertEquals("3", img.getProps().get(0).toString());
    assertEquals("3", img.getProps().get(1).toString());
    img.changeCanvasSize(2, 3);
    assertEquals("2", img.getProps().get(0).toString());
    assertEquals("3", img.getProps().get(1).toString());
  }
}
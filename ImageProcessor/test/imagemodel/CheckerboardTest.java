package imagemodel;

import filter.Blur;
import filter.Greyscale;
import filter.Sepia;
import filter.Sharpen;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the Checkerboard class. These tests ensure that the IImage is implemented
 * in the correct way and returns the CheckerboardImage as a String in p3 ppm format.
 */
public class CheckerboardTest {
  int widthInvalid;
  int width;
  int heightInvalid;
  int height;
  int depthInvalid;
  int depth;
  Checkerboard img;
  Checkerboard img2;
  String imgFile;
  String imgFile2;

  @Before
  public void setUp() {
    widthInvalid = -200;
    width = 3;
    heightInvalid = 0;
    height = 3;
    depthInvalid = -3;
    depth = 255;
    img = new Checkerboard(width, height, depth, 1);
    img2 = new Checkerboard(width, height, depth, 3);

    imgFile = "3\n"
            + "3\n"
            + "255\n"
            + "0 0 0  255 255 255  0 0 0  255 255 255  0 0 0  255 255 255  0 0 0  "
            + "255 255 255  0 0 0  \n";

    imgFile2 = "3\n"
            + "3\n"
            + "255\n"
            + "0 0 0  \n";
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidWidth() {
    new Checkerboard(widthInvalid, height, depth, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidHeight() {
    new Checkerboard(width, heightInvalid, depth, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDepth() {
    new Checkerboard(width, height, depthInvalid, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSize() {
    new Checkerboard(width, height, depth, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSizeWithHeight() {
    new Checkerboard(width, height, depth, 2);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInvalidApplyFilterBlur() {
    img.applyFilter(new Blur());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInvalidApplyFilterSharpen() {
    img.applyFilter(new Sharpen());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInvalidApplyTransformSepia() {
    img.applyFilter(new Sepia());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testInvalidApplyTransformGreyscale() {
    img.applyFilter(new Greyscale());
  }

  @Test
  public void testToString() {
    assertEquals(imgFile, img.toString());
  }

  @Test
  public void testToStringForSizeGreaterThan1() {
    assertEquals(imgFile2, img2.toString());
  }
}
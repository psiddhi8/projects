package imagemodel;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the img.Pixel class. These tests ensure that the {@link Pixel} is implemented
 * in the correct way with rgb values in bounds of 0 and 255 and positive x and y values.
 * It also checks that Pixel prints in the way the ppm p3 file would want. The final tests are to
 * ensure that when multiplying an rgb value by a modifier that all goes well arithmetically.
 */
public class PixelTest {
  Pixel random;
  Pixel white;
  Pixel black;
  List<Double> result;

  @Before
  public void setUp() {
    random = new Pixel(3, 3, 40, 30, 112);
    white = new Pixel(5, 12, 255, 255, 255);
    black = new Pixel(12, 4, 0, 0, 0);
    result = new ArrayList<Double>(Arrays.asList(12.0, 9.0, 33.6));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidXParam() {
    new Pixel(-1, 3, 23, 123, 255);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidYParam() {
    new Pixel(1, -3, 23, 123, 255);
  }

  @Test
  public void testToString() {
    assertEquals("40 30 112  ", random.toString());
    assertEquals("0 255 34  ", new Pixel(12, 1, -3, 1234, 34).toString());
  }

  @Test
  public void testApplyToR() {
    assertEquals(140011.6, random.applyToR(3500.29), 0.001);
    assertEquals(0.0, black.applyToR(123.123123), 0.001);
  }

  @Test
  public void testApplyToG() {
    assertEquals(105008.7, random.applyToG(3500.29), 0.001);
    assertEquals(0.0, black.applyToR(123.123123), 0.001);
  }

  @Test
  public void testApplyToB() {
    assertEquals(392032.48, random.applyToB(3500.29), 0.001);
    assertEquals(0.0, black.applyToR(123.123123), 0.001);
  }

  @Test
  public void testApplyToAllChannels() {
    assertEquals(result, random.applyToAllChannels(0.3));
    assertEquals(new ArrayList<Double>(Arrays.asList(0.0, 0.0, 0.0)),
            black.applyToAllChannels(123.123123));
  }

  @Test
  public void testGetCoords() {
    assertEquals("[3, 3]", random.getCoords().toString());
  }

  @Test
  public void testGetColor() {
    assertEquals("[40, 30, 112]", random.getColor().toString());
  }
}
package view;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * This class tests that the test view is working as it should.
 */
public class TextViewTest {
  public Scanner sc;
  public Readable rd;
  public Appendable ap;
  String input;
  public ITextView tw;

  @Before
  public void setUp() {
    input = "load image \n35 40\n";
    rd = new StringReader(input);
    sc = new Scanner(rd);
    ap = new StringBuffer();
    tw = new TextView(rd, ap);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadableNull() {
    new TextView(null, ap);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAppendableNull() {
    new TextView(rd, null);
  }

  @Test
  public void testGetInput() throws IOException {
    assertEquals("", this.ap.toString());
    assertEquals("load image ", tw.getInput());
    assertEquals("Enter input: ", this.ap.toString());
    assertEquals("35 40", tw.getInput());
    assertEquals("Enter input: Enter input: ", this.ap.toString());
    assertEquals(null, tw.getInput());
    assertEquals("Enter input: Enter input: Enter input: Unable to read input.",
            this.ap.toString());
  }

  @Test
  public void testDisplayOutput() throws IOException {
    assertEquals("", this.ap.toString());
    tw.displayOutput("hello.");
    assertEquals("hello.", this.ap.toString());
  }
}

package controller;

import filecontroller.FileController;
import filecontroller.IFileController;
import imagemodel.IImage;
import layermodel.ILayer;
import layermodel.Layer;
import org.junit.Before;
import org.junit.Test;
import view.ITextView;
import view.TextView;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the Controller class and ensures that it is working as it supposed to in
 * handling input and rendering output.
 */
public class TextControllerTest {

  public IFileController fileController;
  public Appendable ap;
  public Readable rd;
  String input;
  public ITextView view;
  public ILayer model;
  public IImage img;
  public List<IImage> imgs;
  public TextController textController;

  @Before
  public void setUp() {
    fileController = new FileController();
    ap = new StringBuffer();
    try {
      img = fileController.readImage("res/flower.ppm");
    } catch (IOException e) {
      throw new IllegalArgumentException("noFile.");
    }
    imgs = new ArrayList<>(Arrays.asList(img));
    model = new Layer(imgs, imgs.get(0).getProps());

  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullView() {
    new TextController(null, model);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullModel() {
    new TextController(view, null);
  }

  @Test
  public void testInvalidFirstWordInputted() throws IOException {
    input = "grow image 3 \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Unable to perform that operation!\n"
            + "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidSecondWordInputtedForLoad() throws IOException {
    input = "load notjf res/flower.ppm \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Unknown asset to load.\n"
            + "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testNoThirdWordInputtedForLoad() throws IOException {
    input = "load image \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Invalid number of arguments.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidThirdWordInputtedForLoad() throws IOException {
    input = "load image fklgvdf \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: We cannot read that file type." + System.lineSeparator() +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testNumberOfArgumentsThatIsntExit() throws IOException {
    input = "load \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Invalid number of arguments.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testExit() throws IOException {
    input = "exit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidModifierForApply() throws IOException {
    input = "apply sdjf\nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Cannot apply that modifier!\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidIntForApplyMosaic() throws IOException {
    input = "apply mosaic a\nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Mosaic requires an integer input.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidWidthForApplyDownscale() throws IOException {
    input = "apply downscale a 100\nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Downscale requires integers for width and height.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidHeightForApplyDownscale() throws IOException {
    input = "apply downscale 100 a\nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Downscale requires integers for width and height.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testSetWithNotAnInteger() throws IOException {
    input = "set a\nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Must enter integer.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testSetWithInvalidInteger() throws IOException {
    input = "set 0 \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Image at 0 does not exist." + System.lineSeparator() +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }


  @Test
  public void testInvalidSecondWordForSave() throws IOException {
    input = "save sfj not \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Unable to perform that operation!\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidThirdArgForSave() throws IOException {
    input = "save image fkvmf \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Invalid number of arguments.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidFileNameForSave() throws IOException {
    input = "save image current boot \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Invalid file extension on boot." + System.lineSeparator() +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidLayerNumberForSave() throws IOException {
    input = "save image 2 2.jpg \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Layer at this index does not exist.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }


  @Test
  public void testToggleImageAtALetterNotInt() throws IOException {
    input = "toggle a \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Must enter integer after toggle.\n" +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testToggleInvalidInt() throws IOException {
    input = "toggle 0 \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Image at 0 does not exist." + System.lineSeparator() +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

  @Test
  public void testInvalidFileExtensionOnExport() throws IOException {
    input = "export image.doc \nexit";
    rd = new StringReader(input);
    view = new TextView(rd, ap);
    textController = new TextController(view, model);
    assertEquals("", this.ap.toString());
    textController.start();
    assertEquals("Enter input: Enter valid image file type." + System.lineSeparator() +
            "Enter input: All unsaved changes will be lost.\n", this.ap.toString());
  }

}

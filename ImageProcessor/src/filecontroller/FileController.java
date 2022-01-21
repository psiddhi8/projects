package filecontroller;

import imagemodel.IImage;
import imagemodel.IPixel;
import imagemodel.Image;
import imagemodel.Pixel;
import layermodel.ILayer;
import layermodel.Layer;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.io.FileOutputStream;


/**
 * This class contains methods for the FileController. This class allows for creation of images
 * based on the input image files, such as (ppm, bmp, jpeg, and png) or the reading of a state which
 * is a layer file that is in a txt file. It results in am image or a layer that can be modified.
 * It also can write an image or a layer to a file in the users choosing folder when
 * given a file name and the contents of the file.
 */
public class FileController implements IFileController {

  @Override
  public IImage readImage(String filename) throws IOException {
    String[] nameComps = filename.split("\\.");
    String fileExtension = nameComps[nameComps.length - 1].toLowerCase(Locale.ROOT);

    switch (fileExtension) {
      case "ppm":
        return readPPM(filename);
      case "jpeg":
      case "png":
      case "jpg":
        return readOtherImg(filename);
      default:
        throw new UnsupportedOperationException("We cannot read that file type.");
    }
  }

  private IImage readPPM(String filename) throws FileNotFoundException {
    Scanner sc;
    try {
      sc = new Scanner(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException("File " + filename + " not found!");
    }
    StringBuilder builder = new StringBuilder();
    //read the file line by line, and populate a string. This will throw away any comment lines
    while (sc.hasNextLine()) {
      String s = sc.nextLine();
      if (s.charAt(0) != '#') {
        builder.append(s).append(System.lineSeparator());
      }
    }

    //now set up the scanner to read from the string we just built
    sc = new Scanner(builder.toString());

    String token;

    token = sc.next();
    if (!token.equals("P3")) {
      throw new FileNotFoundException("Invalid PPM file: plain RAW file should begin with P3");
    }

    return this.makeImage(sc);
  }

  private IImage makeImage(Scanner sc) {
    List<IPixel> pix = new ArrayList<>();

    int width = sc.nextInt();
    int height = sc.nextInt();
    int maxValue = sc.nextInt();

    for (int i = 0; i < height; i++) { //COL
      for (int j = 0; j < width; j++) { //ROW
        int r = sc.nextInt();
        int g = sc.nextInt();
        int b = sc.nextInt();

        pix.add(new Pixel(j, i, r, g, b));
      }
    }
    return new Image(pix, width, height, maxValue);
  }

  private IImage readOtherImg(String filename) throws IOException {
    BufferedImage img = ImageIO.read(new File(filename));

    int height = img.getHeight();
    int width = img.getWidth();
    int depth = (img.getColorModel().getPixelSize() * 8) - 1;

    List<IPixel> pixels = new ArrayList<>();

    for (int j = 0; j < height; j++) {
      for (int i = 0; i < width; i++) {
        Color c = new Color(img.getRGB(i, j));
        pixels.add(new Pixel(i, j, c.getRed(), c.getGreen(), c.getBlue()));
      }
    }

    return new Image(pixels, width, height, depth);
  }

  @Override
  public ILayer readState(String filename) throws FileNotFoundException {
    File f = new File(filename);
    Scanner s;

    try {
      s = new Scanner(f);
    } catch (FileNotFoundException e) {
      throw new FileNotFoundException("File " + filename + " not found!");
    }
    StringBuilder contents = new StringBuilder();

    while (s.hasNext()) {
      contents.append(s.nextLine()).append(System.lineSeparator());
    }

    s = new Scanner(contents.toString());

    if (!s.next().equals("LAYER")) {
      throw new FileNotFoundException("This is not a valid layer state");
    }

    int numImgs = s.nextInt();
    ILayer layer = new Layer(new ArrayList<>(Arrays.asList(s.nextInt(), s.nextInt(), s.nextInt())));

    for (int i = 0; i < numImgs; i++) {
      boolean visible = Boolean.parseBoolean(s.next());
      layer.addLayer(this.makeImage(s));
      if (!visible) {
        layer.toggleVisibility(i);
      }
    }

    return layer;
  }

  @Override
  public void writeImage(String filename, String extension, IImage contents) throws IOException {
    BufferedImage b = contents.createImage();
    File f = new File(filename + "." + extension);
    switch (extension) {
      case "ppm":
        writeTextOrPPM(filename, extension, "P3\n" + contents.toString());
        break;
      case "png":
        ImageIO.write(b, "PNG", f);
        break;
      case "jpeg":
        ImageIO.write(b, "JPEG", f);
        break;
      case "jpg":
        ImageIO.write(b, "JPG", f);
        break;
      default:
        throw new IllegalArgumentException("Enter valid image file type.");
    }
  }

  @Override
  public void writeTextOrPPM(String filename, String extension, String contents)
          throws IOException {
    FileOutputStream out = new FileOutputStream(filename + "." + extension);
    out.write(contents.getBytes(StandardCharsets.UTF_8));
    out.close();
  }
}
package filecontroller;

import imagemodel.IImage;
import layermodel.ILayer;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This interface allows for a file to be parsed and written into the res/ folder. It allows for
 * an IImage to be created based on the input file name .
 */
public interface IFileController {

  /**
   * Reads the input file.
   *
   * @param filename of the file to be read
   * @return an IImage
   * @throws IOException if file cannot be parsed
   */
  IImage readImage(String filename) throws IOException;

  /**
   * Reads the text file into an ILayer.
   *
   * @param filename String
   * @return an ILayer of the txt file
   * @throws FileNotFoundException if file is not found
   */
  ILayer readState(String filename) throws FileNotFoundException;

  /**
   * Exports an image file to the res/ folder.
   *
   * @param filename  to be created
   * @param extension image file extension
   * @param image     IImage that needs to be exported
   * @throws IOException if file cannot be created
   */
  void writeImage(String filename, String extension, IImage image) throws IOException;

  /**
   * Writes a text or ppm file into the res/ folder.
   *
   * @param filename  of the file to be written
   * @param extension file extension
   * @param contents  the information to be written in the file
   * @throws IOException if file cannot be made
   */
  void writeTextOrPPM(String filename, String extension, String contents) throws IOException;

}

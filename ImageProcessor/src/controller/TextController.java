package controller;

import filecontroller.FileController;
import filecontroller.IFileController;
import filter.Blur;
import filter.DownScale;
import filter.Greyscale;
import filter.IModifier;
import filter.Mosaic;
import filter.Sepia;
import filter.Sharpen;
import java.io.FileNotFoundException;
import java.io.IOException;
import layermodel.ILayer;
import view.ITextView;

/**
 * This class represents an implementation of IController that allows delegation to the view and
 * model as well as input and output control. This controller allows a models state to be saved
 * as a file as well as reading through an input file to delegate commands.
 */
public class TextController implements IController {

  private final IFileController fileController;
  private final ITextView view;
  private ILayer model;
  private boolean running;

  /**
   * Creates a controller object.
   *
   * @param view  a View object
   * @param model An ILayer object.
   * @throws IllegalArgumentException if arguments are bull
   */
  public TextController(ITextView view, ILayer model) {
    if (view == null || model == null) {
      throw new IllegalArgumentException("Arguments are null.");
    }
    this.fileController = new FileController();
    this.view = view;
    this.model = model;
    this.running = true;
  }

  @Override
  public void start() throws IOException {
    while (this.running) {
      this.handleInput(this.view.getInput());
    }
  }

  private void handleInput(String input) throws IOException {
    String[] components = input.split(" ");

    if (components.length < 2) {
      if (components[0].equalsIgnoreCase("exit")) {
        this.view.displayOutput("All unsaved changes will be lost.\n");
        this.running = false;
      } else {
        this.view.displayOutput("Invalid number of arguments.\n");
      }
    } else {
      switch (components[0]) {
        case "load":
          this.loadInputHandler(components);
          break;
        case "apply":
          IModifier modifier = this.getModifier(components);
          if (modifier != null) {
            if (components[1].equalsIgnoreCase("downscale")) {
              try {
                model.alterLayer(modifier, Integer.parseInt(components[2]),
                        Integer.parseInt(components[3]));
              } catch (IllegalArgumentException e) {
                this.view.displayOutput(e.getMessage() + System.lineSeparator());
              }
            } else {
              try {
                model.applyToCurrent(modifier);
              } catch (IllegalArgumentException e) {
                this.view.displayOutput(e.getMessage() + System.lineSeparator());
              }
            }
          }
          break;
        case "set":
          try {
            int num = Integer.parseInt(components[1]);
            try {
              model.setCurrent(num);
            } catch (IllegalArgumentException e) {
              this.view.displayOutput(e.getMessage() + System.lineSeparator());
            }
          } catch (NumberFormatException e) {
            this.view.displayOutput("Must enter integer.\n");
          }
          break;
        case "save":
          try {
            this.saveInputHandler(components);
          } catch (IOException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        case "toggle":
          try {
            int num = Integer.parseInt(components[1]);
            try {
              this.model.toggleVisibility(num);
            } catch (IllegalArgumentException e) {
              this.view.displayOutput(e.getMessage() + System.lineSeparator());
            }
          } catch (NumberFormatException e) {
            this.view.displayOutput("Must enter integer after toggle.\n");
          }
          break;
        case "export":
          String[] subcomps = components[1].split("\\.");
          try {
            this.fileController.writeImage(subcomps[0], subcomps[1], this.model.blend());
          } catch (IllegalArgumentException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        default:
          this.view.displayOutput("Unable to perform that operation!\n");
      }
    }
  }

  private void saveInputHandler(String[] args) throws IOException {
    if (args.length < 3) {
      this.view.displayOutput("Invalid number of arguments.\n");
    } else {
      switch (args[1]) {
        case "image":
          try {
            this.saveImageHandlerHelper(args);
          } catch (IOException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        case "state":
          try {
            this.saveState(args[2]);
          } catch (IOException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        default:
          this.view.displayOutput("Unable to perform that operation!\n");
      }
    }
  }

  private void saveImageHandlerHelper(String[] args) throws IOException {
    if (args.length < 4) {
      this.view.displayOutput("Invalid number of arguments.\n");
    } else {
      String[] subcomps = args[3].split("\\.");
      if (subcomps.length < 2) {
        this.view.displayOutput("Invalid file extension on " + args[3] + "."
                + System.lineSeparator());
      } else {
        if ("current".equals(args[2])) {
          this.fileController.writeImage(subcomps[0], subcomps[1], this.model.getCurrent());
        } else {
          try {
            this.fileController.writeImage(subcomps[0], subcomps[1],
                    this.model.getLayer(Integer.parseInt(args[2])));
          } catch (IllegalArgumentException e) {
            this.view.displayOutput("Layer at this index does not exist.\n");
          }
        }
      }
    }
  }

  private void loadInputHandler(String[] args) throws IOException {
    if (args.length < 3) {
      this.view.displayOutput("Invalid number of arguments.\n");
    } else {
      switch (args[1]) {
        case "image":
          try {
            this.model.addLayer(fileController.readImage(args[2]));
          } catch (UnsupportedOperationException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        case "state":
          try {
            this.model = this.fileController.readState(args[2]);
          } catch (FileNotFoundException e) {
            this.view.displayOutput(e.getMessage() + System.lineSeparator());
          }
          break;
        default:
          this.view.displayOutput("Unknown asset to load.\n");
      }
    }
  }

  private IModifier getModifier(String[] args) throws IOException {
    switch (args[1]) {
      case "blur":
        return new Blur();
      case "sharpen":
        return new Sharpen();
      case "sepia":
        return new Sepia();
      case "greyscale":
        return new Greyscale();
      case "mosaic":
        try {
          int seed = Integer.parseInt(args[2]);
          return new Mosaic(seed);
        } catch (NumberFormatException e) {
          this.view.displayOutput("Mosaic requires an integer input.\n");
          return null;
        }
      case "downscale":
        try {
          int width = Integer.parseInt(args[2]);
          int height = Integer.parseInt(args[3]);
          return new DownScale(width, height);
        } catch (NumberFormatException e) {
          this.view.displayOutput("Downscale requires integers for width and height.\n");
          return null;
        }
      default:
        this.view.displayOutput("Cannot apply that modifier!\n");
        return null;
    }
  }

  private void saveState(String stateName) throws IOException {
    fileController.writeTextOrPPM(stateName, "txt", model.toString());
  }
}
package view;

import imagemodel.IImage;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import layermodel.IROLayer;
import view.dialogs.AlertBox;
import view.dialogs.CurrentDetails;
import view.dialogs.DownscaleDetails;
import view.dialogs.ExportImage;
import view.dialogs.LoadFile;
import view.dialogs.MosaicDetails;
import view.dialogs.SaveState;
import view.dialogs.ToggleDetails;

/**
 * This class represents a GUI view of an Image processing program. This class creates a view that
 * shows the buttons, panels, and a scroll pane. This class also shows error messages. This class
 * takes in a title and a IROLayer which is a read only layer. This allows the view to get
 * components of the layer images without being about to edit it in any way.
 */
public class GraphicalView implements IGraphicalView {

  private JPanel panel1;
  private JButton blurBtn;
  private JButton sharpenButton;
  private JButton sepiaButton;
  private JButton greyscaleButton;
  private JButton downscaleButton;
  private JButton mosaicButton;
  private JButton loadButton;
  private JButton saveButton;
  private JButton exportButton;
  private JButton setCurrentButton;
  private JButton toggleVisibilityButton;
  private JButton blendAllLayersButton;
  private JTextField layerToolsTextField;
  private JTextArea imageModifiersTextArea;
  private JScrollPane imageDisplay;
  private JFrame frame;
  private ActionListener listener;

  private IROLayer roLayer;

  /**
   * Creates a GraphicalView object.
   *
   * @param title   String that appears at the top of the GUI frame
   * @param roLayer Read only layer object which will allow the view to access observers for the
   *                model.
   */
  public GraphicalView(String title, IROLayer roLayer) {
    this.initialise(title);
    this.roLayer = roLayer;
  }

  private void initialise(String title) {
    frame = new JFrame(title);
    frame.setContentPane(this.panel1);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
  }

  @Override
  public void display() {
    this.frame.setVisible(true);
  }

  @Override
  public void refresh() {
    IImage current = this.roLayer.getCurrentVisible();
    if (current != null) {
      this.imageDisplay.setViewportView(new JLabel(new ImageIcon(current.createImage())));
    } else {
      this.imageDisplay.setViewportView(new JLabel(new ImageIcon()));
    }
  }

  @Override
  public void alert(String message) {
    new AlertBox(message);
  }

  @Override
  public void setModel(IROLayer model) {
    this.roLayer = model;
  }

  @Override
  public List<String> dialogHandler(String identifier) {
    switch (identifier) {
      case "mosaic":
        return new MosaicDetails().getResults();
      case "downscale":
        return new DownscaleDetails().getResults();
      case "toggle":
        return new ToggleDetails().getResults();
      case "set":
        return new CurrentDetails().getResults();
      case "load":
        return new LoadFile().getResults();
      case "save":
        return new SaveState().getResults();
      case "export":
        return new ExportImage().getResults();
      default:
        return null;
    }
  }

  @Override
  public void setListener(ActionListener listener) {
    this.listener = listener;
    this.setActionListeners();
  }

  private void setActionListeners() {
    blurBtn.addActionListener(listener);
    sharpenButton.addActionListener(listener);
    sepiaButton.addActionListener(listener);
    greyscaleButton.addActionListener(listener);
    downscaleButton.addActionListener(listener);
    mosaicButton.addActionListener(listener);
    loadButton.addActionListener(listener);
    saveButton.addActionListener(listener);
    exportButton.addActionListener(listener);
    setCurrentButton.addActionListener(listener);
    toggleVisibilityButton.addActionListener(listener);
    blendAllLayersButton.addActionListener(listener);
  }

  private void createUIComponents() {
    this.imageDisplay = new JScrollPane();
  }
}

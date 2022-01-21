package view.dialogs;

import view.IDialogView;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the visual way to export an image in the graphical view. This class
 * creates a window that asks the user to input a file name for the current image they wish to save.
 * The constructor for this class is what creates the file chooser popup. This class extends
 * JDialog and implements IDialogView.
 */
public class ExportImage extends JDialog implements IDialogView {

  private JPanel contentPane;
  private final List<String> res;

  /**
   * Creates an export image object which creates the GUI visual of the file chooser.
   */
  public ExportImage() {
    JFileChooser fileChooser = new JFileChooser();

    setContentPane(contentPane);
    setModal(true);
    setResizable(false);
    res = new ArrayList<>();

    contentPane.add(fileChooser);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("png", "png"));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("jpg", "jpg"));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("ppm", "ppm"));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("jpeg", "jpeg"));
    int result = fileChooser.showSaveDialog(this.contentPane);

    if (result == JFileChooser.APPROVE_OPTION) {
      res.add(fileChooser.getSelectedFile().getAbsolutePath());
      res.add(fileChooser.getFileFilter().getDescription());
    }
  }


  @Override
  public List<String> getResults() {
    return new ArrayList<>(this.res);
  }

  private void createUIComponents() {
    this.contentPane = new JPanel();
  }
}

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
 * This class represents the GUI representation to load a file. When the load button is pushed,
 * this class should be called in order to create a file chooser pane that will allow the user to
 * upload an image of their choosing. This class extends JDialog and implements IDialogView.
 */
public class LoadFile extends JDialog implements IDialogView {
  private JPanel contentPane;
  private final List<String> res = new ArrayList<>();

  /**
   * Creates a LoadFile object which initializes the GUI file chooser of a txt or img file.
   */
  public LoadFile() {
    JFileChooser fileChooser = new JFileChooser();

    fileChooser.setPreferredSize(contentPane.getPreferredSize());
    setContentPane(contentPane);
    setModal(true);
    setResizable(false);

    contentPane.add(fileChooser);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Image", "jpg", "png", "ppm",
            "jpeg"));
    fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("State", "txt"));
    res.add(fileChooser.getFileFilter().getDescription());

    int result = fileChooser.showOpenDialog(this.contentPane);

    if (result == JFileChooser.APPROVE_OPTION) {
      System.out.println("File loaded...");
      res.add(fileChooser.getSelectedFile().getAbsolutePath());
    } else if (result == JFileChooser.CANCEL_OPTION) {
      System.out.println("No file loaded!");
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

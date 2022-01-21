package view.dialogs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import view.IDialogView;

/**
 * This class represents the visual representation of saving an ILayer file. This class creates
 * a file chooser which will allow the user to choose where and to what name they wish to save the
 * layer that is currently loaded. This class extends JDialog and implements IDialogView.
 */
public class SaveState extends JDialog implements IDialogView {

  private JPanel contentPane;
  private final List<String> res;

  /**
   * Creates a SaveState object which will render the file chooser for the user to folder to save
   * the image in as well as enter a file name.
   */
  public SaveState() {
    JFileChooser fileChooser = new JFileChooser();

    setContentPane(contentPane);
    setModal(true);
    setResizable(false);
    res = new ArrayList<>();

    contentPane.add(fileChooser);
    fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    fileChooser.setAcceptAllFileFilterUsed(false);
    fileChooser.setFileFilter(new FileNameExtensionFilter("State", "txt"));

    int result = fileChooser.showSaveDialog(this.contentPane);

    if (result == JFileChooser.APPROVE_OPTION) {
      System.out.println("File saved...");
      res.add(fileChooser.getSelectedFile().getAbsolutePath());
    } else if (result == JFileChooser.CANCEL_OPTION) {
      System.out.println("State not saved!");
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

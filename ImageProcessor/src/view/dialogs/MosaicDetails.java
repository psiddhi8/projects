package view.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import view.IDialogView;

/**
 * This class represents the GUI representation of the Mosaic. When the Mosaic button is pushed,
 * this class essentially should create a pop up panel that asks the user to input the number of
 * seeds to create the mosaic image.This will be sent to get results in order to apply the mosaic
 * image. This class extends JDialog and implements IDialogView.
 */
public class MosaicDetails extends JDialog implements IDialogView {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextField asTextField;
  private JTextArea enterNumberOfSeedsTextArea;
  private List<String> res;

  /**
   * Creates a MosaicDetails object which will render the window where the user inputs the number
   * of seeds.
   */
  public MosaicDetails() {
    setContentPane(contentPane);
    setModal(true);
    setResizable(false);
    getRootPane().setDefaultButton(buttonOK);
    this.res = new ArrayList<>();

    buttonOK.addActionListener(e -> onOK());

    buttonCancel.addActionListener(e -> onCancel());

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        onCancel();
      }
    });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(),
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    pack();
    setLocationRelativeTo(null);
    setVisible(true);
  }

  private void onOK() {
    res.add(asTextField.getText());
    dispose();
  }

  private void onCancel() {
    this.res = null;
    dispose();
  }

  @Override
  public List<String> getResults() {
    if (this.res != null) {
      return new ArrayList<>(this.res);
    }
    return null;
  }
}

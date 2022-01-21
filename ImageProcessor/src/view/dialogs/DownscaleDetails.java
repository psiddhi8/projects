package view.dialogs;

import view.IDialogView;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the GUI for Downscaling. When the downscale button is pushed, this class
 * is called which renders an image of a box asking the user to input the width and height that
 * they wish to downscale the current image to.
 * This class extends JDialog and implements IDialogView.
 */
public class DownscaleDetails extends JDialog implements IDialogView {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextArea enterTheHeightOfTextArea;
  private JTextField widthField;
  private JTextArea enterTheWidthOfTextArea;
  private JTextField heightField;

  private List<String> res;

  /**
   * Creates a DownscaleDetails Object that renders the visual of the window asking for a
   * width and height to downscale to.
   */
  public DownscaleDetails() {
    setContentPane(contentPane);
    setModal(true);
    setResizable(false);
    getRootPane().setDefaultButton(buttonOK);

    res = new ArrayList<>();

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
    this.res.add(this.widthField.getText());
    this.res.add(this.heightField.getText());
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

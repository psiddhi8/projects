package view.dialogs;

import view.IDialogView;


import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the GUI representation of set Current. Whenever this class is rendered, it
 * creates a box asking for the index starting from one and when given an integer determines if it
 * can be set to current. This class extends JDialog and implements IDialogView.
 */
public class CurrentDetails extends JDialog implements IDialogView {

  private JPanel contentPane;
  private JButton buttonOK;
  private JButton buttonCancel;
  private JTextPane textPane1;
  private List<String> res;

  /**
   * Creates a CurrentDetails object which will render visually.
   */
  public CurrentDetails() {
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
    res.add(textPane1.getText());
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

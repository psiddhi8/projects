package view.dialogs;

import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

/**
 * This class represents an AlertBox which displays an error message when something goes wrong.
 * This class is the GUI representation of the error. This class extends JDialog and implements
 * IDialogView.
 */
public class AlertBox extends JDialog {

  private JPanel contentPane;
  private JButton buttonCancel;
  private JTextPane textPane1;
  private JTextPane alertTextPane;

  /**
   * Creates an AlertBox object.
   *
   * @param message String error message.
   */
  public AlertBox(String message) {
    setContentPane(contentPane);
    setModal(true);
    setResizable(false);
    getRootPane().setDefaultButton(buttonCancel);
    textPane1.setText(message);

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

  private void onCancel() {
    dispose();
  }
}

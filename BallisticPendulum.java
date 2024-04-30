import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;

public class BallisticPendulum {
    // Properties
    JFrame frame = new JFrame();
    JPanel panel = new JPanel();

    // Event Listeners

    // Methods

    // Constructor
    public BallisticPendulum() {
        panel.setPreferredSize(new Dimension(960, 540));
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    // Main
    public static void main(String[] args) {
        new BallisticPendulum();
    }
}
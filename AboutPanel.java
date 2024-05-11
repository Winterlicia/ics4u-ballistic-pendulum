
import javax.swing.JLabel;
import javax.swing.JPanel;

class AboutPanel extends JPanel {
    JLabel programmerLabel = new JLabel();
    JLabel versionLabel = new JLabel();
    JLabel courseLabel = new JLabel();

    // Constructor
    AboutPanel() {
        super();

        setLayout(null);

        programmerLabel.setText("Developed by Kaden Seto and Martin Sit");
        programmerLabel.setBounds(10, 10, 300, 20);
        add(programmerLabel);

        versionLabel.setText("Version 1.0");
        versionLabel.setBounds(10, 30, 300, 20);
        add(versionLabel);

        courseLabel.setText("St. Augustine CHS Computer Science - ICS4U1 - Mr. Cadawas");
        courseLabel.setBounds(10, 50, 500, 20);
        add(courseLabel);
    }
}


import javax.swing.JLabel;
import javax.swing.JPanel;

class AboutPanel extends JPanel {
    JLabel programmerLabel = new JLabel("Developed by Kaden Seto and Martin Sit");
    JLabel versionLabel = new JLabel("Version 1.0");
    JLabel courseLabel = new JLabel("St. Augustine CHS Computer Science - ICS4U1");
    JLabel teacherLabel = new JLabel("Taught by Mr. Cadawas");

    // Constructor
    AboutPanel() {
        super();
        // Set the layout to null
        setLayout(null);

        // Set the size and location of the programmerLabel
        programmerLabel.setSize(400, 40);
        programmerLabel.setFont(programmerLabel.getFont().deriveFont(16.0f));
        programmerLabel.setLocation(10, 10);
        add(programmerLabel);

        // Set the size and location of the versionLabel
        versionLabel.setSize(400, 40);
        versionLabel.setFont(versionLabel.getFont().deriveFont(16.0f));
        versionLabel.setLocation(10, 50);
        add(versionLabel);

        // Set the size and location of the courseLabel
        courseLabel.setSize(400, 40);
        courseLabel.setFont(courseLabel.getFont().deriveFont(16.0f));
        courseLabel.setLocation(10, 90);
        add(courseLabel);

        // Set the size and location of the teacherLabel
        teacherLabel.setSize(400, 40);
        teacherLabel.setFont(teacherLabel.getFont().deriveFont(16.0f));
        teacherLabel.setLocation(10, 130);
        add(teacherLabel);
    }
}

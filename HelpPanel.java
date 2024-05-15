import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Draws the help panel on the screen
 */
public class HelpPanel extends JPanel {
    /**
     * The image of the tutorial that will be displayed on the screen
     */
    BufferedImage imgHelp;

    /**
     * Draws the tutorial image
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(imgHelp, 105, 60, 750, 420, null);
    }

    // Constructor
    /**
     * Creates a new HelpPanel object
     */
    HelpPanel() {
        super();

        // Set the layout to null
        setLayout(null);

        //Try to read the image from both the jar file and local drive
        InputStream helpClass = this.getClass().getResourceAsStream("HelpPanel.png");

        if (helpClass != null) {
            try {
                imgHelp = ImageIO.read(helpClass);
            } catch (IOException e) {
                System.out.println("Unable to read/load image from jar");
                e.printStackTrace();
            }
        } else { //If it can't be found on the jar, search it locally
            try {
                imgHelp = ImageIO.read(new File("HelpPanel.png"));
            } catch (IOException e) {
                System.out.println("Unable to read/load image");
                e.printStackTrace();
            }
        }
    }
}

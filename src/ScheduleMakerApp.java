package src;

import javax.swing.SwingUtilities;

public class ScheduleMakerApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ScheduleMakerGUI().setVisible(true);
        });
    }
}

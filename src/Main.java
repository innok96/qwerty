import javax.swing.*;
import java.lang.String;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        GUI app = new GUI();
        app.setVisible(true);
    }
}

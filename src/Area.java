import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Area extends JFrame {
    public Area(BufferedImage imag, int x1, int y1, int x2, int y2){
        super("Область");
        setLayout(new GridLayout(1, 1));
        setBounds(50, 50, 350, 430);
        setResizable(false);
        JPanel panel = new JPanel(new GridLayout((x2-x1+1)*(y2-y1+1), 3));
        FileWriter writer=null; FileReader reader=null;
        try {
            writer = new FileWriter("out.txt", false);
        } catch (Exception e){}
        try {
            reader = new FileReader("out.txt");
        } catch (Exception e) {}

        for(int i=x1; i<=x2; i++){
            for(int j=y1; j<=y2; j++){
                int rgb1 = imag.getRGB(i, j);
                RGB rgb = new RGB(rgb1);
                double hsv[] = rgb.getHSV();
                double lab[] = rgb.getLAB();
                try {
                    writer.write("RGB in point (" + i + "," + j + ") is (" + rgb.R + "," + rgb.G + "," + rgb.B + ")  ");
                    writer.write("HSV in point (" + i + "," + j + ") is (" + (int) hsv[0] + "," + (int) hsv[1] + "," + (int) hsv[2] + ")  ");
                    writer.write("LAB in point (" + i + "," + j + ") is (" + (int) lab[0] + "," + (int) lab[1] + "," + (int) lab[2] + ")");
                    writer.append('\n');
                } catch(Exception e){
                    System.out.println("Fail");
                }
            }
        }
        try {
            writer.flush();
        } catch (IOException e) {}
        JTextArea textArea = new JTextArea();
        try{
            textArea.read(reader, false);
        } catch(Exception e){}
        JScrollPane sc = new JScrollPane(textArea);
        add(sc);
    }
}

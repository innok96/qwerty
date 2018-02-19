import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.abs;

public class GUI extends JFrame{
    private BufferedImage imag;
    private BufferedImage imag2;
    private BufferedImage imag3;
    private JLabel outRGB, hueLabel, satLabel, valLabel;
    private int startX=-1, startY=-1;
    private int x1=-1, y1=-1, x2=-1, y2=-1;
    Graphics g;
    JSpinner hueSpinner, valSpinner, satSpinner;
    public GUI() {
        super("MyApplication");
        setResizable(false);
        setBounds(0, 0, 1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container=getContentPane();
        container.setLayout(null);

        JButton showRGB = new JButton("Показать RGB, HSV, LAB");
        outRGB = new JLabel();
        add(outRGB);

        hueSpinner = new JSpinner(); hueLabel = new JLabel("Изменить тон");
        satSpinner = new JSpinner(); satLabel = new JLabel("Изменить насыщенность");
        valSpinner = new JSpinner(); valLabel = new JLabel("Изменить объем");

        JButton Lbutton = new JButton("Показать гистограмму L-компоненты");
        JButton abutton = new JButton("Показать гистограмму a-компоненты");
        JButton bbutton = new JButton("Показать гистограмму b-компоненты");

        JMenuBar menu=new JMenuBar();
        setJMenuBar(menu);
        JMenu fileMenu = new  JMenu("Файл");
        menu.add(fileMenu);

        JLabel label = new JLabel();
        JScrollPane spanel = new JScrollPane(label);

        container.add(spanel);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //JOptionPane.showMessageDialog(null, "was clicked");
                g.clearRect(0, 0, 10000, 10000);
                g.drawImage(imag2, 0, 0, new ImageObserver() {
                    @Override
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
                x1=y1=x2=y2=-1;
                repaint();
            }
        });

        label.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                //JOptionPane.showMessageDialog(null, "Mouse was dragged");
                if(startX==-1){ startX = e.getX(); startY = e.getY();}
                //outRGB.setText(saySomething("Mouse dragged", e));
                //outRGB.setBounds(5, spanel.getHeight(), 600, 10);
                label.repaint();
                g = imag.getGraphics();
                g.setColor(Color.BLUE);
                g.drawImage(imag2, 0, 0, new ImageObserver() {
                    @Override
                    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                        return false;
                    }
                });
                if(e.getXOnScreen()>1200){
                    int val = spanel.getHorizontalScrollBar().getValue();
                    spanel.getHorizontalScrollBar().setValue(val+10);
                }
                if(e.getXOnScreen()<50){
                    int val = spanel.getHorizontalScrollBar().getValue();
                    spanel.getHorizontalScrollBar().setValue(val-10);
                }
                if(e.getYOnScreen()>680){
                    int val = spanel.getVerticalScrollBar().getValue();
                    spanel.getVerticalScrollBar().setValue(val+10);
                }
                if(e.getYOnScreen()<100){
                    int val = spanel.getVerticalScrollBar().getValue();
                    spanel.getVerticalScrollBar().setValue(val-10);
                }
                g.drawRect(max(0, min(startX, e.getX())), max(0, min(startY, e.getY())), abs(min(imag.getWidth()-1, max(0, e.getX()))-startX), abs(min(imag.getHeight()-1, max(0, e.getY()))-startY));
                x1=min(startX, e.getX()); y1=min(startY, e.getY()); x2=x1+abs(e.getX()-startX); y2=y1+abs(e.getY()-startY);
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //JOptionPane.showMessageDialog(null, "Mouse was moved in point "+e.getX() + " "+e.getY());
                startX=startY=-1;
                int rgb1 = imag.getRGB(e.getX(), e.getY());
                RGB rgb = new RGB(rgb1);
                double hsv[] = rgb.getHSV();
                double lab[] = rgb.getLAB();
                outRGB.setText("RGB = ("+rgb.R+","+rgb.G+","+rgb.B+") HSV = ("+(int)hsv[0]+","+(int)hsv[1]+","+(int)hsv[2]+") LAB = ("+(int)lab[0]+","+(int)lab[1]+","+(int)lab[2]+")");
                outRGB.setBounds(5, spanel.getHeight(), 1000, 30);
            }
        });
        Action loadAction = new  AbstractAction("Загрузить") {
            public void actionPerformed(ActionEvent event) {
                JFileChooser jf= new  JFileChooser();
                int  result = jf.showOpenDialog(null);
                if(result==JFileChooser.APPROVE_OPTION) {
                    try {
                        String fileName = jf.getSelectedFile().getAbsolutePath();
                        imag = null;
                        try {
                            imag = ImageIO.read(new File(fileName));
                            imag2 = ImageIO.read(new File(fileName));
                            imag3 = ImageIO.read(new File(fileName));
                        } catch (IOException e) {}
                        ImageIcon i = new ImageIcon(imag);
                        g=imag.getGraphics();
                        spanel.setBounds(0, 0, min(1280, imag.getWidth()+3), min(720, imag.getHeight()+3));
                        label.setIcon(i);
                        showRGB.setBounds(min(1280, imag.getWidth()+3)+10, 10, 200, 30);
                        hueLabel.setBounds(min(1280, imag.getWidth()+3)+10, 50, 200, 30);
                        hueSpinner.setBounds(min(1280, imag.getWidth()+3)+10, 90, 200, 30);
                        satLabel.setBounds(min(1280, imag.getWidth()+3)+10, 130, 200, 30);
                        satSpinner.setBounds(min(1280, imag.getWidth()+3)+10, 170, 200, 30);
                        valLabel.setBounds(min(1280, imag.getWidth()+3)+10, 210, 200, 30);
                        valSpinner.setBounds(min(1280, imag.getWidth()+3)+10, 250, 200, 30);
                        Lbutton.setBounds(min(1280, imag.getWidth()+3)+10, 290, 300, 30);
                        abutton.setBounds(min(1280, imag.getWidth()+3)+10, 330, 300, 30);
                        bbutton.setBounds(min(1280, imag.getWidth()+3)+10, 370, 300, 30);
                        hueSpinner.updateUI(); satSpinner.updateUI(); valSpinner.updateUI();
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Something is wrong");
                    }
                }
            }
        };
        JMenuItem loadMenu = new  JMenuItem(loadAction);
        fileMenu.add(loadMenu);

        showRGB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(x1!=-1) {
                    Area area = new Area(imag2, max(x1, 0), max(y1, 0), min(x2, imag.getWidth()-1), min(y2, imag.getHeight()-1));
                    area.setVisible(true);
                }
            }
        });
        add(showRGB);
        add(hueSpinner); add(hueLabel);
        add(satSpinner); add(satLabel);
        add(valSpinner); add(valLabel);

        JLabel lll = new JLabel();
        lll.setBounds(10, 700, 1000, 30);
        add(lll);
        hueSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) hueSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                if(val>360){
                    hueSpinner.setValue(new Integer(360));
                    return;
                }
                if(val<-360){
                    hueSpinner.setValue(new Integer(-360));
                    return;
                }
                for(int i=0; i<width; i++){
                    for(int j=0; j<height; j++){
                        RGB rgb1 = new RGB(imag3.getRGB(i, j));
                        RGB rgb2 = new RGB(imag.getRGB(i, j));
                        double H[] = rgb1.getHSV();
                        H[1]=rgb2.getHSV()[1];
                        H[2]=rgb2.getHSV()[2];
                        H[0]+=val;
                        if(H[0]>=360) H[0]-=360;
                        if(H[0]<0) H[0]+=360;
                        RGB rgb = new RGB(H);
                        imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                spanel.updateUI();
            }
        });

        satSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) satSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                if(val>100){
                    satSpinner.setValue(new Integer(100));
                    return;
                }
                if(val<-100){
                    satSpinner.setValue(new Integer(-100));
                    return;
                }
                for(int i=0; i<width; i++){
                    for(int j=0; j<height; j++){
                        RGB rgb1 = new RGB(imag3.getRGB(i, j));
                        RGB rgb2 = new RGB(imag.getRGB(i, j));
                        double H[] = rgb1.getHSV();
                        H[0]=rgb2.getHSV()[0];
                        H[2]=rgb2.getHSV()[2];
                        if (val >= 0) {
                            H[1] += (100.0 - H[1]) * (double) val / 100.0;
                        } else {
                            H[1] += H[1] * (double) val / 100.0;
                        }
                        RGB rgb = new RGB(H);
                        imag.setRGB(i, j, rgb.toInt((imag2.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                spanel.updateUI();
            }
        });

        valSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) valSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                if(val>100){
                    valSpinner.setValue(new Integer(100));
                    return;
                }
                if(val<-100){
                    valSpinner.setValue(new Integer(-100));
                    return;
                }
                for(int i=0; i<width; i++){
                    for(int j=0; j<height; j++){
                        RGB rgb1 = new RGB(imag3.getRGB(i, j));
                        RGB rgb2 = new RGB(imag.getRGB(i, j));
                        double H[] = rgb1.getHSV();
                        H[0]=rgb2.getHSV()[0];
                        H[1]=rgb2.getHSV()[1];
                        if (val >= 0) {
                            H[2] += (100.0 - H[2]) * (double) val / 100.0;
                        } else {
                            H[2] += H[2] * (double) val / 100.0;
                        }
                        RGB rgb = new RGB(H);
                        imag.setRGB(i, j, rgb.toInt((imag2.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                spanel.updateUI();
            }
        });

        add(Lbutton); add(abutton); add(bbutton);
        Lbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Histogramm('L');
            }
        });

        abutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Histogramm('a');
            }
        });

        bbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Histogramm('b');
            }
        });
    }

    private void Histogramm(char type) {
        int N = (imag.getWidth() + 1) * (imag.getHeight() + 1);
        double[] value = new double[N];
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);

        for (int i = 0; i < imag.getWidth(); i++) {
            for (int j = 0; j < imag.getHeight(); j++) {
                RGB rgb = new RGB(imag.getRGB(i, j));
                double lab[] = rgb.getLAB();
                double val = 0;
                switch (type) {
                    case 'L':
                        val = lab[0];
                        break;
                    case 'a':
                        val = lab[1];
                        break;
                    case 'b':
                        val = lab[2];
                        break;
                }
                value[i * imag.getHeight() + j] = val;
            }
        }

        dataset.addSeries("Histogram", value, 10);

        JFreeChart chart = ChartFactory.createHistogram("", "Lightness", "Percents",
                dataset, PlotOrientation.VERTICAL, false, false, false);

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.lightGray);

        ChartFrame hist = new ChartFrame("Histogram for "+type+"-component", chart);

        hist.setBackground(Color.WHITE);
        hist.pack();
        hist.setLocationRelativeTo(null); //окно по центру
        hist.setVisible(true);
    }

}

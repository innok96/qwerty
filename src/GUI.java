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
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.*;

public class GUI extends JFrame{
    private BufferedImage imag, imag2, imag3, originalImage=null;
    private JLabel outRGB, hueLabel, satLabel, valLabel, label, angleLabel;
    private JButton filterGaussButton, filterSobelButton, filterGaborButton;
    private JRadioButton angle1, angle2, angle3, angle4;
    private ButtonGroup angle;
    private int startX=-1, startY=-1;
    private int x1=-1, y1=-1, x2=-1, y2=-1;
    private Graphics g;
    private JSpinner hueSpinner, valSpinner, satSpinner;
    private double d[], D[][];
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

        filterGaussButton = new JButton("Фильтр Гаусса");
        filterGaborButton = new JButton("Фильтр Габора");
        filterSobelButton = new JButton("Фильтр Собеля");
        add(filterGaussButton); add(filterSobelButton); add(filterGaborButton);

        JButton Lbutton = new JButton("Показать гистограмму L-компоненты");
        JButton abutton = new JButton("Показать гистограмму a-компоненты");
        JButton bbutton = new JButton("Показать гистограмму b-компоненты");

        JMenuBar menu=new JMenuBar();
        setJMenuBar(menu);
        JMenu fileMenu = new  JMenu("Файл");
        menu.add(fileMenu);

        label = new JLabel();
        JScrollPane spanel = new JScrollPane(label);

        container.add(spanel);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                g.clearRect(0, 0, 10000, 10000);
                g.drawImage(imag2, 0, 0, null);
                x1=y1=x2=y2=-1;
                repaint();
            }
        });

        angleLabel = new JLabel("Выберите Тетта");
        angle1 = new JRadioButton("0", true);
        angle2 = new JRadioButton("45");
        angle3 = new JRadioButton("90");
        angle4 = new JRadioButton("135");
        angle = new ButtonGroup();
        angle.add(angle1); angle.add(angle2); angle.add(angle3); angle.add(angle4);
        add(angle1); add(angle2); add(angle3); add(angle4); add(angleLabel);

        label.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(startX==-1){ startX = e.getX(); startY = e.getY();}
                label.repaint();
                g = imag.getGraphics();
                g.setColor(Color.BLUE);
                g.drawImage(imag2, 0, 0, null);
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
                startX=startY=-1;
                if(e.getX()>=imag.getWidth() || e.getY()>=imag.getHeight()) return;
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
                            originalImage = ImageIO.read(new File(fileName));
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
                        filterGaussButton.setBounds(min(1280, imag.getWidth()+3)+10, 410, 300, 30);
                        filterSobelButton.setBounds(min(1280, imag.getWidth()+3)+10, 450, 300, 30);
                        angleLabel.setBounds(min(1280, imag.getWidth()+3)+10, 485, 300, 15);
                        angle1.setBounds(min(1280, imag.getWidth()+3)+10, 505, 60, 15);
                        angle2.setBounds(min(1280, imag.getWidth()+3)+80, 505, 60, 15);
                        angle3.setBounds(min(1280, imag.getWidth()+3)+150, 505, 60, 15);
                        angle4.setBounds(min(1280, imag.getWidth()+3)+220, 505, 60, 15);
                        filterGaborButton.setBounds(min(1280, imag.getWidth()+3)+10, 530, 300, 30);
                        hueSpinner.updateUI(); satSpinner.updateUI(); valSpinner.updateUI();
                    }
                    catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Something is wrong");
                    }
                }
            }
        };
        JMenuItem loadMenu = new  JMenuItem(loadAction);
        Action loadAction1 = new  AbstractAction("Исходное изображение") {
            public void actionPerformed(ActionEvent event) {
                if(originalImage == null) return;
                imag.getGraphics().drawImage(originalImage, 0, 0, null);
                imag3.getGraphics().drawImage(originalImage, 0, 0, null);
                imag2.getGraphics().drawImage(originalImage, 0, 0, null);
                label.updateUI();
            }
        };
        JMenuItem loadMenu1 = new  JMenuItem(loadAction1);
        fileMenu.add(loadMenu); fileMenu.add(loadMenu1);

        filterGaussButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int sigma;
                try {
                    String sign = JOptionPane.showInputDialog("Введите сигма");
                    sigma = Integer.parseInt(sign);
                } catch(Exception ex){
                    JOptionPane.showMessageDialog(null, "Некорректное значение");
                    return;
                }
                if(sigma<=0) return;
                d=new double[2*sigma+1];
                for(int i=0; i<d.length; i++){
                    double x=sigma-i;
                    d[i]=exp(-(x*x)/(2.0*sigma*sigma))/(2.*Math.acos(-1.)*sigma*sigma);
                }
                filterGauss();
                JOptionPane.showMessageDialog(null, "Finished");
                normalize();
                label.updateUI();
            }
        });
        filterSobelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterSobel();
                JOptionPane.showMessageDialog(null, "Finished");
                //normalize();
                label.updateUI();
            }
        });
        filterGaborButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                D = new double[3][3];
                for(int i=0; i<D.length; i++){
                    for(int j=0; j<D.length; j++){
                        double x=i-1, y=j-1, tetta=0;
                        if(angle1.isSelected()) tetta = 0;
                        if(angle2.isSelected()) tetta = Math.PI*45./180.;
                        if(angle3.isSelected()) tetta = Math.PI*90./180.;
                        if(angle4.isSelected()) tetta = Math.PI*135./180.;
                        double x1 = x*cos(tetta) + y*sin(tetta), y1 = -x*sin(tetta) + y*cos(tetta);
                        D[i][j]=exp(-(x1*x1+y1*y1)/2.)*cos(Math.PI*x1);
                    }
                }
                filterGabor();
                JOptionPane.showMessageDialog(null, "Finished");
                //normalize();
                label.updateUI();
            }
        });

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

        hueSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) hueSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                g.clearRect(0, 0, 10000, 10000);
                g.drawImage(imag2, 0, 0, null);
                if(val>360){
                    hueSpinner.setValue(360);
                    return;
                }
                if(val<-360){
                    hueSpinner.setValue(360);
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
                        if(rgb.R < 0 || rgb.R > 255 || rgb.G < 0 || rgb.G > 255 || rgb.B < 0 || rgb.B > 255)
                            System.out.println("WRONG");
                        imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                normalize();
                spanel.updateUI();
            }
        });
        satSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) satSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                g.clearRect(0, 0, 10000, 10000);
                g.drawImage(imag2, 0, 0, null);
                if(val>100){
                    satSpinner.setValue(100);
                    return;
                }
                if(val<-100){
                    satSpinner.setValue(-100);
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
                        if(rgb.R < 0 || rgb.R > 255 || rgb.G < 0 || rgb.G > 255 || rgb.B < 0 || rgb.B > 255)
                            System.out.println("WRONG");
                        imag.setRGB(i, j, rgb.toInt((imag2.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                normalize();
                spanel.updateUI();
            }
        });
        valSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int val = (Integer) valSpinner.getValue(), width = imag.getWidth(), height = imag.getHeight();
                g.clearRect(0, 0, 10000, 10000);
                g.drawImage(imag2, 0, 0, null);
                if(val>100){
                    valSpinner.setValue(100);
                    return;
                }
                if(val<-100){
                    valSpinner.setValue(-100);
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
                        if(rgb.R < 0 || rgb.R > 255 || rgb.G < 0 || rgb.G > 255 || rgb.B < 0 || rgb.B > 255)
                            System.out.println("WRONG");
                        imag.setRGB(i, j, rgb.toInt((imag2.getRGB(i, j) >> 24) & 0xff));
                        imag2.setRGB(i, j, imag.getRGB(i, j));
                    }
                }
                normalize();
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

    private void normalize(){
        int mn = 0, mx = 255, width = imag.getWidth(), height = imag.getHeight();
        for(int i=0; i<width; i++){
            for(int j=0; j<height; j++){
                RGB rgb = new RGB(imag.getRGB(i, j));
                mn=min(min(rgb.R, rgb.G), min(rgb.B, mn));
                mx=max(max(rgb.R, rgb.G), max(rgb.B, mx));
            }
        }
        if(mn<0 || mx>255){
            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    RGB rgb = new RGB(imag.getRGB(i, j));
                    RGB rgb1 = new RGB(255*(rgb.R-mn)/(mx-mn), 255*(rgb.G-mn)/(mx-mn), 255*(rgb.B-mn)/(mx-mn));
                    imag.setRGB(i, j, rgb1.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                }
            }
        }
    }

    private void filterGauss(){
        int N = d.length;
        double sum=0;
        for(int i=0; i<N; i++)
            sum+=d[i];
        double pixR[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixG[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixB[][] = new double[imag.getWidth()][imag.getHeight()];
        for(int i=0; i<imag.getWidth(); i++){
            for(int j=0; j<imag.getHeight(); j++){
                for(int ii=i-N/2; ii<=i+N/2; ii++){
                    RGB rgb;
                    if(ii>=0 && ii<imag.getWidth())
                        rgb = new RGB(imag.getRGB(ii, j));
                    else{
                        int ii1=ii;
                        if(ii1<0) ii1=0;
                        if(ii1>=imag.getWidth()) ii1=imag.getWidth()-1;
                        rgb = new RGB(imag.getRGB(ii1, j));
                    }
                    pixR[i][j]+=rgb.R*d[ii-i+N/2];
                    pixG[i][j]+=rgb.G*d[ii-i+N/2];
                    pixB[i][j]+=rgb.B*d[ii-i+N/2];
                }
                pixR[i][j]/=sum; pixB[i][j]/=sum; pixG[i][j]/=sum;
            }
        }
        for(int i=0; i<pixR.length; i++){
            for(int j=0; j<pixR[i].length; j++){
                RGB rgb = new RGB((int)pixR[i][j], (int)pixG[i][j], (int)pixB[i][j]);
                imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                imag2.setRGB(i, j, imag.getRGB(i, j));
                imag3.setRGB(i, j, imag.getRGB(i, j));
            }
        }
        label.updateUI();
        //JOptionPane.showMessageDialog(null, "Finished");
        for(int i=0; i<pixR.length; i++)
            for(int j=0; j<pixR[i].length; j++)
                pixR[i][j]=pixG[i][j]=pixB[i][j]=0;
        for(int i=0; i<imag.getWidth(); i++){
            for(int j=0; j<imag.getHeight(); j++){
                for(int jj=j-N/2; jj<=j+N/2; jj++){
                    RGB rgb;
                    if(jj>=0 && jj<imag.getHeight())
                        rgb = new RGB(imag.getRGB(i, jj));
                    else{
                        int jj1=jj;
                        if(jj1<0) jj1=0;
                        if(jj1>=imag.getHeight()) jj1=imag.getHeight()-1;
                        rgb = new RGB(imag.getRGB(i, jj1));
                    }
                    pixR[i][j]+=rgb.R*d[jj-j+N/2];
                    pixG[i][j]+=rgb.G*d[jj-j+N/2];
                    pixB[i][j]+=rgb.B*d[jj-j+N/2];
                }
                pixR[i][j]/=sum; pixB[i][j]/=sum; pixG[i][j]/=sum;
            }
        }
        for(int i=0; i<pixR.length; i++){
            for(int j=0; j<pixR[i].length; j++){
                RGB rgb = new RGB((int)pixR[i][j], (int)pixG[i][j], (int)pixB[i][j]);
                imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                imag2.setRGB(i, j, imag.getRGB(i, j));
                imag3.setRGB(i, j, imag.getRGB(i, j));
            }
        }
    }

    private void filterSobel(){
        satSpinner.setValue(99);
        satSpinner.setValue(-100);
        int N = 3;
        double[][] d1 = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}},
                d2 = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        double pixR1[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixG1[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixB1[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixR2[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixG2[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixB2[][] = new double[imag.getWidth()][imag.getHeight()];
        for(int i=0; i<imag.getWidth(); i++){
            for(int j=0; j<imag.getHeight(); j++){
                for(int ii=i-N/2; ii<=i+N/2; ii++){
                    for(int jj=j-N/2; jj<=j+N/2; jj++) {
                        RGB rgb;
                        if (ii >= 0 && ii < imag.getWidth() && jj>=0 && jj<imag.getHeight())
                            rgb = new RGB(imag.getRGB(ii, j));
                        else {
                            int ii1 = ii, jj1=jj;
                            if (ii1 < 0) ii1 = 0;
                            if (ii1 >= imag.getWidth()) ii1 = imag.getWidth() - 1;
                            if (jj1 < 0) jj1 = 0;
                            if (jj1 >= imag.getHeight()) jj1 = imag.getHeight() - 1;
                            rgb = new RGB(imag.getRGB(ii1, jj1));
                        }
                        pixR1[i][j] += rgb.R * d1[ii - i + N / 2][jj - j + N / 2];
                        pixG1[i][j] += rgb.G * d1[ii - i + N / 2][jj - j + N / 2];
                        pixB1[i][j] += rgb.B * d1[ii - i + N / 2][jj - j + N / 2];
                        pixR2[i][j] += rgb.R * d2[ii - i + N / 2][jj - j + N / 2];
                        pixG2[i][j] += rgb.G * d2[ii - i + N / 2][jj - j + N / 2];
                        pixB2[i][j] += rgb.B * d2[ii - i + N / 2][jj - j + N / 2];
                    }
                }
            }
        }
        for(int i=0; i<pixR1.length; i++){
            for(int j=0; j<pixR1[i].length; j++){
                RGB rgb = new RGB((int)Math.sqrt(pixR1[i][j]*pixR1[i][j]+pixR2[i][j]*pixR2[i][j]),
                        (int)Math.sqrt(pixG1[i][j]*pixG1[i][j]+pixG2[i][j]*pixG2[i][j]),
                        (int)Math.sqrt(pixB1[i][j]*pixB1[i][j]+pixB2[i][j]*pixB2[i][j]));
                imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                imag2.setRGB(i, j, imag.getRGB(i, j));
                imag3.setRGB(i, j, imag.getRGB(i, j));
            }
        }
    }

    private void filterGabor(){
        int N = 3;
        double pixR[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixG[][] = new double[imag.getWidth()][imag.getHeight()];
        double pixB[][] = new double[imag.getWidth()][imag.getHeight()];
        double L_a_b[][] = new double[imag.getWidth()][imag.getHeight()];
        for(int i=0; i<imag.getWidth(); i++){
            for(int j=0; j<imag.getHeight(); j++){
                for(int ii=i-N/2; ii<=i+N/2; ii++){
                    for(int jj=j-N/2; jj<=j+N/2; jj++) {
                        RGB rgb;
                        if (ii >= 0 && ii < imag.getWidth() && jj>=0 && jj<imag.getHeight())
                            rgb = new RGB(imag.getRGB(ii, j));
                        else {
                            int ii1 = ii, jj1=jj;
                            if (ii1 < 0) ii1 = 0;
                            if (ii1 >= imag.getWidth()) ii1 = imag.getWidth() - 1;
                            if (jj1 < 0) jj1 = 0;
                            if (jj1 >= imag.getHeight()) jj1 = imag.getHeight() - 1;
                            rgb = new RGB(imag.getRGB(ii1, jj1));
                        }
                        double[] L = rgb.getLAB();
                        L_a_b[i][j] += L[0]*D[ii - i + N / 2][jj - j + N / 2];
                        pixR[i][j] += rgb.R * D[ii - i + N / 2][jj - j + N / 2];
                        pixG[i][j] += rgb.G * D[ii - i + N / 2][jj - j + N / 2];
                        pixB[i][j] += rgb.B * D[ii - i + N / 2][jj - j + N / 2];
                    }
                }
            }
        }
        for(int i=0; i<pixR.length; i++){
            for(int j=0; j<pixR[i].length; j++){
                RGB rgb = new RGB((int)pixR[i][j], (int)pixG[i][j], (int)pixB[i][j]);
                imag.setRGB(i, j, rgb.toInt((imag3.getRGB(i, j) >> 24) & 0xff));
                imag2.setRGB(i, j, imag.getRGB(i, j));
                imag3.setRGB(i, j, imag.getRGB(i, j));
            }
        }
    }
}
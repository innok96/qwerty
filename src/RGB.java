import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.Math.*;

public class RGB {
    public int R, G, B;
    public RGB(int rgb){
        R = (rgb>>16) & 0xff; G = (rgb>>8) & 0xff; B = rgb & 0xff;
    }
    public RGB(int r, int g, int b){
        R = r; G = g; B = b;
    }

    public int toInt(int alpha){
        R=min(R, 255); R=max(R, 0);
        G=min(G, 255); G=max(G, 0);
        B=min(B, 255); B=max(B, 0);
        return alpha*256*256*256+(int)R*256*256+(int)G*256+(int)B;
    }

    public RGB(double hsv[]){
        double H = hsv[0], S = hsv[1], V = hsv[2];
        H/=360.;
        S/=100.;
        V/=100.;
        if (S==0){
            R=(int)(V*255);
            G=(int)(V*255);
            B=(int)(V*255);
            return;
        }
        double var_h=H*6.;
        if (var_h==6) var_h=0;
        double var_i=Math.floor(var_h);
        double var_1=V*(1.-S);
        double var_2= V*(1.-S*(var_h-var_i));
        double var_3 = V*(1.-S*(1.-(var_h-var_i)));


        switch ((int)var_i){
            case 0 : {R=(int)(255.*V); G=(int)(255.*var_3); B=(int)(255.*var_1); break;}
            case 1 : {R=(int)(255.*var_2); G=(int)(255.*V); B=(int)(255.*var_1); break;}
            case 2 : {R=(int)(255.*var_1); G=(int)(255.*V); B=(int)(255.*var_3); break;}
            case 3 : {R=(int)(255.*var_1); G=(int)(255.*var_2); B=(int)(255.*V); break;}
            case 4 : {R=(int)(255.*var_3); G=(int)(255.*var_1); B=(int)(255.*V); break;}
            default: {R=(int)(255.*V); G=(int)(255.*var_1); B=(int)(255.*var_2); break;}
        }
    }

    public RGB(double lab[], boolean flag){
        double var_Y = ( lab[0] + 16. ) / 116.;
        double var_X = lab[1] / 500. + var_Y;
        double var_Z = var_Y - lab[2] / 200.;

        if ( Math.pow(var_Y, 3.)  > 0.008856 ) var_Y = Math.pow(var_Y, 3.);
        else var_Y = ( var_Y - 16. / 116. ) / 7.787;
        if ( Math.pow(var_X, 3.)  > 0.008856 ) var_X = Math.pow(var_X, 3);
        else var_X = ( var_X - 16. / 116. ) / 7.787;
        if ( Math.pow(var_Z, 3.)  > 0.008856 ) var_Z = Math.pow(var_Z, 3.);
        else var_Z = ( var_Z - 16. / 116. ) / 7.787;

        double X = var_X * 95.047, Y = var_Y * 100.000, Z = var_Z * 108.883;
        var_X = X/100; var_Y = Y/100; var_Z = Z/100;

        double var_R = var_X *  3.2406 + var_Y * -1.5372 + var_Z * -0.4986;
        double var_G = var_X * -0.9689 + var_Y *  1.8758 + var_Z *  0.0415;
        double var_B = var_X *  0.0557 + var_Y * -0.2040 + var_Z *  1.0570;

        if ( var_R > 0.0031308 ) var_R = 1.055 * ( Math.pow(var_R, 1. / 2.4 ) ) - 0.055;
        else var_R = 12.92 * var_R;
        if ( var_G > 0.0031308 ) var_G = 1.055 * ( Math.pow(var_G , 1 / 2.4 ) ) - 0.055;
        else var_G = 12.92 * var_G;
        if ( var_B > 0.0031308 ) var_B = 1.055 * ( Math.pow(var_B , 1 / 2.4 ) ) - 0.055;
        else var_B = 12.92 * var_B;

        R = (int)(var_R * 255);
        G = (int)(var_G * 255);
        B = (int)(var_B * 255);
    }

    public double[] getHSV() {
        double H=0, S, V;
        double max = Math.max(Math.max(R, G), B);
        double min = Math.min(Math.min(R, G), B);
        if (max != min) {
            if ((int)max == R) {
                if (G >= B)
                    H = 60. * (((double)(G - B)) / (max - min));
                if (G < B)
                    H = 60. * (((double)(G - B)) / (max - min)) + 360.;
            } else {
                if ((int)max == G)
                    H = 60. * (((double)(B - R)) / (max - min)) + 120.;
                else
                    H = 60. * (((double)(R - G)) / (max - min)) + 240.;
            }
        } else
            H = 0.;
        S = (max == 0. ? 0. : 1. - min / max)*100.;
        V = max*100./255.;

        double hsv[] = {H, S, V};
        return hsv;
    }

    public double[] getLAB(){
        double lab[] = new double[3];

        double var_R = ( R / 255. );
        double var_G = ( G / 255. );
        double var_B = ( B / 255. );

        if ( var_R > 0.04045 ) var_R = Math.pow(( ( var_R + 0.055 ) / 1.055 ) , 2.4);
        else var_R = var_R / 12.92;
        if ( var_G > 0.04045 ) var_G = Math.pow(( ( var_G + 0.055 ) / 1.055 ) , 2.4);
        else var_G = var_G / 12.92;
        if ( var_B > 0.04045 ) var_B = Math.pow(( ( var_B + 0.055 ) / 1.055 ) , 2.4);
        else var_B = var_B / 12.92;

        var_R = var_R * 100.;
        var_G = var_G * 100.;
        var_B = var_B * 100.;

        double X = var_R * 0.4124 + var_G * 0.3576 + var_B * 0.1805;
        double Y = var_R * 0.2126 + var_G * 0.7152 + var_B * 0.0722;
        double Z = var_R * 0.0193 + var_G * 0.1192 + var_B * 0.9505;
        double var_X = X / 95.047, var_Y = Y / 100.000, var_Z = Z / 108.883;

        if ( var_X > 0.008856 ) var_X = Math.pow(var_X , ( 1./3. ));
        else                    var_X = ( 7.787 * var_X ) + ( 16. / 116. );
        if ( var_Y > 0.008856 ) var_Y = Math.pow(var_Y , ( 1./3. ));
        else                    var_Y = ( 7.787 * var_Y ) + ( 16. / 116. );
        if ( var_Z > 0.008856 ) var_Z = Math.pow(var_Z , ( 1./3. ));
        else                    var_Z = ( 7.787 * var_Z ) + ( 16. / 116. );

        double L = ( 116. * var_Y ) - 16.,
            A = 500. * ( var_X - var_Y ),
            B = 200. * ( var_Y - var_Z );

        lab[0]=L; lab[1]=A; lab[2]=B;
        return lab;
    }

    public double[] getLCH(){
        double[] Lab=getLAB();
        double var_H = atan2(Lab[1], Lab[2]);
        if ( var_H > 0 ) var_H = ( var_H / PI ) * 180.;
        else var_H = 360 - ( abs( var_H ) / PI ) * 180.;

        double[] LCH = {Lab[0], sqrt( Math.pow(Lab[1] , 2) + Math.pow(Lab[2] , 2) ), var_H};
        return LCH;
    }
}
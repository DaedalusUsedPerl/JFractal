package fractal;

import java.util.Random;
import org.apache.commons.math3.complex.Complex;

/**
 * Common paint methods
 * @author Shubham
 */
public final class Painters {
    
    private Painters(){}

    public static String blackWhitePaint(Float v, Complex c){
        int bw = (int)(v*255.0);
        return bw + " " + bw + " " + bw;
    }

    public static String whiteBlackPaint(Float v, Complex c){
        int bw = (int)((1.0-v)*255.0);
        return bw + " " + bw + " " + bw;
    }

    public static String blueWhitePaint(Float v, Complex c){
        int bw = (int)((1.0 - v)*255.0);
        return bw + " " + bw + " " + 255;
    }
    
    public static String randomPaint(Float v, Complex c){
        Random prng = new Random((int) (v * 256.0));
        return prng.nextInt(256) + " " + prng.nextInt(256) + " " + prng.nextInt(256);
    }
    
    public static String randomPaint(Float v, Complex c, int n){
        Random prng = new Random((int) (v * 256.0));
        return ((prng.nextInt(256) + n) % 256) + " " + ((prng.nextInt(256) + n) % 256) + " " + ((prng.nextInt(256) + n) % 256);
    }

    //For painting newton fractals
    public static String newtonPaint(Float v, Complex c){
        int r,g,b;
        double ang = c.getArgument();
        double a1 = Math.PI, a2 = +Math.PI / 3.0, a3 = -Math.PI / 3.0;
        if (-Math.PI < ang && ang <= a3) {
            //red-green
            double g_a = (ang + a1);
            double r_a = (a3 - ang);
            r = (int) ((r_a / (r_a + g_a)) * 255.0);
            g = (int) ((g_a / (r_a + g_a)) * 255.0);
            b = (int) (v * 255.0);
        } else if (a3 < ang && ang <= a2) {
            //green-blue
            double b_a = (ang - a3);
            double g_a = (a2 - ang);
            r = (int) (v * 255.0);
            g = (int) ((g_a / (b_a + g_a)) * 255.0);
            b = (int) ((b_a / (b_a + g_a)) * 255.0);
        } else {
            //blue - red
            double r_a = (ang - a2);
            double b_a = (a1 - ang);
            r = (int) ((r_a / (b_a + r_a)) * 255.0);
            g = (int) (v * 255.0);
            b = (int) ((b_a / (b_a + r_a)) * 255.0);
        }
        return r + " " + g + " " + b;
    }
}

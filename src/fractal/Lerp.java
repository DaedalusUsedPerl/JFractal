package fractal;

import org.apache.commons.math3.complex.Complex;

/**
 * Utility functions for linear interpolations
 * @author Shubham
 */
public final class Lerp {
    
    private Lerp(){}
    
    public static int lerp(int f, int n, int from, int to){
        return lerp((double)f/n, from, to);
    }

    public static int lerp(double frac, int from, int to) {
        return (int) (frac * (double) to + (1.0 - frac) * (double) from);
    }
    
    public static float lerp(int f, int n, float from, float to){
        return lerp((double)f/n, from, to);
    }

    public static float lerp(double frac, float from, float to) {
        return (float) (frac * to + (1.0 - frac) * from);
    }
    
    public static double lerp(int f, int n, double from, double to){
        return lerp((double)f/n, from, to);
    }

    public static double lerp(double frac, double from, double to) {
        return frac * to + (1.0 - frac) * from;
    }
    
    public static Complex lerp(int f, int n, Complex from, Complex to){
        return lerp((double)f/n, from, to);
    }

    public static Complex lerp(double frac, Complex from, Complex to) {
        Complex f_term = from.multiply(1.0 - frac);
        Complex t_term = to.multiply(frac);
        return f_term.add(t_term);
    }
    
    public static String lerp(int f, int n, String from, String to){
        return lerp((double)f/n, from, to);
    }

    public static final String lerp(double frac, String from, String to) {
        String[] frgb = from.trim().split(" ");
        String[] trgb = to.trim().split(" ");
        String[] drgb = new String[3];
        for (int i = 0; i < 3; ++i) {
            int f = Integer.parseInt(frgb[i]);
            int t = Integer.parseInt(trgb[i]);
            drgb[i] = "" + (lerp(frac, f, t));
        }
        return drgb[0] + " " + drgb[1] + " " + drgb[2];
    }
    
}

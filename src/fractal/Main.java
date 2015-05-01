package fractal;

import static fractal.Painters.*;
import static fractal.Lerp.*;
import static java.lang.Math.*;
import org.apache.commons.math3.complex.Complex;

public class Main {

    private static Complex update(Complex c, Complex o) {
        Complex p = c.pow(3).subtract(1);
        Complex p_ = c.pow(2).multiply(3);
        Complex f = p.divide(p_);
        return c.subtract(f);
    }

    private static String paint(Float v, Complex c) {
        int bw = (int)(v*255.0);
        return bw + " " + bw + " " + bw;
    }

    private static String paint_trap(Float v, Complex c) {
        int bw = (int)(exp(-c.abs())*255.0);
        return bw + " " + bw + " " + bw;
    }

    private static double trap(Complex z) {
        Complex td = z.multiply((z.abs()-1)/z.abs());
        return abs(td.getImaginary()) + abs(td.getReal());
    }

    public static void main(String[] args) {
            Fractal fr = new Fractal.Builder()
                    .update((c,o) -> c.pow(3).exp().add(-0.59))
                    .width(1000)
                    .depth(100)
                    .scale(0.5f)
                    .infLim(10E37f)
                    .paint((v,c)-> randomPaint(v, c))
                    .build();
            fr.draw("out");
    }

    private static Updater frame(int f, int n) {
        return (c, o) -> {
            Complex c1 = c.pow(3).add(-1);
            Complex c2 = c.pow(3).exp().add(-0.59);
            return lerp(f, n, c1, c2);
        };
    }

    private static void movie() {
        double phi = (1.0 + Math.sqrt(5)) / 2.0;
        double phi2 = (1.0 + Math.sqrt(5)) / 2.0;
        Movie m = new Movie.Builder()
                .nFrames(30)
                .frame((Complex c, Complex o) -> c.pow(-6).add(o))
                .depth(100)
                .width(250)
                .scale((f, n) -> lerp(f, n, 50.0f, 2.5f))
                .infLim(10E37f)
                .paint((Float v, Complex c) -> blackWhitePaint(v, c))
                .build();
        m.drawMovie();
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}

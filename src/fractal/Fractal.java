package fractal;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.function.Function;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 * Basic class to draw fractals. This class can draw the Mandelbrot set, Julia
 * sets, and Newton fractals, and render them using the escape-time or
 * orbit-trap methods.
 *
 * @author Shubham
 */
public class Fractal {

    private static final boolean USE_IMAGICK = true;

    private final int width; //Width of the final fractal image
    private final int depth; //Maximum number of iterations
    private final float scale; //width of the complex field to render
    private final float inf_lim; //bailout value
    private final Complex offset; //offset the origin to draw different areas of the field
    private final Updater update; //Z_(n+1) = update(Z_n)
    private final Painter paint; //Converts the fractal model to color
    private final Function<Double, Double> warp; //Twist the fractal to create snowflakes
    private final Complex start; //Z_0 = start
    private final Function<Complex, Double> trap; //The trap used to estimate distance

    private final String[] output; //Holds all the pixels before printing to file
    //Necessare since pixels are generated out of order

    public Fractal(int width, int depth, float scale, float inf_lim, Complex offset, Updater update, Painter paint, Function<Double, Double> warp, Function<Complex, Double> t, Complex start) {
        this.width = width;
        this.depth = depth;
        this.scale = scale;
        this.inf_lim = inf_lim;
        this.offset = offset;
        this.update = update;
        this.paint = paint;
        this.warp = warp;
        this.trap = t;
        this.start = start;
        this.output = new String[4 * width * width];
    }

    /**
     * Holds the number of iterations before bailout and a complex number. The
     * complex number is the final value at bailout (escape time) or the closest
     * approach to the trap (orbit trap)
     */
    private static class Pair {

        public int n;
        public Complex c;

        public Pair(int n, Complex c) {
            this.n = n;
            this.c = c;
        }
    }

    /**
     * Builder-pattern constructor for Fractal.
     */
    public static class Builder {

        private int width;
        private int depth;
        private float scale;
        private float inf_lim;
        private Complex offset;
        private Updater update;
        private Painter paint;
        private Function<Double, Double> warp;
        private Function<Complex, Double> trap;
        private Complex start;

        public Builder() {
            width = depth = 100;
            scale = 2.5f;
            inf_lim = 5.0f;
            offset = new Complex(0.0, 0.0);
            update = ((c, o) -> c.pow(2).add(o));
            paint = ((v, c) -> {
                int val = (int) (v * 255.0);
                return val + " " + val + " " + val;
            });
            warp = a -> a;
            trap = null;
            start = null;
        }

        public Fractal build() {
            return new Fractal(width, depth, scale, inf_lim,
                    offset, update, paint, warp, trap, start);
        }

        public Builder width(int w) {
            this.width = w;
            return this;
        }

        public Builder depth(int d) {
            this.depth = d;
            return this;
        }

        public Builder scale(float s) {
            this.scale = s;
            return this;
        }

        public Builder infLim(float l) {
            this.inf_lim = l;
            return this;
        }

        public Builder offset(Complex o) {
            this.offset = o;
            return this;
        }

        public Builder update(Updater u) {
            this.update = u;
            return this;
        }

        public Builder paint(Painter p) {
            this.paint = p;
            return this;
        }

        public Builder warp(Function<Double, Double> w) {
            this.warp = w;
            return this;
        }

        public Builder trap(Function<Complex, Double> t) {
            this.trap = t;
            return this;
        }

        public Builder start(Complex s) {
            this.start = s;
            return this;
        }
    }

    /**
     * Render the pixel.
     *
     * @param x x-coordinate of pixel
     * @param y y-coordinate of pixel
     * @param v shading intensity
     * @param c final complex value
     */
    private void paintPixel(int x, int y, float v, Complex c) {
        x += width;
        y = width - y;
        int index = ((y * 2 * width) + x) % output.length;
        output[index] = paint.paint((v / depth), c).trim() + " ";
    }

    /**
     * Escape-time render
     *
     * @param x real part
     * @param y imaginary part
     * @return Pair containing shading information for this pixel
     */
    private Pair iterateOn(float x, float y) {
        int n = 0;
        Complex c = new Complex(x, y)
                .multiply(scale / width)
                .add(offset);
        Complex z = start == null
                ? new Complex(c.getReal(), c.getImaginary())
                : start;
        double mag = z.abs(), ang = z.getArgument();
        ang = warp.apply(ang);
        z = ComplexUtils.polar2Complex(mag, ang);
        while (z.abs() < inf_lim) {
            n++;
            if (n >= depth) {
                return new Pair(n, z);
            }
            z = update.update(z, c);
        }
        return new Pair(n, z);
    }

    /**
     * Orbit-trap render
     *
     * @param x real part
     * @param y imaginary part
     * @return Pair containing shading information for this pixel
     */
    private Pair getDistance(float x, float y) {
        int n = 0;
        Complex c = new Complex(x, y)
                .multiply(scale / width)
                .add(offset);
        Complex z = start == null
                ? new Complex(c.getReal(), c.getImaginary())
                : start;
        Complex tr = z;
        double mag = z.abs(), ang = z.getArgument();
        ang = warp.apply(ang);
        z = ComplexUtils.polar2Complex(mag, ang);
        double v = 10E37;
        for (int i = 0; i < depth; ++i) {
            z = update.update(z, c);
            if (z.abs() > inf_lim) {
                n = i;
            }
            double t = trap.apply(z);
            tr = t < v ? z : tr;
            v = t < v ? t : v;
        }
        return new Pair(n, tr);
    }

    public void drawFractal() {
        for (int x = -width; x < width; x++) {
            for (int y = -width; y < width; y++) {
                Pair p = trap != null ? getDistance(x, y) : iterateOn(x, y);
                paintPixel(x, y, p.n, p.c);
            }
        }
    }

    private void writeTo(String name) {
        try (BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(name + ".ppm")))) {
                    String data = "P3\n" + (2 * width) + " " + (2 * width) + "\n255\n";
                    out.write(data);
                    for (int i = 0; i < output.length; ++i) {
                        String colour = output[i];
                        out.write(colour);
                        if ((i + 1) % width == 0) {
                            out.write("\n");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace(System.out);
                    System.exit(-1);
                    throw new RuntimeException(ex);
                }
                if (USE_IMAGICK) {
                    try {
                        String srcfile = "\"" + name + ".ppm\"";
                        String dstfile = "\"" + name + ".png\"";
                        Runtime.getRuntime().exec("convert.exe " + srcfile
                                + " " + dstfile);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
    }

    public void draw(String s) {
        drawFractal();
        writeTo(s);
    }
}

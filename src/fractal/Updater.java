package fractal;

import org.apache.commons.math3.complex.Complex;

@FunctionalInterface
public interface Updater {
        Complex update(Complex curr, Complex start);
}

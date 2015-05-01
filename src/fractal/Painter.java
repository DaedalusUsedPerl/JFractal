package fractal;

import org.apache.commons.math3.complex.Complex;

/**
 * Given shading information for a pixel (cf. the Pair inner class in Fractal),
 * generates a string containing RGB pixel values.
 * @author Shubham
 */
@FunctionalInterface
public interface Painter {
    String paint(Float value, Complex number);
}

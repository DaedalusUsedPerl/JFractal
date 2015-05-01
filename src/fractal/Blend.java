package fractal;

/**
 * Specifies blending information for any parameter to Fractal.
 * @author Shubham
 * @param <E> Any parameter that changes over an animation
 */
@FunctionalInterface
public interface Blend<E> {
    /**
     * Updates a value over an animation
     * @param f The current frame
     * @param n The maximum number of frames
     * @return The value of the parameter at this stage of the animation
     */
    E blend(int f, int n);
}

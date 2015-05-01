package fractal;

import java.util.function.Function;
import org.apache.commons.math3.complex.Complex;

/**
 * Class for drawing fractal animations.
 * Uses the Blend interface to allow parameters to change.
 * At each iteration, this class creates a new fractal and writes it out to file.
 * @author Shubham
 */
public class Movie {
    
    private final int nFrames;
    private final Blend<Integer> width, depth;
    private final Blend<Float> inf_lim, scale;
    private final Blend<Complex> offset;
    private final Blend<Function<Double, Double>> warp;
    private final Blend<Painter> paint;
    private final Blend<Updater> frame;

    public Movie(int nFrames, 
            Blend<Integer> width, Blend<Integer> depth, 
            Blend<Float> scale, Blend<Float> inf_lim, 
            Blend<Updater> frame, 
            Blend<Painter> paint, 
            Blend<Complex> offset, 
            Blend<Function<Double, Double>> warp) {
        this.nFrames = nFrames;
        this.width = width;
        this.depth = depth;
        this.inf_lim = inf_lim;
        this.scale = scale;
        this.offset = offset;
        this.warp = warp;
        this.paint = paint;
        this.frame = frame;
    }

    public static class Builder {

        private int nFrames;
        private Blend<Integer> width, depth;
        private Blend<Float> inf_lim, scale;
        private Blend<Complex> offset;
        private Blend<Function<Double, Double>> warp;
        private Blend<Painter> paint;
        private Blend<Updater> frame;
        
        public Builder(){
            nFrames = 1;
            width = depth = (f, n) -> 100;
            inf_lim = (f, n) -> 5.0f;
            scale = (f, n) -> 1.0f;
            offset = (f, n) -> new Complex(0,0);
            warp = (f, n) -> (a -> a);
            paint = (f, n) -> ((v, c) -> {
                int val = (int)(v*255.0);
                return val + " " + val + " " + val;
            });
            frame = (f, n) -> ((c,o) -> c.pow(2).add(o));
        }
        
        public Movie build(){
            return new Movie(nFrames, width, depth, 
                    scale, inf_lim, frame, paint, offset, warp);
        }
        
        public Builder nFrames(int n){ nFrames = n; return this;}
        public Builder width(Blend<Integer> w){width = w; return this;}
        public Builder depth(Blend<Integer> d){depth = d; return this;}
        public Builder infLim(Blend<Float> i){inf_lim = i;return this;}
        public Builder scale(Blend<Float> s){scale = s; return this;}
        public Builder offset(Blend<Complex> o){offset = o; return this;}
        public Builder warp(Blend<Function<Double, Double>> w){warp = w; return this;}
        public Builder paint(Blend<Painter> p){paint = p; return this;}
        public Builder frame(Blend<Updater> f){frame = f; return this;}
        
        public Builder width(int w){width = (f, n) -> w; return this;}
        public Builder depth(int d){depth = (f, n) -> d; return this;}
        public Builder infLim(float i){inf_lim = (f, n) -> i; return this;}
        public Builder scale(float s){scale = (f, n) -> s; return this;}
        public Builder offset(Complex o){offset = (f, n) -> o; return this;}
        public Builder warp(Function<Double, Double> w){warp = (f, n) -> w; return this;}
        public Builder paint(Painter p){paint = (f, n) -> p; return this;}
        public Builder frame(Updater fr){frame = (f, n) -> fr; return this;}
        
    }

    public void drawFrame(int frame_no){
        System.out.println("Drawing: " + frame_no);
        Fractal f = new Fractal.Builder()
                .width(width.blend(frame_no, nFrames-1))
                .depth(depth.blend(frame_no, nFrames-1))
                .scale(scale.blend(frame_no, nFrames-1))
                .infLim(inf_lim.blend(frame_no, nFrames-1))
                .offset(offset.blend(frame_no, nFrames-1))
                .update(frame.blend(frame_no, nFrames-1))
                .paint(paint.blend(frame_no, nFrames-1))
                .warp(warp.blend(frame_no, nFrames-1))
                .build();
        f.draw("./frames/frame" + frame_no);
    }

    public void drawMovie(){
        for (int i = 0; i < nFrames; i++) {
            drawFrame(i);
            System.out.println("Drew frame: " + i);
        }
    }
}

package rusting.game;

import arc.Events;
import arc.func.Floatf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.game.EventType.Trigger;
import rusting.graphics.PulseFieldRenderer;

public class PulseFieldManager {

    public static class PulseFieldGroup{

        public Seq<PulseFieldEmitter> emitters = new Seq<>();
    }

    public PulseFieldManager() {
        renderer = new PulseFieldRenderer(this);
        Events.on(Trigger.postDraw.getClass(), e -> {
            renderer.draw();
        });
    }

    //gets the value
    public interface PulseFieldEmitter{

        Vec2 position();

        float getValue(Vec2 point);

        float getIntensity();

        boolean active();

        void handleEntity();

        PulseFieldManager getManager();

        void joinGroup();

        PulseFieldGroup getGroup();
    }

    public enum Directions{
        N,
        NW,
        NE,
        S,
        SE,
        SW,
        E,
        W;
    }

    public static class SaddlePoint {

        public int x, y;
        public boolean within = false;

    }

    public static class SaddlePointSquare{

        //the points, starting at bottom left, and going from left to right and bottom to top
        public SaddlePoint[] points = new SaddlePoint[]{null, null, null, null};

        //state of the SaddlePointSquare. Refer to documentation on line 43
        public int state = 0;

        //used for convenience
        public boolean fullyWithin = false;

        public int startX, startY;
    }

    /*
    easy access to resources at https://en.wikipedia.org/wiki/File:Marching_squares_algorithm.svg
    state 0: nothing
    state 1: point 0 within
    state 2: point 1 within
    state 3: point 0 & 1 within
    state 4: point 3 within
    state 5: point 0 & 3 within
    state 6: point 1 & 3 within
    state 7: point 0 & 1 & 3 within
    state 8: point 2 within
    state 9: point 0 & 2 within
    state 10: point 1 & 2 within
    state 11: point 0 & 1 & 2 within
    state 12: point 2 & 3 within
    state 13: point 0 & 2 & 3 within
    state 14: point 1 & 2 & 3 within
    state 15: point 0 & 1 & 2 & 3 within
     */

    public PulseFieldRenderer renderer;

    //used in finding bottom of the field's x and y
    public float bottomX, bottomY;


    //*sigh* 3 separate IDMaps for 3 different use cases, map one being for buildings, map 2 being for units and map 3 is mostly for visual things and other things
    public IntMap<PulseFieldEmitter> IDMap1 = new IntMap();
    public IntMap<PulseFieldEmitter> IDMap2 = new IntMap();
    public IntMap<PulseFieldEmitter> IDMap3 = new IntMap();
    public Seq<PulseFieldEmitter> emitters = new Seq<>();
    private static IntMap<PulseFieldEmitter> tmpMap;
    float totalForce;
    PulseFieldEmitter currentEmitter;

    //note: don't use this to add an emitter to a map, use the addField method instead as it's much more efficient
    public void updateField(int map, int id, PulseFieldEmitter newEmitter){
        if(map == 1) tmpMap = IDMap1;
        if(map == 2) tmpMap = IDMap2;
        if(map == 3) tmpMap = IDMap3;
        if(tmpMap.containsKey(id)){
            currentEmitter = tmpMap.get(id);
            if(emitters.contains(currentEmitter)) emitters.remove(currentEmitter);
            tmpMap.put(id, newEmitter);
            emitters.add(newEmitter);
            return;
        }
        addEmitter(map, id, newEmitter);
    }

    //use this method to add emitters to a map
    public void addEmitter(int map, int id, PulseFieldEmitter newEmitter){
        if(map == 1) tmpMap = IDMap1;
        if(map == 2) tmpMap = IDMap2;
        if(map == 3) tmpMap = IDMap3;
        tmpMap.put(id, newEmitter);
        emitters.add(newEmitter);
        return;
    }

    //use this method to remove emitters from the maps
    public void removeEmitter(int map, int id, PulseFieldEmitter emitter){
        if(map == 1) tmpMap = IDMap1;
        if(map == 2) tmpMap = IDMap2;
        if(map == 3) tmpMap = IDMap3;
        tmpMap.remove(id);
        emitters.remove(emitter);
        return;
    }

    //get intensity of fields at that point Anything that is above 0 is considdered "within" a field by the renderer. Anything with 0 is considdered outside a field.
    public float getIntensity(Vec2 point){

        totalForce = 0;

        emitters.each(e -> {
            totalForce += e.getValue(point);
        });

        return totalForce;
    };


    public Seq<SaddlePointSquare> squares = new Seq();

    //points on a grid.
    public Seq<SaddlePoint> points = new Seq();

    private SaddlePoint currentPoint;
    private SaddlePointSquare currentSquare;

    public int width = 0, height = 0;
    public int squareWidth = 0, squareHeight = 0;

    public void update(Floatf<Vec2> fieldValue, float initialX, float initialY, int width, int height){
        this.width = width;
        this.height = height;

        squareWidth = width + 2;
        squareHeight = height + 2;

        squares.clear();
        points.clear();

        //if statement spam is pain, even more pain is the positioning
        //Squares start at -1, -1 and end at squareWidth - 1, squareHeight - 1
        for (int y = 0; y < squareWidth; y++) {
            for (int x = 0; x < squareHeight; x++) {
                if(squares.size - 1 < x + y * width) squares.add(new SaddlePointSquare());
                currentSquare = squares.get(x + y * width);
                currentSquare.startX = x;
                currentSquare.startY = y;
            }
        }

        //get values for all the points
        for (int y = -1; y < width; y++) {
            for (int x = -1; x < height; x++) {
                if(points.size - 1 < x + y * width) points.add(new SaddlePoint());
                currentPoint = points.get(x + y * width);
                currentPoint.within = false;
                currentPoint.x = x;
                currentPoint.y = y;
                if(fieldValue.get(Tmp.v1.set(initialX + x * 8, initialY + y * 8)) > 1) currentPoint.within = true;

                //set square's points
                for (int sx = 0; sx < 1; sx++) {
                    for (int sy = 0; sy < 1; sy++) {
                        //turn 0, 1 pair into -1, 1
                        int realx = sx * 2 - 1, realy = sy * 2 - 1;
                        currentSquare = getSaddlePointSquare(realx, realy);

                        /*
                        points are in a grid like this
                            2, 3
                            0, 1
                         getting a square to the right from the point will result in a point to the left of the square (right is left, up is down etc)
                         */

                        currentSquare.points[(sx == 0 ? 1 : 0) + (sy == 0 ? 2 : 0)] = currentPoint;
                    }
                }
            }
        }

        squares.each(s -> {

        });
    }

    //note: only works with initialized points
    public SaddlePoint getSaddlePoint(int x, int y) {
        return points.get(x + y * width);
    }

    //note: only works with initialized squares
    public SaddlePointSquare getSaddlePointSquare(int x, int y) {
        //square's position starts at -1, -1 and ends at width + 1, height + 1 so adjust accordingly
        return squares.get(x + y * squareWidth + squareWidth);
    }
}

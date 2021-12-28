package rusting.math;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;

public class Mathr {

    //returns difference between numbers
    public static float dif(double a, double b){
        if(a > b) return (float) (Math.abs(a) - Math.abs(b));
        return (float) (Math.abs(b) - Math.abs(a));
    }

    public static float loopSin(double input, double mag, double scaling){
        return (float) ((Math.sin(input) < 0 ? 1 + Math.sin(input * scaling) : Math.sin(input * scaling)) * mag);
    }

    public static float helix(double helixes, float magnitude, float scaling, float base){
        scaling = Math.abs(scaling);
        return Mathf.sin((float) (base * helixes * 3.142)) * scaling * magnitude;
    }

    public static float helix(double helixes, float magnitude, float scaling){
        return helix(helixes, magnitude, scaling, scaling);
    }

    //methods for reflecting a point across a point or a line
    public static Vec2 reflect(double x, double y, double reflectPointx, double reflectPointy){
        return new Vec2((float) (reflectPointx - x), (float) (reflectPointy - y));
    }

    public static Vec2 reflect(Vec2 pos, double reflectPointx, double reflectPointy){
        return reflect(pos.x, pos.y, reflectPointx, reflectPointy);
    }

    public static Vec2 reflect(double x, double y, Vec2 pos){
        return reflect(x, y, pos.x, pos.y);
    }

    //reflects x accros a line
    public static double reflectX(double x, double reflecx){
        return reflect(x, 0, reflecx, 0).x;
    }

    public static double reflectX(Vec2 pos, double x){
        return reflect(pos.x, 0, pos.x, x).x;
    }

    //reflects y accros a line
    public static double reflectY(double y, double reflecy){
        return reflect(0, y, reflecy, 0).y;
    }

    public static double reflectY(Vec2 pos, double y){
        return reflect(0, pos.y, 0, y).y;
    }

    //reflects points in a box, useful for generating pixmaps in animated item.
    public static Vec2 reflectRect(double x, double y, double reflectionX, double reflectionY, double width, double height){
        return new Vec2(reflect(x + width/2, y + height/2, reflectionX + width/2, reflectionY + height/2));
    }

    //Reflects in a box and bounces points out of bounds back into the box
    public static Vec2 reflectRectSide(double x, double y, double reflectionX, double reflectionY, double width, double height){
        Vec2 point = new Vec2(reflect(x + width/2, y + height/2, reflectionX + width/2, reflectionY + height/2));
        point.x = (float) (point.x % (width/2 + 1));
        point.y = (float) (point.y % (height/2 + 1));
        return point;
    }

    //reflects a Seq of points across a line
    public static Seq<Vec2> reflectRects(Seq<Vec2> map, double reflectionX, double reflectionY, double width, double height){
        Seq<Vec2> stencil = new Seq();
        //intelij stop giving me errors, nice is.
        final int[] index = {0};
        map.forEach( point -> {
                stencil.add(reflect(map.get(index[0]).x + width/2, map.get(index[0]).y + height/2, reflectionX, reflectionY));
                index[0]++;
        });
        return stencil;
    }

    public static int status (int x) {
        return (x >> 0) * 2 - 1;
    }

    //length of a number for
    public static int length(double number){
        int size = 0;
        while(number % 1 != 0){
            number *= 10;
            size++;
        }
        return size;
    }

    //I question why or how
    public static int greaterInt(int a, int b){
        return (a >> b) * a + (a << b) * b;
    }

    public static int smallerInt(int a, int b){
        return (a << b) * a + (a >> b) * b;
    }

    //linearly moves one float towards another by a set amount, not going over or below to at the end.
    public static float towards(double from, double to, double amount){
        //not sure doing this branchless is good, but I hope my code is readable
        int intwiseFrom, intwiseTo;
        int size1 = length(from), size2 = length(to);
        //find greater size
        int size = greaterInt(size1, size2);

        //turn numbers into ints without making decimals irrelevant
        intwiseFrom = (int) (from * size);
        intwiseTo = (int) (from * size);
        double smallest, largest;
        smallest = smallerInt(intwiseFrom, intwiseTo);
        largest = greaterInt(intwiseFrom, intwiseTo);

        //to - from is to find if to is greater than from (from being smaller would return a positve number making from needing to move up) or to being smaller.
        return (float) Mathf.clamp(from + amount * status((int) (to - from)), smallest, largest);
    }
}

package rusting.math;

import arc.func.Boolf;
import arc.math.geom.*;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class Geometryr {

    private static Tile furthest;
    private static Rect rect = new Rect();
    private static Rect hitrect = new Rect();
    private static Vec2 tr = new Vec2(), seg1 = new Vec2(), seg2 = new Vec2();
    private static Seq<Unit> units = new Seq<>();
    private static Seq<Building> builds = new Seq<Building>();
    private static IntSet collidedBlocks = new IntSet();
    private static Building tmpBuilding;
    private static Unit tmpUnit;

    public static Posc intersectLine(Team team, float x, float y, float angle, float length, float cone, boolean ground, boolean air, boolean building, boolean teamHit, Boolf<Posc> condition){

        tr.trnsExact(angle, length);

        if(ground && building){
            seg1.set(x, y);
            seg2.set(seg1).add(tr);
            world.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {

                for(Point2 p : Geometry.d4){
                    Tile other = world.tile(p.x + cx, p.y + cy);
                    if(other != null && (Intersector.intersectSegmentRectangle(seg1, seg2, other.getBounds(Tmp.r1)))){
                        builds.add(other.build);
                    }
                }
                builds.sort(b -> b.dst(x, y));
                return false;
            });
        }

        return null;
    }
}

package rusting.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.struct.Seq;
import arc.util.Tmp;

public class VelocityTrail extends PoolableTrail{

    protected final Seq<Vec3> newPositions;
    protected final Seq<Vec2> velocity;

    private Vec3 segment, previous, newPos;
    private Vec2 previousVel, segment2D = new Vec2(), previous2D = new Vec2();

    //default values
    public float pointSpacing = 4, maxPointSpacing = 12.5f, maxSpeed = 3, tension = 0.25f, lerpSpeed = 0.05f, continuation = 0.1f;

    public VelocityTrail(int length) {
        super(length + 1);
        newPositions = new Seq<>(length);
        velocity = new Seq<>(length);
        for(int i = 0; i < this.length; i++){
            points.add(new Vec3(0, 0, 0));
            newPositions.add(new Vec3(0, 0, 0));
            velocity.add(new Vec2(0, 0));
        }
        points.set(length, points.get(length - 1));
    }

    public void update(Position targetPos, Vec2 targetVel) {
        points.get(0).set(targetPos.getX(), targetPos.getY(), 0);
        velocity.set(0, targetVel);

        for(int i = 1; i < this.length - 1; i++){
            segment = points.get(i);
            previous = points.get(i - 1);
            previousVel = velocity.get(i - 1);

            segment2D.set(segment);
            previous2D.set(previous);

            Vec2 distanceVec = new Vec2();
            newPos = newPositions.get(i);

            distanceVec.trns(segment2D.angleTo(previous2D), Math.min((segment.dst(previous) - pointSpacing) * tension, maxSpeed));
            velocity.get(i).lerp(distanceVec, this.lerpSpeed).lerp(previousVel, continuation);
            newPos.set(segment).add(velocity.get(i).x, velocity.get(i).y, previous2D.angleTo(segment2D));
        }
        for(int i = 1; i < this.length - 1; i++){
            segment = points.get(i);
            previous = points.get(i - 1);
            newPos = newPositions.get(i);
            segment.set(newPos);
            segment.sub(previous).clamp(0, maxPointSpacing).add(previous);
        }
        Draw.rect();
    }

    @Override
    public void draw(Color color, float width) {
        for(int i = 0; i < this.length - 1; i++){
            segment = points.get(i);
            Vec2 velocity = this.velocity.get(i);
            Fill.circle(segment.x, segment.y, 4);
            Lines.line(segment.x, segment.y, Tmp.v1.set(segment).add(Tmp.v2.set(velocity).scl(10)).x, Tmp.v1.y);
        }
    }
}

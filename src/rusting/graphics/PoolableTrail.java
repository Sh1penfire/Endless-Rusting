package rusting.graphics;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.*;
import arc.util.pooling.Pool.Poolable;

public class PoolableTrail implements Poolable {
    public int length;

    protected final Seq<Vec3> points;
    protected float lastX = -1, lastY = -1;

    public PoolableTrail(int length){
        this.length = length;
        points = new Seq<>(length);
    }

    public void clear(){
        points.clear();
    }

    public void draw(Color color, float width){
        Draw.color(color);

        for(int i = 0; i < points.size - 1; i++){
            Vec3 c = points.get(i);
            Vec3 n = points.get(i + 1);
            float size = width / length;

            float cx = Mathf.sin(c.z) * i * size, cy = Mathf.cos(c.z) * i * size, nx = Mathf.sin(n.z) * (i + 1) * size, ny = Mathf.cos(n.z) * (i + 1) * size;
            Fill.quad(c.x - cx, c.y - cy, c.x + cx, c.y + cy, n.x + nx, n.y + ny, n.x - nx, n.y - ny);
        }

        Draw.reset();
    }

    public void update(float x, float y){
        if(points.size > length){
            Pools.free(points.first());
            points.remove(0);
        }

        float angle = -Angles.angle(x, y, lastX, lastY);

        points.add(Pools.obtain(Vec3.class, Vec3::new).set(x, y, (angle) * Mathf.degRad));


        for (int i = 0; i < points.size - 1; i++) {
            Vec3 p = points.get(points.size - i - 1), e = points.get(points.size - i - 2);
            p.lerp(Tmp.v31.set(e.x, e.y, e.z), 0.01f * Time.delta);
        }

        lastX = x;
        lastY = y;
    }

    @Override
    public void reset() {
        clear();
        lastX = lastY = -1;
    }
}

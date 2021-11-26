package rusting.graphics;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.util.pooling.Pools;

public class JagedTrail extends PoolableTrail{

    public float varyDst;

    public JagedTrail(int length, float varyDst) {
        super(length);
        this.varyDst = varyDst;
    }

    @Override
    public void update(float x, float y) {
        if(points.size > length){
            Pools.free(points.first());
            points.remove(0);
        }

        float angle = -Angles.angle(x, y, lastX, lastY);

        points.add(Pools.obtain(Vec3.class, Vec3::new).set(x + Mathf.random(-varyDst, varyDst), y + Mathf.random(-varyDst, varyDst), (angle) * Mathf.degRad));

        lastX = x;
        lastY = y;
    }
}

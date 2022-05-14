package rusting.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.gen.Bullet;
import mindustry.gen.Teamc;
import rusting.graphics.Drawr;

public class LandmineBulletType extends BaseBulletType {

    public boolean drawDefault = false;
    public float armingMulti = 2;
    public float armingRange = 85;
    static Vec2 tmp = new Vec2();
    public float spin = 0.08f;
    public int shieldSides = 6;
    public float shieldRadius = hitSize, indicatorRadius = hitSize;
    public Color shieldBack, shieldFront, colorStart, colorEnd;
    public float indicatorPercent = 0.5f, indicatorFadein = 6;
    public float[] verts;

    public LandmineBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
    }

    public void setSides(int sides){
        shieldSides = sides;
        verts = new float[shieldSides * 2];
    }

    @Override
    public void init() {
        super.init();
        setSides(shieldSides);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        Teamc target = Units.closestTarget(b.team, b.x, b.y, armingRange, e -> (e.isGrounded() && collidesGround) || (e.isFlying() && collidesAir), t -> collidesGround);
        if(target != null){
            b.time += armingMulti * Time.delta;
        }
    }

    @Override
    public void draw(Bullet b) {
        float rotation = Mathf.randomSeed(b.id) * 360 + b.time * spin;

        if(drawDefault) {
            super.draw(b);
        }
        else {
            for (int i = 0; i < shieldSides * 2; i += 2) {
                tmp.trns(i * 360 / shieldSides + rotation, shieldRadius);
                verts[i] = tmp.x;
                verts[i + 1] = tmp.y;
            }
            Drawr.polyLight(b.x, b.y, verts, shieldFront, shieldBack);
        }

        if(b.fin() >= indicatorPercent){
            float scaling = (b.fin() - indicatorPercent) * indicatorFadein;
            float indScaling = b.fout() / indicatorPercent;
            Draw.color(colorStart, colorEnd, scaling);
            Draw.alpha(scaling);
            Lines.arc(b.x, b.y, indicatorRadius, indScaling, b.fout() * 360 + rotation);
        }
    }
}

package rusting.entities.bullet;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

//laser but focused on a single point. Can be giving the option to collide with units or blocks on the path to the target
public class PointLaserBulletType extends BaseBulletType {
    public boolean extend = false;
    public float length = 45;
    public float stumpOffset = 3;
    public int shineSides = 4;
    public float shineCycle = 0.15f, shineWidth = 9, shineSize = 16, shineVary = 3;;
    private static float bulletLen = 0;
    public float beamSecWidth = 2.5f;

    public PointLaserBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        shrinkX = 1;
        width = 12;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        b.data = new Vec2(b.x, b.y);
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        b.fdata = b.rotation();
        bulletLen = extend ? length * b.fin() : length;
        Tmp.v1.trns(b.fdata, bulletLen);
        boolean setThing = false;
        if (!pierce) {
            Healthc entity = Damage.linecast(b, b.x, b.y, b.fdata, bulletLen);
            if (entity != null) {
                ((Vec2) b.data).set(entity.x(), entity.y());
                entity.damage(damage * Time.delta);
                setThing = true;
            }
        }
        if (!setThing) ((Vec2) b.data).trns(b.fdata, bulletLen).add(b.x, b.y);
    }

    @Override
    public void draw(Bullet b) {
        float bwidth = this.width * ((1f - shrinkX) + shrinkX * b.fout());

        Draw.z(Layer.effect + 0.1f);
        for (int i = 0; i * beamSecWidth < bwidth; i++) {
            float widthOffset = bwidth - i * beamSecWidth;
            Draw.color(frontColor);
            Draw.mixcol(backColor, bwidth/widthOffset);
            Draw.alpha(0.25f);
            Fill.quad(b.x, b.y, b.x, b.y, Tmp.v1.set(widthOffset, -stumpOffset).rotate(b.fdata + 90).add(b.x, b.y).x, Tmp.v1.y, Tmp.v2.set(-widthOffset, -stumpOffset).rotate(b.fdata + 90).add(b.x, b.y).x, Tmp.v2.y);
            Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.set(((Vec2) b.data)).x, Tmp.v3.y, Tmp.v3.x, Tmp.v3.y);

            Draw.blend(Blending.additive);
            Draw.color(Color.white);
            Draw.alpha(0.15f);
            Fill.quad(b.x, b.y, b.x, b.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y);
            Fill.quad(Tmp.v1.x, Tmp.v1.y, Tmp.v2.x, Tmp.v2.y, Tmp.v3.x, Tmp.v3.y, Tmp.v3.x, Tmp.v3.y);
            Draw.blend();
        }
        
        float shinez = (shineSize + Mathf.sin(shineCycle * b.time, shineVary)) * b.fout();
        Draw.color(frontColor);
        Fill.circle(Tmp.v3.x, Tmp.v3.y, shinez/2);
        Draw.color(backColor);
        Fill.circle(Tmp.v3.x, Tmp.v3.y, shinez/3);

        Draw.z(Layer.effect + 0.1f);
        Draw.blend(Blending.additive);
        Fill.light(Tmp.v3.x, Tmp.v3.y, shineSides, shinez, Tmp.c2.set(frontColor).lerp(backColor, Mathf.sin(shineCycle * Time.time)).a(0.35f), Tmp.c1.set(Color.white).a(0));
        for(int i: Mathf.signs){
            Draw.color(backColor);
            Draw.alpha(0.15f);
            Drawf.tri(Tmp.v3.x, Tmp.v3.y, shinez, b.fout() * shineWidth * 2, b.fdata + 90 * i);

            Draw.color(frontColor);
            Drawf.tri(Tmp.v3.x, Tmp.v3.y, shinez, b.fout() * shineWidth, b.fdata + 90 * i);
        }
        Draw.blend();
    }
}

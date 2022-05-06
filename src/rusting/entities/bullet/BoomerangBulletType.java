package rusting.entities.bullet;

import arc.Core;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.*;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.logic.Ranged;
import rusting.content.Palr;

public class BoomerangBulletType extends BounceBulletType {

    public float drawAlpha = 1f;
    public float totalRotation;
    public float rotateMag = 1f, rotScaling = 1, rotScaleMin = 0.1f, rotScaleMax = 1, rotateVisualMag = 1;
    public float rotateDirection = 0;
    public boolean stayInRange = false;
    public Func<Bullet, Float> rotationScale = b -> b.fout();

    public BoomerangBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        this.trailLength = 10;
        this.trailWidth = width/3;
        this.totalRotation = this.lifetime/30 * 360 * 4.5f;
        this.bounceCap = 2;
        this.pierceCap = 2;
        this.bounciness = 1;
        this.shrinkX = 0;
        this.trailColor = Palr.pulseBullet;
        this.hittable = false;
        this.reflectable = false;
        this.absorbable = false;
    }

    @Override
    public void draw(Bullet b) {

        if(Core.settings.getBool("settings.er.drawtrails")) trails(b).each(t -> t.draw(trailColor, trailWidth * b.fout()));

        float height = this.height * ((1f - shrinkY) + shrinkY * b.fout());
        float width = this.width * ((1f - shrinkX) + shrinkX * b.fout());
        float offset = -90 + (spin != 0 ? Mathf.randomSeed(b.id, 360f) + b.time * spin : 0f);
        float scaling = rotationScale.get(b);
        float rotation = b.rotation() + offset + totalRotation * rotateVisualMag * scaling % 360 * (b.fdata);

        Color mix = Tmp.c1.set(mixColorFrom).lerp(mixColorTo, b.fin());

        Draw.mixcol(mix, mix.a);

        Draw.color(backColor);
        Draw.alpha(drawAlpha);
        Draw.rect(backRegion, b.x, b.y, width, height, rotation);
        Draw.color(frontColor);
        Draw.alpha(drawAlpha);
        Draw.rect(frontRegion, b.x, b.y, width, height, rotation);

        Draw.reset();
    }

    public void update(Bullet b) {
        super.update(b);

        if (rotateMag > 0) {
            b.vel.rotate(rotateMag * Mathf.clamp(rotationScale.get(b), rotScaleMin, rotScaleMax) * b.fdata * Time.delta * rotScaling);
        }

        if(stayInRange && b.owner instanceof Ranged && b.dst(((Ranged) b.owner).x(), ((Ranged) b.owner).y()) > ((Ranged) b.owner).range()) b.rotation(b.angleTo(((Ranged) b.owner).x(), ((Ranged) b.owner).y()));

    }

    @Override
    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        Bullet boomerang = super.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
        boomerang.fdata = rotateDirection;
        if (rotateDirection == 0) {
            boomerang.fdata = Mathf.round(Mathf.randomSeed((int) Time.time * 2, 0, 1)) * 2 - 1;
        }
        return boomerang;
    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, int rotation){
        Bullet boomerang  = create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
        boomerang.fdata = rotation;
        return boomerang;
    }

    public static Func<Bullet, Float> inverseScale = b -> b.fin();
}

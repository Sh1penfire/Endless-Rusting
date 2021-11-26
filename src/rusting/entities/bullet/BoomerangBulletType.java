package rusting.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.*;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.logic.Ranged;
import rusting.content.Palr;

public class BoomerangBulletType extends BounceBulletType {

    //preferably a copy of this boomerang but only with rotateRight inverted
    public BulletType other = null;
    public float drawAlpha = 1f;
    public boolean rotateRight = true;
    public float revolutions = 10;
    public float rotateTotalAngle = revolutions * 360;
    public float rotateMag = 1f, rotScaling = 1, rotScaleMin = 0.1f, rotScaleMax = 1, rotateVisualMag = 1;
    public boolean reverseBoomerangRotScale = false, stayInRange = false;

    public BoomerangBulletType(float speed, int damage, String sprite) {
        super(speed, damage, sprite);
        this.trailLength = 10;
        this.trailWidth = width/3;
        this.revolutions = this.lifetime/30;
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
        float scaling = reverseBoomerangRotScale ? b.fout() : b.fin();
        float rotation = b.rotation() + offset + rotateTotalAngle * rotateVisualMag * scaling % 360 * (rotateRight ? -1 : 1);

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
            b.vel.rotate(rotateMag * Mathf.clamp(reverseBoomerangRotScale ? b.fout() : b.fin(), rotScaleMin, rotScaleMax) * (rotateRight ? -1 : 1) * Time.delta * rotScaling);
        }

        if(stayInRange && b.owner instanceof Ranged && b.dst(((Ranged) b.owner).x(), ((Ranged) b.owner).y()) > ((Ranged) b.owner).range()) b.rotation(b.angleTo(((Ranged) b.owner).x(), ((Ranged) b.owner).y()));

    }

    @Override
    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        if (other != null && Mathf.randomSeed((int) Time.time * 2, 0, 1) > 0.5 && other instanceof BoomerangBulletType) {
            return ((BoomerangBulletType) other).createBoomerang(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
        } else return super.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
    }

    //no chance to spawn other boomerang
    public Bullet createBoomerang(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data) {
        return super.create(owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data);
    }
}

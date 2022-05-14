package rusting.entities.abilities;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import rusting.EndlessRusting;
import rusting.content.Palr;

public class SpeedupAbility extends MountAbility{
    protected float timer;
    public float boostIntensity = 1.75f;
    private float domeAlpha = 0;

    public static Seq<Bullet> speedupBullets = Seq.with();

    public SpeedupAbility(){
        this.mountName = "time-speedup-port";
        this.reload = 255;
        this.laserOffset = 0;
        this.mirror = true;
    }

    @Override
    public void update(Unit unit) {
        super.update(unit);

        timer += Time.delta;

        if(timer >= reload){

            Vars.indexer.eachBlock(unit, range, b -> true, b -> {
                b.applyBoost(boostIntensity, reload * 3);
            });
            Fx.overdriveWave.at(unit.x, unit.y, range);

            timer = 0f;
            domeAlpha = 1;
        }
        domeAlpha = Math.max(0, domeAlpha - 0.005f);

        Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2, b -> {
            if(unit.dst(b) > range || b.type == null || b.team == unit.team || speedupBullets.contains(b)) return;
            speedupBullets.add(b);
        });
    }

    public void drawMount(Unit unit, float angle, float reverse){

        float mountx = mountx(unit, reverse);
        float mounty = mounty(unit, reverse);

        Draw.z(Mathf.lerp(Layer.groundUnit, Layer.flyingUnit, unit.elevation));

        TextureRegion mount = Core.atlas.find(EndlessRusting.modname + "-" + mountName);
        if (Core.atlas.isFound(mount)) Draw.rect(mount, unit.x + mountx, unit.y + mounty,mount.width/4 * reverse, mount.height/4, unit.angleTo(unit.x + mountx, unit.y + mounty) + 90);
        drawLaser(unit, mountx, mounty);

        if(reverse == 1) return;
        //useable later, though for now make it always draw
        float domeAlphaMultiplier = 1;

        Fill.light(unit.x, unit.y, 8 + (int) range/3, range, Tmp.c1.set(Pal.lightTrail).a(domeAlpha/2 * domeAlphaMultiplier), Tmp.c2.set(Palr.voidBullet).a(domeAlpha/5 * domeAlphaMultiplier));
    }

    @Override
    public void drawLaser(Unit unit, float mountx, float mounty) {

        float angle = unit.angleTo(unit.x + mountx, unit.y + mounty) + 90;
        float sourcx = unit.x + mountx + Angles.trnsx(angle, 0, laserOffset), sourcy = unit.y + mounty + Angles.trnsy(angle, 0, laserOffset);
        float edgex = unit.x, edgey = unit.y;
        float width = maxWidth;

        Draw.z(Layer.flyingUnit + 1);

        Draw.color(Palr.voidBullet, Pal.lightTrail, timer / reload);
        Lines.stroke(width * 1.35f);
        Lines.line(sourcx, sourcy, edgex, edgey);
        Fill.circle(edgex, edgey, width * 1.35f);
        Fill.circle(sourcx, sourcy, width * 0.85f);
        Draw.reset();
    }
}

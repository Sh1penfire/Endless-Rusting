package rusting.content;

import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Fires;
import mindustry.entities.Units;
import mindustry.entities.bullet.PointBulletType;
import mindustry.gen.*;
import mindustry.type.Liquid;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class PressurizedLiquidBulletType extends PointBulletType {
    private static float cdist = 0f;
    private static Unit result;
    public boolean pierceArmor = true;

    public Liquid liquid;

    public PressurizedLiquidBulletType(Liquid liquid){
        this.liquid = liquid;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);

        float px = b.x + b.lifetime * b.vel.x,
                py = b.y + b.lifetime * b.vel.y,
                rot = b.rotation();

        Geometry.iterateLine(0f, b.x, b.y, px, py, trailSpacing, (x, y) -> {
            trailEffect.at(x, y, rot);
        });

        b.time = b.lifetime;
        b.set(px, py);

        //calculate hit entity

        cdist = 0f;
        result = null;
        float range = 1f;

        Units.nearbyEnemies(b.team, px - range, py - range, range*2f, range*2f, e -> {
            if(e.dead()) return;

            e.hitbox(Tmp.r1);
            if(!Tmp.r1.contains(px, py)) return;

            float dst = e.dst(px, py) - e.hitSize;
            if((result == null || dst < cdist)){
                result = e;
                cdist = dst;
            }
        });

        if(result != null){
            if(pierceArmor){
                float damageAmount = damage;
                damage = 0;
                b.collision(result, px, py);
                damage = damageAmount;
                result.damagePierce(damageAmount);
            }
            else b.collision(result, px, py);
        }else{
            Building build = Vars.world.buildWorld(px, py);
            if(build != null && build.team != b.team){
                build.collision(b);
            }
            if(liquid.canExtinguish()){
                Tile tile = world.tileWorld(b.x, b.y);
                if(tile != null && Fires.has(tile.x, tile.y)){
                    Fires.extinguish(tile, 100f);
                    hit(b);
                }
            }
        }

        b.remove();

        b.vel.setZero();
    }
}

package rusting.world.blocks.defense.turret;

import arc.math.Mathf;
import arc.util.Tmp;
import mindustry.gen.Bullet;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class EmperorTurret extends ItemTurret {

    public float delayRange = 0, minDelay = 35;
    public float projectileSpawnRange = 5, projectileMinDst = 30;

    public EmperorTurret(String name) {
        super(name);
    }

    public class EmperorTurretBuild extends ItemTurretBuild{

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
                Tmp.v1.trns(Mathf.random(360), projectileMinDst + Mathf.random(projectileSpawnRange));
                bullet.set(targetPos.x + Tmp.v1.x, targetPos.y + Tmp.v1.y);
                bullet.rotation( Tmp.v1.angle() - 180);
                bullet.lifetime = bullet.type.lifetime * (1f + Mathf.range(velocityRnd));
        }
    }
}

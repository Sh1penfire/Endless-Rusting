package rusting.world.blocks.defense.turret;

import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class EmperorTurret extends ItemTurret {

    public float delayRange = 0, minDelay = 35;
    public float projectileSpawnRange = 5, projectileMinDst = 30;

    public EmperorTurret(String name) {
        super(name);
    }

    public class EmperorTurretBuild extends ItemTurretBuild{
        @Override
        protected void bullet(BulletType type, float angle) {
            for (int i = 0; i < shots; i++) {
                Time.run(minDelay + Mathf.random(delayRange), () -> {
                    Tmp.v1.trns(Mathf.random(360), projectileMinDst + Mathf.random(projectileSpawnRange));
                    type.create(this, team, targetPos.x + Tmp.v1.x, targetPos.y + Tmp.v1.y, Tmp.v1.angle() - 180, 1f + Mathf.range(velocityInaccuracy), 1);
                });
            }
        }
    }
}

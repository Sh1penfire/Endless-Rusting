package rusting.world.blocks.defense.turret;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;

public class BoomerangTurret extends AutoreloadItemTurret {


    public BoomerangTurret(String name) {
        super(name);
    }

    public class BoomerangTurretBuild extends AutoreloadItemTurretBuild{
        @Override
        protected void shoot(BulletType type) {

            for (int j = 0; j < shots; j++) {
                final int[] o = new int[]{j};
                Time.run(j * burstSpacing/timeScale, () -> {
                    if(this == null || this.dead || !this.isAdded()) return;;
                    int i = o[0];
                    tr.trns(i * spread + rotation, shootLength);

                    effects();
                    float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

                    type.create(this, team, x + tr.x, y + tr.y, i * spread + rotation, type.damage, 1f + Mathf.range(velocityInaccuracy), lifeScl, this);
                });
            }

            shotCounter++;

            recoil = recoilAmount;
            heat = 1f;
            useAmmo();
        }

        protected void effects(){
            Effect fshootEffect = shootEffect == Fx.none ? peekAmmo().shootEffect : shootEffect;
            Effect fsmokeEffect = smokeEffect == Fx.none ? peekAmmo().smokeEffect : smokeEffect;

            float rotation = angleTo(x + tr.x, y + tr.y);

            fshootEffect.at(x + tr.x, y + tr.y, rotation);
            fsmokeEffect.at(x + tr.x, y + tr.y, rotation);
            shootSound.at(x + tr.x, y + tr.y, Mathf.random(0.9f, 1.1f));

            if(shootShake > 0){
                Effect.shake(shootShake, shootShake, this);
            }

            recoil = recoilAmount;
        }
    }
};

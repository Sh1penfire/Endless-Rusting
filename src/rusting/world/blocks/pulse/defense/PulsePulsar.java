package rusting.world.blocks.pulse.defense;

import arc.audio.Sound;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.*;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Posc;
import mindustry.gen.Sounds;
import mindustry.logic.Ranged;
import mindustry.type.StatusEffect;
import rusting.content.*;
import rusting.world.blocks.pulse.PulseBlock;

//haha, it's funny
//get it?
//...no?
//ok then...
public class PulsePulsar extends PulseBlock {
    public PulsePulsar(String name) {
        super(name);
        projectile = RustingBullets.boltingVortex;
    }

    //ticks before the block can shoot again
    public float reloadTime = 60;
    //number of shots
    public float shots = 1;
    //you know where this is going
    public float bursts = 3;
    //separate shot and burst count
    public float burstSpacing = 0;
    //inaccuracy
    public float inaccuracy = 0;
    //sound pitch
    public float startSoundPitchMin = 0.1f, startSoundPitchMax = 0.3f,  shootSoundPitchMin = 0.65f, shootSoundPitchMax = 0.85f;
    //shoot sound
    public Sound shootSoundStart = Sounds.explosion, shootSound = Sounds.missile;
    //effect played when you start shooting
    public Effect shootStartEffect = Fxr.launchCraeWeavers, shootEffect = Fxr.craeWeaversResidue;
    //range
    public float range = 0;
    public float velocityRnd = 0;
    public float speedScl = 1;
    public float lifetimeScl = 1;
    public StatusEffect status = RustingStatusEffects.balancedPulsation;
    public float statusDuration = 160;

    @Override
    public float projectileRange(){
        return super.projectileRange() * lifetimeScl * speedScl;
    }

    public class PulsePulsarBuild extends PulseBlockBuild implements Ranged {
        public float reload = 0;
        @Nullable
        public Posc target = null;
        public Vec2 targetPos = new Vec2();

        @Override
        public float range(){
            return projectile != null ? projectileRange() : range;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(reload < reloadTime) reload += Time.delta;
            else {
                target = Units.closestTarget(team, x, y, range());
                if(target != null){
                    targetPosition(target);
                    if (canShoot()) shoot();
                    reload -= reloadTime;
                }
            }
        }

        public void targetPosition(Posc pos){
            if(pos == null) return;
            float speed = projectile.speed * speedScl;
            //slow bullets never intersect
            if(speed < 0.1f) speed = 9999999f;

            targetPos.set(Predict.intercept(this, pos, speed));
            if(targetPos.isZero()){
                targetPos.set(pos);
            }
        }

        public boolean canShoot(){
            return pConsValid() && target != null;
        }

        @Override
        public void overloadEffect() {
            super.overloadEffect();
        }

        public void shoot(){
            float speedScaling = projectile.scaleLife ? projectileRange()/dst(targetPos) : size * 0.6f * speedScl;

            if(projectile != null){
                if(bursts > 1){
                    for(int i = 0; i < bursts; i++){
                        Time.run(burstSpacing * i, () -> {
                            for(int i1 = 0; i1 < shots; i1++){
                                bullet(projectile, speedScaling);
                                shootEffect.at(x, y);
                                shootSound.at(x, y, Mathf.random(shootSoundPitchMin, shootSoundPitchMax));

                                consume();
                                pConsume();
                            }
                        });
                    }
                }
                else if(shots > 1){
                    for(int i = 0; i < shots; i++){
                        bullet(projectile, speedScaling);
                        shootEffect.at(x, y);
                        shootSound.at(x, y, Mathf.random(shootSoundPitchMin, shootSoundPitchMax));

                        consume();
                        pConsume();
                    }
                }

                shootStartEffect.at(x, y);
            }
            Units.nearbyEnemies(team, x - range()/2, y - range()/2, range(), range(), u -> {
                u.apply(status, statusDuration);
            });
            shootSoundStart.at(x, y, Mathf.random(startSoundPitchMax, startSoundPitchMax));

        }

        public void bullet(BulletType bullet, float speedScl){
            bullet.create(this, team, x, y, angleTo(targetPos.x, targetPos.y) + Mathf.random(-inaccuracy, inaccuracy), speedScl, lifetimeScl);
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(reload);
            w.f(targetPos.x);
            w.f(targetPos.y);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            reload = r.f();
            targetPos.set(r.f(), r.f());
        }

        @Override
        public void draw() {
            super.draw();
        }
    }
}

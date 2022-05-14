package rusting.entities.bullet;

import arc.func.Cons;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.Units;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;

//includes cons for some methods in BulletType
public class BaseBulletType extends BasicBulletType {
    @Nullable
    public Cons<Bullet> consUpdate;

    @Nullable
    public Cons<Bullet> consDespawned;

    @Nullable
    public Cons<Bullet> consHit;

    public HomingType homingType;

    public boolean useRange = false;
    public float range = 0;
    public float trueSpeed = speed;
    public boolean useTrueSpeed = true;
    public float rotationOffset = 0;

    public BaseBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update(Bullet b) {
        if(consUpdate != null) consUpdate.get(b);

        if (this.weaveMag > 0.0F) {
            b.vel.rotate(Mathf.sin(b.time + 3.1415927F * this.weaveScale / 2.0F, this.weaveScale, this.weaveMag * (float)(Mathf.randomSeed((long)b.id, 0, 1) == 1 ? -1 : 1)) * Time.delta);
        }

        if (this.trailChance > 0.0F && Mathf.chanceDelta((double)this.trailChance)) {
            this.trailEffect.at(b.x, b.y, this.trailParam, this.trailColor);
        }


    }

    @Override
    public void hit(Bullet b, float x, float y) {
        if(consHit != null) consHit.get(b);
        super.hit(b, x, y);
    }

    @Override
    public void despawned(Bullet b) {
        if(consDespawned != null) consDespawned.get(b);
        super.despawned(b);
    }

    public Bullet create(@Nullable Entityc owner, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data){
        Bullet bullet = Bullet.create();
        bullet.type = this;
        bullet.owner = owner;
        bullet.team = team;
        bullet.time = 0f;
        bullet.vel.trns(angle += rotationOffset, (useTrueSpeed ? trueSpeed : speed) * velocityScl);
        if(backMove){
            bullet.set(x - bullet.vel.x * Time.delta, y - bullet.vel.y * Time.delta);
        }else{
            bullet.set(x, y);
        }
        bullet.lifetime = lifetime * lifetimeScl;
        bullet.data = data;
        bullet.drag = drag;
        bullet.hitSize = hitSize;
        bullet.damage = (damage < 0 ? this.damage : damage) * bullet.damageMultiplier();
        bullet.add();

        if(keepVelocity && owner instanceof Velc) bullet.vel.add(((Velc) bullet.owner).vel());
        return bullet;
    }


    public abstract class HomingType{
        public BaseBulletType type;
        public abstract void update(Bullet b);
    }

    public class GenericHoming extends HomingType{
        @Override
        public void update(Bullet b){
            if (type.homingPower > 1.0E-4F && b.time >= type.homingDelay) {
                Teamc target = Units.closestTarget(b.team, b.x, b.y, type.homingRange, (e) -> {
                    return e.isGrounded() && type.collidesGround || e.isFlying() && type.collidesAir;
                }, (t) -> type.collidesGround
                );
                if (target != null) {
                    b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), type.homingPower * Time.delta * 60));
                }
            }
        }
    }
}

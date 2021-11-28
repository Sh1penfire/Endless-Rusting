package rusting.entities.bullet;

import arc.func.Cons;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.*;

//includes cons for some methods in BulletType
public class ConsBulletType extends BasicBulletType {
    @Nullable
    public Cons<Bullet> consUpdate;

    @Nullable
    public Cons<Bullet> consDespawned;

    @Nullable
    public Cons<Bullet> consHit;

    public boolean useRange = false;
    public float range = 0;
    public float trueSpeed = speed;
    public boolean useTrueSpeed = true;
    public float rotationOffset = 0;

    public ConsBulletType(float speed, float damage, String sprite){
        super(speed, damage, sprite);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update(Bullet b) {
        if(consUpdate != null) consUpdate.get(b);
        super.update(b);

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

    @Override
    public float range() {
        return useRange ? range : super.range();
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
}

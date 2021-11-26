package rusting.entities.bullet;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;
import rusting.content.Palr;

public class BulletSpawnBulletType extends ConsBulletType{

    //fired on bullet hit/despawn. Correctly rotated.
    public BulletType finalFragBullet = null;
    public float finalFragBullets = 1;

    public float scaleDrawIn = 4, scaleDrawOut = 4, drawSize = 4;

    public static class BulletSpawner{
        //Bullelt to shoot. Shoudn't be null
        public BulletType bullet = Bullets.standardCopper;
        //how much time it takes to shoot the bullet
        public float reloadTime = 1;
        //time between the bullet first spawning and despawning that the spawner can't fire
        public float intervalIn = 0, intervalOut = 0;
        //inaccuracy when shooting at the target
        public float inaccuracy = 0;
        //random spray that happens when theres no rotation value set
        public float idleInaccuracy = 360;
        //effect played
        public Effect shootEffect = Fx.none;
        //allows owner to aim for the bullet.
        public boolean manualAiming = true;
        //allows bullet to aim. only comes after manualAiming is false or owner is dead
        public boolean bulletTargeting = true;
        //if no targets found, shoot anyways
        public boolean shootWhenIdle = true;
        //sound played upon bullet shoot
        public Sound shootSound = Sounds.none;
        //mutlipliers for bullet lifetime and speed
        public float lifetimeMultiplier = 1, speedMultiplier = 1;
    }

    public Seq<BulletSpawner> bullets = new Seq<BulletSpawner>();

    private BulletSpawner spawner;
    private float rotation;
    private Unit unitOwner;

    public BulletSpawnBulletType(float speed, float damage, String sprite) {
        super(speed, damage, sprite);
        this.trueSpeed = speed;
        this.keepVelocity = false;
        this.collides = false;
        this.frontColor = Palr.lightstriken;
        this.backColor = Palr.dustriken;
        this.trailChance = 0;
    }

    @Override
    public void init(Bullet b) {
        super.init(b);
        if(b != null){
            b.data = new float[bullets.size];
        }
    }

    @Override
    public void init() {
        super.init();
        if(useRange && range <= 0) {
            bullets.each(b -> {
                if (b.bullet.range() > range && b.bullet != this) range = b.bullet.range();
            });
            useRange = false;
            range += range();
            useRange = true;
        }
    }

    @Override
    public void update(Bullet b) {
        super.update(b);
        for (int i = 0; i < bullets.size; i++) {
            spawner = bullets.get(i);
            //I don't get the logic behind this, interval in and interval out do the opposit of what I made them to do
            if(spawner.intervalIn >= b.fin() * lifetime || spawner.intervalOut >= b.fout() * lifetime) break;
            if(((float[]) b.data)[i] < spawner.reloadTime){
                ((float[]) b.data)[i] += Time.delta;
            }
            else {
                boolean foundTarget = false;
                rotation = b.rotation() + Mathf.random(spawner.idleInaccuracy);
                if(spawner.manualAiming){
                    if(b.owner != null && (b.owner instanceof Unit || b.owner instanceof TurretBuild)){
                        if(b.owner instanceof Unit){
                            unitOwner = ((Unit) b.owner);
                            rotation = Mathf.angle(unitOwner.aimX - b.x,unitOwner.aimY - b.y);
                            if(spawner.inaccuracy != 0) rotation += Math.random() * spawner.inaccuracy;
                        }
                        else if(b.owner instanceof TurretBuild){
                            rotation = b.angleTo(((TurretBuild) b.owner).targetPos);
                            if(spawner.inaccuracy != 0) rotation += Math.random() * spawner.inaccuracy;
                        }
                        foundTarget = true;
                    }
                }
                else if(spawner.bulletTargeting){
                    Teamc targ = Units.closestTarget(b.team, b.x, b.y, spawner.bullet.range(), u -> spawner.bullet.collidesGround && !u.isFlying() || spawner.bullet.collidesAir && u.isFlying());
                    if(targ instanceof Posc){
                        rotation = b.angleTo(targ);
                        if(spawner.idleInaccuracy != 0) rotation += Math.random() * spawner.inaccuracy;
                        foundTarget = true;
                    }
                }
                if(spawner.shootWhenIdle || foundTarget) {
                    spawner.bullet.create(b.owner, b.team, b.x, b.y, rotation, spawner.speedMultiplier, spawner.lifetimeMultiplier);
                    if (spawner.shootSound != Sounds.none) spawner.shootSound.at(b.x, b.y);
                    spawner.shootEffect.at(b.x, b.y, rotation);
                    ((float[]) b.data)[i] = 0;
                    b.fdata = rotation;
                }
            }
        }
    }

    @Override
    public void hit(Bullet b, float x, float y) {
        super.hit(b, x, y);

        if(finalFragBullet != null){
            for(int i = 0; i < finalFragBullets; i++){
                float len = Mathf.random(1f, 7f);
                float a = b.fdata + Mathf.range(fragCone/2) + fragAngle;
                finalFragBullet.create(b, x + Angles.trnsx(a, len), y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
            }
        }

    }

    @Override
    public void draw(Bullet b) {
        Draw.color(frontColor, backColor, b.fin());
        float initialScaling = Math.min(b.fout() * scaleDrawOut, Math.min(b.fin() * scaleDrawIn, 1));
        float scaling = b.fout() * drawSize;
        if(!Core.settings.getBool("bloom")) Draw.alpha(initialScaling * initialScaling);
        else Draw.alpha(initialScaling);
        Fill.circle(b.x, b.y, initialScaling * drawSize);
        float spacing = 2;
        for(float i = 0; i < scaling; i++){
            Lines.stroke((scaling - i) * spacing/5);
            Lines.circle(b.x, b.y, (scaling - i) * spacing + i + drawSize);
        }
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
        bullet.vel.trns(angle, trueSpeed * velocityScl);
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

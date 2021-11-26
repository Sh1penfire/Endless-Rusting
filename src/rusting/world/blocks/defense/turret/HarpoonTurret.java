package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.UnitType;
import rusting.content.Fxr;
import rusting.entities.bullet.BlockHarpoonBulletType;
import rusting.graphics.Drawr;

public class HarpoonTurret extends AutoreloadItemTurret {

    private Vec2 drawHarpoonPos = new Vec2();
    public TextureRegion topRegion;
    public float detachRange = 50;
    public float basePullStrength = 0;
    public float pullStrength = 70;
    public float retractSpeed = 2;
    private float chainShadowOffset = 1/12;

    private Seq<Item> keys = Seq.with();


    public HarpoonTurret(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
        ammoTypes.each((item, bullet) -> {
            keys.add(item);
        });
    }

    @Override
    public void load() {
        super.load();
        topRegion = Core.atlas.find(name + "-top");
    }

    public class HarpoonTurretBuild extends AutoreloadItemTurretBuild{

        public Vec2 harpoonPosition = new Vec2(x, y);
        public Bullet harpoonBullet;
        public boolean harpoonShot = false;
        public boolean harpoonStuck = false;
        public boolean harpoonRetracting = false;
        public float relativeRotation = 0;
        public float distanceOffset = 0;
        public Unit stuckOn;
        public int harpoonAmmo = 0;
        public float lastBulletLifetime = 0;

        //damage units every 10 ticks
        public float damageInterval = 0;
        Seq<Cons> consSeq = Seq.with();

        public boolean hasHarpoon(){
            return true;
        }

        public BlockHarpoonBulletType getHarpoon(){
            return (BlockHarpoonBulletType) ammoTypes.get(keys.get(harpoonAmmo));
        }

        @Override
        public void updateTile() {
            if(consSeq.size > 0){
                consSeq.each(c -> c.get(consSeq));
                consSeq.clear();
            }
            unit.ammo((float)unit.type().ammoCapacity * totalAmmo / maxAmmo);

            if(!validateTarget()) target = null;

            wasShooting = false;

            recoil = Mathf.lerpDelta(recoil, 0f, restitution);
            heat = Mathf.lerpDelta(heat, 0f, cooldown);

            unit.health(health);
            unit.rotation(rotation);
            unit.team(team);
            unit.set(x, y);

            if(logicControlTime > 0){
                logicControlTime -= Time.delta;
            }

            if(hasAmmo()){

                if(timer(timerTarget, targetInterval)){
                    findTarget();
                }

                if(validateTarget()){
                    boolean canShoot = true;

                    if(isControlled()){ //player behavior
                        targetPos.set(unit.aimX(), unit.aimY());
                        canShoot = unit.isShooting();
                    }else if(logicControlled()){ //logic behavior
                        canShoot = logicShooting;
                    }else{ //default AI behavior
                        targetPosition(target);

                        if(Float.isNaN(rotation)){
                            rotation = 0;
                        }
                    }

                    float targetRot = angleTo(targetPos);

                    if(shouldTurn()){
                        turnToTarget(targetRot);
                    }

                    if(Angles.angleDist(rotation, targetRot) < shootCone && canShoot && !harpoonShot && !harpoonStuck){
                        wasShooting = true;
                        updateShooting();
                    }
                }
            }

            if(acceptCoolant){
                updateCooling();
            }

            if(harpoonRetracting){
                harpoonPosition.add(Tmp.v2.trns(Tmp.v1.set(harpoonPosition).angleTo(this.x, this.y), Mathf.clamp(dst(harpoonPosition), 0, retractSpeed * timeScale())));
                if(within(harpoonPosition, shootLength + 1)){
                    harpoonRetracting = false;
                    harpoonShot = false;
                    reload = reloadTime * autoreloadThreshold;
                    Sounds.click.at(x, y);
                }
            }
            else if(harpoonShot && harpoonBullet != null && !harpoonStuck && !harpoonRetracting){
                harpoonPosition.x = harpoonBullet.x;
                harpoonPosition.y = harpoonBullet.y;
                if(harpoonBullet.lifetime != 0) lastBulletLifetime = harpoonBullet.fout();
                rotation = angleTo(harpoonBullet);
            }
            else if(harpoonStuck){
                if(stuckOn == null || stuckOn.dead == true) {
                    harpoonStuck = false;
                    harpoonShot = false;
                    harpoonRetracting = true;
                }
                else {
                    harpoonPosition.set(stuckOn.x, stuckOn.y).add(Tmp.v2.trns(stuckOn.rotation + relativeRotation, distanceOffset));

                    tr.trns(rotation, shootLength);

                        if(Tmp.v1.set(stuckOn.x, stuckOn.y).add(stuckOn.vel).dst(x, y) > range){
                            harpoonStuck = false;
                            harpoonShot = false;
                            harpoonRetracting = false;
                            if(hasHarpoon()) {
                                BlockHarpoonBulletType bullet = getHarpoon();
                                stuckOn.damagePierce(bullet.tearDamage);
                                stuckOn.apply(bullet.bleedEffect, bullet.bleedEffectDuration);
                                Fxr.instaltSummonerExplosion.at(harpoonPosition.x, harpoonPosition.y);
                                Geometry.iterateLine(0, x, y, stuckOn.x, stuckOn.y, getHarpoon().chainRegion.height/4, (x2, y2) -> {
                                    Fxr.regionDrop.at(x2, y2, angleTo(harpoonPosition) - 90, getHarpoon().chainRegion);
                                });
                            };
                            stuckOn = null;

                        }
                        else {
                            if(isShooting()) {
                                stuckOn.impulse(Tmp.v2.trns(Tmp.v1.set(stuckOn.x, stuckOn.y).angleTo(x + tr.x, y + tr.y), Mathf.lerp(pullStrength, basePullStrength, Mathf.clamp(1 - (dst(stuckOn) + detachRange) / (range - detachRange), 0, 1)) * Time.delta));
                                if(hasHarpoon()) {
                                    BlockHarpoonBulletType bullet = getHarpoon();
                                    bullet.updateUnitEffect(this, stuckOn);

                                    if (damageInterval >= 10) {
                                        stuckOn.damagePierce(bullet.ripDamage * Mathf.clamp((dst(stuckOn) - detachRange) / (range - detachRange), 0, 1));
                                        damageInterval -= 10;
                                    } else damageInterval += Time.delta;
                                }
                            }
                        }
                        float toHarpoon = angleTo(harpoonPosition.x, harpoonPosition.y);
                        rotation = Mathf.clamp(Angles.moveToward(rotation, toHarpoon, 1f * Time.delta),toHarpoon - 15, toHarpoon + 15);
                }
            }
            if(reload <= autoreloadThreshold * reloadTime && !isShooting() && !harpoonShot && !harpoonStuck) {
                reload = Math.min(reload + Time.delta / reloadTime * baseReloadSpeed() * 60 * 3, autoreloadThreshold * reloadTime);
            }
        }

        @Override
        public boolean shouldTurn() {
            return super.shouldTurn() && !harpoonRetracting && !harpoonStuck && !harpoonShot;
        }

        @Override
        public void draw() {
            super.draw();
            BlockHarpoonBulletType bullet = (BlockHarpoonBulletType) Bullet.create().type;
            boolean drawHarpoon = false;
            float width = 32, height = 32;
            float alpha = 1;
            float harpoonRotation = rotation - (Core.settings.getBool("settings.er.doggoharpoons", false) ? 0 : 90);
            tr.trns(rotation, shootLength);

            if(harpoonStuck){
                bullet = getHarpoon();
                drawHarpoonPos.set(harpoonPosition.x, harpoonPosition.y);
                drawHarpoon = true;
                harpoonRotation = stuckOn.rotation + relativeRotation + 90;
            }
            else if(harpoonRetracting && hasHarpoon()){
                bullet = getHarpoon();
                drawHarpoonPos.set(harpoonPosition);
                drawHarpoon = true;
            }
            else if(hasHarpoon() && !harpoonShot){
                bullet = getHarpoon();
                drawHarpoonPos.set(x + tr.x + tr2.x, y + tr.y + tr2.y);
                alpha = reload/reloadTime;
                drawHarpoon = true;
            }
            else if(harpoonShot && harpoonBullet.type instanceof BlockHarpoonBulletType){
                bullet = ((BlockHarpoonBulletType) harpoonBullet.type);
                drawHarpoonPos.set(harpoonBullet.x, harpoonBullet.y);
                drawHarpoon = true;
                harpoonRotation = harpoonBullet.rotation() - 90;
            }

            if(drawHarpoon && bullet != null){

                //to be used for the end of the chain's shadow and the harpoon itslef
                float e;
                if(stuckOn != null) e = Math.max(stuckOn.elevation, -1);
                    //draw the shadow like a unit
                else e = chainShadowOffset;

                if(harpoonStuck || harpoonShot || harpoonRetracting){
                    tr.trns(rotation, shootLength - 3);
                    Draw.color(Pal.shadow);
                    Drawr.drawChain(bullet.chainRegion, x + tr.x + tr2.x + UnitType.shadowTX * chainShadowOffset, y + tr.y + tr2.y + UnitType.shadowTY * chainShadowOffset, drawHarpoonPos.x + UnitType.shadowTX * e, drawHarpoonPos.y + UnitType.shadowTY * e, -90);
                    Draw.color(Color.white);
                    Drawr.drawChain(bullet.chainRegion, x + tr.x + tr2.x, y + tr.y + tr2.y, drawHarpoonPos.x, drawHarpoonPos.y, -90);
                    //change the colour of the chains and make it draw like a unit's shadow
                }
                Draw.color(Pal.shadow);
                Draw.rect(bullet.frontRegion, drawHarpoonPos.x + UnitType.shadowTX * e, drawHarpoonPos.y + UnitType.shadowTY * e, rotation - 90);
                Draw.color(bullet.backColor);
                Draw.alpha(alpha);
                Draw.rect(bullet.backRegion, drawHarpoonPos.x, drawHarpoonPos.y, bullet.width, bullet.height,  harpoonRotation);
                Draw.color(bullet.frontColor);
                Draw.alpha(alpha);
                Draw.rect(bullet.frontRegion, drawHarpoonPos.x, drawHarpoonPos.y, bullet.width, bullet.height,  harpoonRotation);
            }
            Draw.reset();
            Draw.z(Layer.turret);
            Draw.rect(topRegion, x + tr2.x, y + tr2.y, rotation - 90);
        }

        @Override
        protected void bullet(BulletType type, float angle) {
            float lifeScl = type.scaleVelocity ? Mathf.clamp(Mathf.dst(x + tr.x, y + tr.y, targetPos.x, targetPos.y) / type.range(), minRange / type.range(), range / type.range()) : 1f;

            harpoonBullet = type.create(this, team, x + tr.x, y + tr.y, angle, 1f + Mathf.range(velocityInaccuracy), lifeScl);
            harpoonShot = true;
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(harpoonPosition.x);
            w.f(harpoonPosition.y);
            w.i(harpoonAmmo);
            w.bool(harpoonBullet != null);
            if(harpoonBullet != null){
                w.f(harpoonBullet.x);
                w.f(harpoonBullet.y);
                w.f(harpoonBullet.vel().len());
                w.f(harpoonBullet.rotation());
                w.f(lastBulletLifetime);
            }
            w.bool(harpoonShot);
            w.bool(harpoonStuck);
            w.bool(harpoonRetracting);
            w.f(relativeRotation);
            w.f(distanceOffset);
            w.bool(stuckOn != null);
            if(stuckOn != null){
                w.f(stuckOn.elevation);
                w.i(stuckOn.team.id);
                w.f(stuckOn.x);
                w.f(stuckOn.y);
            }
            w.f(damageInterval);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            harpoonPosition.set(r.f(), r.f());
            harpoonAmmo = r.i();
            boolean hasBullet = r.bool();
            if(hasBullet){
                Tmp.v1.set(r.f(), r.f());
                float bulletSpeed = r.f();
                float bulletRotation = r.f();
                float bulletLifetime = r.f();
                harpoonBullet = getHarpoon().create(this, team, Tmp.v1.x, Tmp.v1.y, bulletRotation, bulletSpeed/getHarpoon().speed, bulletLifetime);
            }
            harpoonShot = r.bool();
            harpoonStuck = r.bool();
            harpoonRetracting = r.bool();
            relativeRotation = r.f();
            distanceOffset = r.f();
            if(r.bool()){
                float elevation = r.f();
                Team untiTeam = Team.get(r.i());
                Vec2 unitpos = new Vec2(r.f(), r.f());
                consSeq.add(o -> stuckOn = Units.closest(untiTeam, unitpos.x, unitpos.y, getHarpoon().hitSize + 28, u -> u.elevation == elevation));
            }
            damageInterval = r.f();
        }
    }
}

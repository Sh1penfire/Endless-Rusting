package rusting.world.blocks.defense.turret;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import rusting.content.*;

//Specialized turret which shoots it's projectile so fast that they instantly travel to their target location, intersecting any enemies along the way.
public class BerthaTurret extends PowerTurret {
    protected static  BerthaTurretBuild current = null;
    public float reclaimRate = 0.000416f;
    public float reclaimMax = 1.3f;
    public float realRange = 0;

    public float effectInterval = 5;

    public float xOffset, yOffset;

    public float shellRecoil, shellReloadOffset;

    //it can stay active for 1500 ticks at most
    public float maxCharge = 1500;


    public TextureRegion topRegion, outlineRegion, stateRegion, reloadRegion;
    public TextureRegion[] shells = {null, null};
    public TextureRegion[] shellOutlines = {null, null};
    public TextureRegion[] shellHeats = {null, null};

    protected static Vec2 shellPos = new Vec2();

    //static variable which is set
    public static Color stateColor = new Color();

    public BerthaTurret(String name) {
        super(name);
        shootShake = 7;
        shootEffect = Fxr.instaltSummonerExplosionLarge;
        targetable = false;
        unitSort = (u, x, y) -> -u.maxHealth;
        configurable = true;
        config(Integer.class, (block, config) -> {
            if(!(block instanceof BerthaTurretBuild)) return;
            BerthaTurretBuild entity = (BerthaTurretBuild) block;
            switch (config){
                case 0: {
                    entity.active = false;
                    break;
                }
                case 1: {
                    if (entity.chargef() >= 1 && !entity.active) {
                        Sounds.click.at(entity.x, entity.y, 0);
                        entity.active = !entity.active;
                    }
                    break;
                }
                case 2: {
                    entity.auto = !entity.auto;
                }
            }
        });
    }

    @Override
    public boolean canBreak(Tile tile) {
        return (Vars.state.isEditor() || Vars.state.rules.infiniteResources) && super.canBreak(tile);
    }

    public static BerthaTurretBuild toTurret(Building b){
        current = (BerthaTurretBuild) b;
        return current;
    }
    @Override
    public void load() {
        super.load();
        outlineRegion = Core.atlas.find(name + "-outline");
        stateRegion = Core.atlas.find(name + "-state");
        reloadRegion = Core.atlas.find(name + "-reload");
        for (int i = 0; i < 2; i++) {
            shells[i] = Core.atlas.find(name + "-shell" + i);
            shellOutlines[i] = Core.atlas.find(name + "-shell-outline" + i);
            shellHeats[i] = Core.atlas.find(name + "-shell-heat" + i);
        }
    }

    @Override
    public void setBars() {
        super.setBars();
        bars.add("turretprep", entity -> new Bar("bar.turretprep", Palr.mhemFactionColour, () -> ((BerthaTurretBuild) entity).preparation/reclaimMax));
        bars.add("turretcharge", entity -> new Bar(
                () -> toTurret(entity).chargef() >= 1 ? Core.bundle.get("bar.turretcharged") : Core.bundle.format("bar.turretcharging",
                        ((int)((
                                current.active ? current.charge/60/entity.timeScale() : (maxCharge - (current.chargef() * maxCharge))/60/entity.timeScale()
                                * 100))/100)),
                () -> Tmp.c1.set(Palr.dustriken).lerp(Palr.lightstriken, toTurret(entity).chargef()),
                () -> toTurret(entity).chargef()));
    }

    @Override
    public void init(){
        consumes.powerCond(powerUse, TurretBuild::isActive);
        if(realRange == 0) realRange = range;
        super.init();
    }

    public class BerthaTurretBuild extends PowerTurretBuild {

        //if turret autoactivates
        public boolean auto = false;
        //if it's activated it you can't turn it off, and have to wait till it runs out of charge
        public boolean active = false;

        public float preparation = 0;
        public float charge = 0;

        @Override
        public void updateTile() {

            preparation = Mathf.clamp(preparation + (cons.valid() ? Time.delta * reclaimRate : -0.001f), 0, reclaimMax);

            if(preparation < 1 || !active) {
                if(charge >= maxCharge && auto && target != null) active = true;
                charge = Math.min(charge + chargeRate(), maxCharge);
            }
            else{
                //increase the charge
                if(active) charge = Math.max(charge - chargeRate(), 0);
                if(charge <= 0) active = false;

                applyBoost(preparation, 10);

            }

            super.updateTile();
            //increase reload until a single tick can make it shoot
            if(active && hasAmmo() && !isShooting() && reload >= reloadTime - baseReloadSpeed()) reload = Math.min(reload + baseReloadSpeed(), reloadTime - 1/reloadTime);
            unit.ammo(charge/maxCharge);
        }

        @Override
        public boolean configTapped() {
            if(!active && chargef() < 1 || active) configure(2);
            else configure(1);
            return false;
        }

        @Override
        public void display(Table table) {
            super.display(table);

            table.row();
            table.table(t -> {
                t.left();
                t.image().update(i -> {
                    i.setDrawable(auto ? Icon.eyeSmall : Icon.eyeOffSmall);
                    i.setScaling(Scaling.fit);
                }).size(32).padBottom(-4).padRight(2);
                t.label(() -> "Auto activation:" + (auto ? " on" : " off"));
            }).left();
        }

        @Override
        public boolean hasAmmo() {
            return preparation >= 1;
        }

        public Vec2 shellPos(int side){
            return shellPos.set((xOffset + shellReloadOffset * recoil/recoilAmount) * side, yOffset + -shellRecoil * recoil/recoilAmount).rotate(rotation - 90);
        }

        public float prepf(){
            return Math.min(preparation, 1);
        }

        public float chargef(){
            return charge/maxCharge;
        }

        public float chargeRate(){
            return timeScale() * Time.delta;
        }

        @Override
        public void targetPosition(Posc pos) {
            targetPos.set(pos.x(), pos.y());
        }

        @Override
        protected void bullet(BulletType type, float angle) {
            Bullet b = type.create(this, x, y, angle);
            Tmp.v1.set(targetPos).sub(x, y).clamp(-range, range).add(x, y);
            Tmp.v2.set(Tmp.v1).sub(x, y);
            //the end of the range, accounting for inaccuracy
            Tmp.v3.trns(angle, Tmp.v2.len()).add(x, y);

            Healthc hit = Damage.linecast(b, x + tr.x, y + tr.y, angle, Tmp.v2.len() + shootLength);
            if(hit == null || !hit.isValid()){
                b.set(Tmp.v3);
                b.update();
            }
            else {
                b.set(hit.getX(), hit.getY());
                Tmp.v2.set(hit.x(), hit.y()).sub(x, y);
            }
            ModSounds.whoosh.at(x, y, 1, 5);
            ModSounds.whoosh.at(Tmp.v2.x, Tmp.v2.y, 1, 5);
            for(int i = 0; i < Tmp.v2.len()/effectInterval; i++){
                Vec2 shootPos = new Vec2(Tmp.v2.x, Tmp.v2.y);
                Time.run(Mathf.random() * 5, () -> {
                    Tmp.v3.trns(angle, Mathf.random() * shootPos.len());
                    Fxr.motionBlurBullet.at(x + Tmp.v3.x, y + Tmp.v3.y, Tmp.v3.angle() + 180, type);
                });
            }
        }

        @Override
        public boolean isShooting() {
            return active && super.isShooting();
        }

        @Override
        protected float baseReloadSpeed() {
            return active ? super.baseReloadSpeed() : 0;
        }

        @Override
        public void draw() {
            tr2.trns(rotation, -recoil);

            Draw.rect(baseRegion, x, y);

            Draw.z(Layer.turret - 1f);
            Draw.alpha(prepf());
            Draw.rect(outlineRegion, x + tr2.x, y + tr2.y, rotation - 90);
            for (int i = 0; i < 2; i++) {
                Draw.rect(shellOutlines[i], x + shellPos(i * 2 - 1).x + tr2.x, y + shellPos.y + tr2.y, rotation - 90);
            }

            Draw.z(Layer.turret);
            for (int i = 0; i < 2; i++) {
                Draw.rect(shells[i], x + shellPos(i * 2 - 1).x + tr2.x, y + shellPos.y + tr2.y, rotation - 90);
                if(heat >= 0.01f) {
                    Draw.color(heatColor);
                    Draw.alpha(heat);
                    Draw.rect(shellHeats[i], x + shellPos(i * 2 - 1).x + tr2.x, y + shellPos.y + tr2.y, rotation - 90);
                    Draw.color();
                }
            }
            Draw.alpha(1);
            Draw.rect(region, x + tr2.x, y + tr2.y, rotation - 90);

            //represent charge
            if(!active && charge > maxCharge) {
                Draw.color(Palr.lightstriken);
                Draw.alpha(chargef());
            }
            else {
                Draw.color(Color.white);
                Draw.alpha(reload / reloadTime);
            }
            Draw.rect(reloadRegion, x + tr2.x, y + tr2.y, rotation - 90);

            //Todo: make turret change state, and use state to get a Color from a Color[]. This method is so slow and unreadable
            //if active and auto set colour to a purpleish
            if(active && auto) stateColor.set(Palr.pulseBullet);
            //if it has charge and is active set colour to green
            else if(active) stateColor.set(Palr.paveFactionColour);
            //if it has charge and not active but auto set colour to blue
            else if(auto) stateColor.set(Palr.craeFactionColour);
            //if it desn't have any charge set colour to red
            else if(charge < maxCharge) stateColor.set(Color.coral);
            //if all else fails the turret is considered idle and set colour to yellow
            else stateColor.set(Palr.mhemFactionColour);
            Draw.alpha(chargef());

            Draw.color(stateColor);
            Draw.alpha(preparation);

            Draw.rect(stateRegion, x + tr2.x, y + tr2.y, rotation - 90);
        }


        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            preparation = r.f();
            charge = r.f();
            auto = r.bool();
            active = r.bool();
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(preparation);
            w.f(charge);
            w.bool(auto);
            w.bool(active);
        }
    }
}

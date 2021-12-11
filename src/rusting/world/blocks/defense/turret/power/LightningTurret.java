package rusting.world.blocks.defense.turret.power;

import arc.Core;
import arc.audio.Sound;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.ControlBlock;
import rusting.content.Fxr;
import rusting.graphics.Drawr;
import rusting.interfaces.Targeting;
import rusting.world.blocks.pulse.PulseBlock;

public class LightningTurret extends PulseBlock {

    public boolean targetGround = true, targetAir = true;

    public int lightning = 3;
    public float damage = 3;
    public float healPercent = 0, statusDuration = 160;
    public float shootLength = 5;
    public float shootCone = 55;
    public float reloadTime = 8;
    public float rotationSpeed = 5;

    public Sound shootSound = Sounds.none;
    public float range = 15;
    public float recoilAmount = 2, restitution = 0.1f;

    public StatusEffect status = StatusEffects.shocked;
    public boolean hitUnits = true, hitBuildings = true;
    public Effect healEffect = Fx.heal, hitEffect = Fx.hitLaserBlast, damageEffect = Fxr.chainLightning;
    public Color color = Pal.surge;

    private static boolean anyNearby = false;
    private static Seq<Healthc> all = new Seq<>();

    public TextureRegion baseRegion;

    protected Vec2 rv = new Vec2();

    public interface VisualLightningHolder{
        Vec2 start();

        Vec2 end();
    }

    public LightningTurret(String name) {
        super(name);
    }

    @Override
    public void load() {
        super.load();
        baseRegion = Core.atlas.find("endless-rusting-pulse-base" + size);
    }

    public class LightningTurretBuild extends PulseBlockBuild implements ControlBlock, Targeting {

        public float reload = 0;
        public Vec2 aimPos = new Vec2(x, y);
        public float rotation = 90;

        public Vec2 lightningPos = new Vec2();

        @Nullable
        public BlockUnitc unit;

        @Nullable
        public Posc target;

        public float recoil = 0;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        public boolean isShooting(){
            return unit().isShooting() && isControlled() || target != null;
        }

        @Override
        public boolean isControlled() {
            return unit().controller() instanceof Player;
        }

        @Override
        public void update() {
            super.update();
            lightningPos.trns(rotation, shootLength).add(x, y);
            updateTargeting();

            if(isShooting() && customConsumeValid()) {
                if(isControlled()) aimPos.set(unit().aimX, unit().aimY);
                else if(target != null) aimPos.set(target.getX(), target.getY());

                rotation = Angles.moveToward(rotation, angleTo(aimPos.x, aimPos.y), delta() * rotationSpeed);
                updateShooting();
            }
            recoil = Mathf.lerpDelta(recoil, 0f, restitution);
        }

        protected void updateTargeting(){
            target = Units.closestTarget(team, x, y, range, t -> t.isFlying() ? targetAir : targetGround);
        }

        protected void updateShooting() {
            if(reload >= reloadTime){
                shoot();

                reload = 0f;
            }else{
                reload += delta();
            }
        }

        protected void shoot() {

            Tmp.v1.set(x, y);
            float rx = Tmp.v1.x, ry = Tmp.v1.y;
            anyNearby = false;

            all.clear();

            if (hitUnits) {
                Units.nearbyEnemies(team, rx - range, ry - range, range * 2, range * 2, other -> {
                    if (Tmp.v1.set(x, y).dst(other.x, other.y) <= range &&
                            //find differente between rotation and angle to target, then find if it's within the shoot cone
                            Angles.within(rotation, Tmp.v1.angleTo(other.x, other.y), shootCone) &&
                            (other.isFlying() ? targetAir : targetGround)) {
                        all.add(other);
                    }
                });
            }

            if (hitBuildings && targetGround) {
                Vars.indexer.eachBlock(null, rx, ry, range, b -> b.team != Team.derelict && (b.team != this.team || b.damaged()) && dst(b) <= range && Angles.within(rotation, Tmp.v1.angleTo(b.x, b.y), shootCone), b -> {
                    all.add(b);
                });
            };

            if(all.contains(this)) all.remove(this);
            all.sort(h -> h.dst2(aimPos.x, aimPos.y));

            int len = Math.min(all.size, lightning);
            for (int i = 0; i < len; i++) {
                Healthc other = all.get(i);

                //ignore plast walls

                if (((Teamc) other).team() == team) {
                    if (other.damaged()) {
                        anyNearby = true;
                        other.heal(healPercent / 100f * other.maxHealth());
                        healEffect.at(other);
                        damageEffect.at(rx, ry, 0f, color, other);
                        hitEffect.at(rx, ry, angleTo(other), color);

                        if (other instanceof Building) {
                            Building b = (Building) other;
                            Fx.healBlockFull.at(b.x, b.y, b.block.size, color);
                        }
                    }
                } else {
                    anyNearby = true;
                    other.damage(damage);
                    if (other instanceof Statusc) {
                        Statusc s = (Statusc) other;
                        s.apply(status, statusDuration);
                    }
                    hitEffect.at(other.x(), other.y(), angleTo(other), color);
                    hitEffect.at(rx, ry, angleTo(other), color);
                }

                damageEffect.at(lightningPos.x, lightningPos.y, 0f, color, new VisualLightningHolder(){
                    Vec2 unitPos = new Vec2(other.x(), other.y());
                    @Override
                    public Vec2 start() {
                        return lightningPos;
                    }

                    @Override
                    public Vec2 end() {
                        return unitPos;
                    }
                });
            }

            if(anyNearby) {
                shootSound.at(this);
                recoil = 1;
                customConsume();
            }
        }

        @Override
        public void draw() {

            rv.trns(rotation + 180, recoil * recoilAmount);

            Draw.z(Layer.blockUnder);
            Draw.rect(baseRegion, x, y, 0);

            float x = this.x + rv.x, y = this.y + rv.y;

            Draw.z(Layer.turret);
            Draw.rect(region, x, y, rotation - 90);

            if(chargeRegion != Core.atlas.find("error")) {

                Draw.color(chargeColourStart, chargeColourEnd, chargef());

                boolean highdraw = false;
                if(Core.settings.getBool("settings.er.additivepulsecolours")) Draw.blend(Blending.additive);

                Draw.draw(Layer.turret, () -> {
                    Drawr.drawPulseRegion(chargeRegion, x, y, rotation, Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd, chargef()), chargef(false));
                });

                Draw.alpha(alphaDraw);
                if(!highdraw) Draw.alpha(Draw.getColor().a * Draw.getColor().a);
                Draw.rect(shakeRegion, x + xOffset, y + yOffset, (chargeRegion.width + yOffset)/4, (chargeRegion.height + xOffset)/4, rotation);

                Draw.alpha(chargef());
                Draw.alpha(Draw.getColor().a * Draw.getColor().a);
                Draw.rect(chargeRegion, x, y, rotation);

                if(Core.settings.getBool("settings.er.pulseglare")){
                    Draw.alpha(chargef() * chargef() * 0.5f);
                    Draw.rect(chargeRegion, x, y, chargeRegion.height * 1.5f/4, chargeRegion.width * 1.5f/4, rotation);
                }
            }
            Draw.reset();
        }

        @Override
        public void write(Writes w) {
            super.write(w);
            w.f(reload);
        }

        @Override
        public void read(Reads r, byte revision) {
            super.read(r, revision);
            reload = r.f();
        }
    }
}

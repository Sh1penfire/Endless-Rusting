package rusting.world.blocks.defense.turret.power;

import arc.Core;
import arc.audio.Sound;
import arc.func.Floatp;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.StatusEffect;
import mindustry.world.blocks.ControlBlock;
import rusting.content.Fxr;
import rusting.interfaces.Targeting;
import rusting.world.blocks.pulse.PulseBlock;

public class LightningTurret extends PulseBlock {

    public boolean targetGround = true, targetAir = true;

    public int lightning = 3;
    public float damage = 3;
    public float healAmount = 0, statusDuration = 160;
    public float shootLength = 5;
    public float shootCone = 55;
    public float reloadTime = 8;
    public float rotationSpeed = 5;

    public Sound shootSound = Sounds.none;
    public float range = 15;
    public float recoilAmount = 2, restitution = 0.1f;

    public float arc = 0.25f;
    public float width = 15;

    public StatusEffect status = StatusEffects.shocked;
    public boolean hitUnits = true, hitBuildings = true;
    public Effect healEffect = Fx.heal, hitEffect = Fx.hitLaserBlast, damageEffect = Fxr.chainLightning;
    public Color color = Pal.surge;

    private static boolean anyNearby = false;
    private static Seq<Healthc> all = new Seq<>();
    private static int seed = 0;

    private static Rand rand = new Rand();
    private static float precent = 0;

    public TextureRegion baseRegion, lightRegion;

    public float lightAlpha = 0.45f, lightAlphaBlending = 0.25f, beamLength = 55, beamAlpha = 0.3f, beamWidth = 25;
    protected Vec2 rv = new Vec2();

    public interface VisualLightningHolder{

        LightningTurret owner();

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
        lightRegion = Core.atlas.find(name + "-light");
    }

    public class LightningTurretBuild extends PulseBlockBuild implements ControlBlock, Targeting {

        public float reload = 0;
        public Vec2 aimPos = new Vec2(x, y);
        public float rotation = 90;

        public Vec2 lightningPos = new Vec2();

        public Seq<Posc> lightningPoses = Seq.with();
        public Seq<Posc> replacementLightningPoses = Seq.with();

        @Nullable
        public BlockUnitc unit;

        @Nullable
        public Posc target;

        public float recoil = 0;

        //seed of the arc's rand
        private int arcSeed = 0;

        @Override
        public Unit unit(){
            if(unit == null){
                unit = (BlockUnitc) UnitTypes.block.create(team);
                unit.tile(this);
            }
            return (Unit)unit;
        }

        public boolean isShooting(){
            return unit().isShooting() || !isControlled() && target != null;
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

            rotation = Angles.moveToward(rotation, angleTo(aimPos.x, aimPos.y), delta() * rotationSpeed);
            if(isShooting() && customConsumeValid()) {
                if(isControlled()) aimPos.set(unit().aimX, unit().aimY);
                else if(target != null) aimPos.set(target.getX(), target.getY());
                updateShooting();
            }
            recoil = Mathf.lerpDelta(recoil, 0f, restitution);
        }

        protected void updateTargeting(){
            target = Units.closestTarget(team, x, y, range, t -> t.isFlying() ? targetAir : targetGround);
            if(target == null) target = Vars.indexer.findTile(team, x, y, range, b -> b.damaged() && b != this);
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

            boolean advanced = Core.settings.getBool("advancedeffects");

            arcSeed++;

            lightningPoses.clear();

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
                        other.heal(healAmount);
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

                if(advanced) damageEffect.at(lightningPos.x, lightningPos.y, 0f, color, new VisualLightningHolder(){

                    @Override
                    public LightningTurret owner() {
                        return (LightningTurret) block;
                    }

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

                else lightningPoses.add(other);
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

            Draw.color(lightColor);
            Draw.alpha(lightAlpha * chargef());
            Draw.rect(lightRegion, x, y, rotation - 90);
            Draw.alpha(lightAlphaBlending * chargef());
            Draw.blend(Blending.additive);
            Draw.rect(lightRegion, x, y, rotation - 90);
            Draw.blend();
            Drawf.light(team, x, y, x + Tmp.v1.trns(rotation, beamLength).x, y + Tmp.v1.y, beamWidth, lightColor, beamAlpha * chargef());
            //hell.

            boolean advanced = Core.settings.getBool("advancedeffects");

            if(advanced) return;

            Draw.z(Layer.effect);

            replacementLightningPoses.clear();
            replacementLightningPoses.addAll(lightningPoses).each(pos -> {

                if(dst(pos) > range || pos.isNull() || !pos.isAdded()) {
                    lightningPoses.remove(pos);
                    return;
                }
                seed += 1;
                seed %= 1000;

                //get the start and ends of the lightning, then the distance between them
                float tx = lightningPos.x, ty = lightningPos.y, dst = Mathf.dst(pos.getX(), pos.getY(), tx, ty);

                Tmp.v3.set(pos).sub(lightningPos).nor();
                float normx = Tmp.v3.x, normy = Tmp.v3.y;

                rand.setSeed(seed + (int) Time.time * 100);

                float arcWidth = rand.range(dst * (arc));

                float angle = lightningPos.angleTo(pos);

                Floatp arcX = () -> {
                    return Mathf.sinDeg(precent * 180) * arcWidth;
                };

                int links = Mathf.ceil(dst / width);
                float spacing = dst / links;
                float interval = 0.5f + Mathf.sin(0.5f, 1f);

                Lines.stroke((2 + 0.75f * interval) * (1 - reload/reloadTime));
                Draw.color(Color.white, color, interval);

                //begin the line
                Lines.beginLine();

                Lines.linePoint(lightningPos.x, lightningPos.y);

                //use replacement just in case
                rand.setSeed(arcSeed + replacementLightningPoses.indexOf(pos));

                for(int i = 0; i < links; i++){
                    float nx, ny;
                    if(i == links - 1){
                        //line at end
                        nx = pos.getX();
                        ny = pos.getY();
                    }else{
                        float len = (i + 1) * spacing;
                        rand.setSeed(seed + i);
                        Tmp.v3.setToRandomDirection().scl(width/2);
                        precent = ((float) (i + 1))/links;

                        nx = tx + normx * len + Tmp.v3.x + Tmp.v4.set(0, arcX.get()).rotate(angle).x;
                        ny = ty + normy * len + Tmp.v3.y + Tmp.v4.y;
                    }

                    Lines.linePoint(nx, ny);
                }

                Lines.endLine();
            });
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

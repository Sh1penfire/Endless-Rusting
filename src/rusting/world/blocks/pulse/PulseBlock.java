package rusting.world.blocks.pulse;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.Ranged;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.ui.Cicon;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import rusting.Varsr;
import rusting.content.*;
import rusting.core.holder.CustomConsumerModule;
import rusting.core.holder.CustomStatHolder;
import rusting.ctype.ResearchType;
import rusting.interfaces.*;
import rusting.world.blocks.pulse.utility.PulseResearchBlock;
import rusting.world.modules.PulseModule;
import rusting.world.modules.ResearchModule;

import static mindustry.Vars.*;

public class PulseBlock extends Block implements ResearchableBlock {
    //research types for the block
    public Seq<ResearchType> researchTypes = new Seq<ResearchType>();
    //research module with more specific information
    public ResearchModule researchModule;

    public Seq<ResearchModule> upgrades = Seq.with();

    private boolean tmpBool = false;

    public CustomStatHolder pStats = new CustomStatHolder();

    public float pulseStorage = 10;
    //decreases pulse energy received
    public float resistance = 0.1f;
    //decreases power over time
    public float powerLoss = 0;
    //minimum power required to work
    public float minRequiredPulsePercent = 0;
    //base efficiency
    public float baseEfficiency = 0.5f;
    //how long before the charged region draw x and y changes
    public int timeOffset = 1;
    //the multiplier for the blended region's alpha
    public float visualBlendingAlphaMulti = 0.15f;
    //if it requires overloading to work, enable. Default should be true;
    public boolean requiresOverload = true;
    //Bool for whether block can overload. Unused by normal pulse block.
    public boolean canOverload = true;
    //how much the block can store when overloaded
    public float overloadCapacity = 3;
    //Whether the building can be connected to by PulseNodes with power lasers
    public boolean connectable = true;
    //what the block can shoot when overloaded
    public BulletType projectile = RustingBullets.craeShard;
    //chance modifier for projectile spawning
    public float projectileChanceModifier = 1;
    //offset for projectiles spawned
    public float projectileOffset = 1;
    //how far away the laser is from the block, is used for drawing to and from block,
    public float laserOffset = 3;
    //custom consumer module used purely to store values
    public CustomConsumerModule customConsumes = new CustomConsumerModule();
    //whether crux has infinite resources. PVP excluded
    public boolean cruxInfiniteConsume = false;
    //regions for charge and shake
    public TextureRegion pulseRegion, shakeRegion;
    //colours for charge
    public Color chargeColourStart, chargeColourEnd;

    public PulseBlock(String name){
        super(name);
        update = true;
        solid = true;
        hasPower = false;
        group = BlockGroup.power;
        chargeColourStart = Palr.pulseChargeStart;
        chargeColourEnd = Palr.pulseChargeEnd;
        researchTypes.clear();
        researchTypes.add(RustingResearchTypes.pulse);
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.powerCapacity, pulseStorage);
    }

    @Override
    public void init() {
        super.init();
        setPulseStats();
    }

    public void setPulseStats(){
        Log.info("hi!, my name is " + localizedName());
        pStats.pulseStorage.setValue(pulseStorage);
        pStats.resistance.setValue(resistance);
        pStats.powerLoss.setValue(powerLoss * 60);
        pStats.connectable.setValue(connectable);
        pStats.canOverload.setValue(canOverload);
        pStats.requiresOverload.setValue(requiresOverload);
        pStats.overloadCapacity.setValue(overloadCapacity);
        pStats.minRequiredPercent.setValue(minRequiredPulsePercent * 100);
        pStats.projectileChanceModifier.setValue(projectileChanceModifier);
        if(projectile != null) pStats.projectileRange.setValue(projectileRange()/8);
    }

    @Override
    public CustomStatHolder customStatHolder() {
        return pStats;
    }

    public void load(){
        super.load();
        pulseRegion = Core.atlas.find(name + "-charged", Core.atlas.find(name + "-pulse"));
        shakeRegion = Core.atlas.find(name + "-shake");
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("power", entity -> new Bar(() ->
            Core.bundle.get("bar.pulsebalance"),
            () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                     ((PulseBlockBuild) entity).chargef()),
            () -> Mathf.clamp(((PulseBlockBuild) entity).chargef())
        ));
    }

    @Override
    public String name() {
        return name;
    }

    //used for ui
    @Override
    public String localizedName() {
        return localizedName;
    }

    @Override
    public TextureRegion researchUIcon() {
        return icon(Cicon.medium);
    }

    @Override
    public Seq<ResearchType> researchTypes() {
        return researchTypes;
    }

    @Override
    public ResearchModule getResearchModule() {
        if(researchModule == null) researchModule = new ResearchModule(ItemStack.with(), this);
        return researchModule;
    }

    @Override
    public Seq<ResearchModule> upgrades() {
        return upgrades;
    }

    @Override
    public boolean isHidden(){
        return !Varsr.research.researched(player.team(), this, researchTypes) || super.isHidden();
    }

    @Override
    public boolean hidden() {
        return getResearchModule().isHidden || !unlocked && Vars.state.isCampaign();
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team){
        //must have been researched, but for now checks if research center exists
        if(tile == null || !Varsr.research.researched(player.team(), this, researchTypes)) return false;
        return super.canPlaceOn(tile, team);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);

        if(tile != null) {
            if(canShoot()){
                Lines.stroke(1f);
                Draw.color(Pal.placing);
                Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, projectileRange(), chargeColourEnd);
                Draw.reset();
            }
            if(!canPlaceOn(tile, player.team()) && getResearchModule().needsResearching){
                drawPlaceText(Core.bundle.get(validCenter(player.team()) ? "bar.requitesresearching" : "bar.dosnthavecenter"), x, y, valid);
            }
        }
    }

    //note: only used for display
    public float projectileRange(){
        return (float) (projectile instanceof LightningBulletType ? (projectile.lightningLength * 2 + projectile.lightningLengthRand) * tilesize : projectile.range() * size * 0.6);
    }

    public boolean canShoot(){
        return canOverload;
    }

    public static boolean validCenter(Team team){
        return getCenterTeam(team) != null;
    }

    public static PulseResearchBlock.PulseResearchBuild getCenterTeam(Team team){
        final PulseResearchBlock.PulseResearchBuild[] returnBuilding = {null};
        Groups.build.each(e -> {
            if(e != null && e.team == team && e instanceof PulseResearchBlock.PulseResearchBuild) returnBuilding[0] = (PulseResearchBlock.PulseResearchBuild) e;
        });
        return returnBuilding[0];
    }

    public void drawLaser(float x, float y, float targetX, float targetY, float laserOffset, float targetLaserOffset, float lerpPercent, Color laserCol1, Color laserCol2){
        Draw.z(Layer.power);
        float angle = Mathf.angle(targetX - x, targetY - y) - 90;
        float sourcx = x + Angles.trnsx(angle, 0, laserOffset), sourcy = y + Angles.trnsy(angle, 0, laserOffset);
        float edgex = targetX + Angles.trnsx(angle + 180, 0, targetLaserOffset), edgey = targetY + Angles.trnsy(angle + 180, 0, targetLaserOffset);
        Draw.color(laserCol1, laserCol2, lerpPercent);
        Lines.stroke(1.35f);
        Lines.line(sourcx, sourcy, edgex, edgey);
        Fill.circle(edgex, edgey, 0.85f);
        Fill.circle(sourcx, sourcy, 1.35f);
        Draw.reset();
    }

    public class PulseBlockBuild extends Building implements PulseBlockc, Ranged {

        public float falloff = resistance;
        public float xOffset = 0, yOffset = 0, alphaDraw = 0;
        public float shake = 0;

        public PulseModule pulseModule = new PulseModule();

        @Override
        public PulseModule pulseModule() {
            return pulseModule;
        }

        @Override
        public float range() {
            return projectileRange();
        }

        @Override
        public float pulseEfficiency(){
            return Math.max(baseEfficiency, chargef(false) * timeScale());
        }

        public void customConsume(){
            pulseModule.pulse -= customConsumes.pulse;
        }

        public boolean customConsumeValid(){
            return (pulseModule.pulse >= customConsumes.pulse) || (!state.rules.pvp && (team == Team.derelict || team == state.rules.waveTeam && cruxInfiniteConsume));
        }

        public boolean allConsValid(){
            return customConsumeValid() && ((team == Team.derelict || (team == state.rules.waveTeam && cruxInfiniteConsume)) && !state.rules.pvp || consValid());
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec build){
            return pulse + pulseModule.pulse < pulseStorage + (canOverload ? overloadCapacity : 0);
        }

        public boolean connectableTo(){
            return connectable;
        }

        @Override
        public boolean receivePulse(float pulse, Pulsec source){
            tmpBool = canReceivePulse(pulse, source);
            if(tmpBool) addPulse(pulse, source);
            return tmpBool;
        }

        @Override
        public void addPulse() {
            pulseModule.pulse = pulseStorage + overloadCapacity;
        }

        @Override
        public void addPulse(float pulse){
            addPulse(pulse, null);
        }

        public void addPulse(float pulse, @Nullable Pulsec building){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            float resistAmount = (building != this ? falloff : 0);
            pulseModule.pulse += Math.max(pulse - resistAmount, 0);
            normalizePulse();
        }

        public void normalizePulse(){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            pulseModule.pulse = Mathf.clamp(pulseModule.pulse, 0, storage);
            SpriteBatch s = null;
        }

        public void overloadEffect(){
            //for now, sprays projectiles around itself, and damages itself.
            Tmp.v1.trns(Mathf.random(360), projectileOffset);
            if(!Vars.headless && Mathf.chance(overloadChargef() * projectileChanceModifier)) Call.createBullet(projectile, team, x + Tmp.v1.x, y + Tmp.v1.y, Tmp.v1.angle(), projectile.damage, Mathf.random(0.5f) + 0.3f * size, 1);
        }

        public boolean overloaded(){
            return pulseModule.pulse > pulseStorage && canOverload;
        }

        public float overloadChargef(){
            return (pulseModule.pulse - pulseStorage)/overloadCapacity;
        }

        public float chargef(boolean overloadaccount){
            return pulseModule.pulse/(pulseStorage + (canOverload && overloadaccount ? overloadCapacity : 0));
        }

        public float chargef(){
            return chargef(true);
        }

        @Override
        public float laserOffset() {
            return laserOffset;
        }

        @Override
        public Tile tile() {
            return tile;
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(shake >= timeOffset){
                xOffset = block.size * 0.3f * Mathf.range(2);
                yOffset = block.size * 0.3f * Mathf.range(2);
                alphaDraw = Mathf.absin(Time.time/100, chargef());
            }
            else shake++;
            pulseModule.pulse = Math.max(pulseModule.pulse - powerLoss, 0);
            if(overloaded()) overloadEffect();
        }

        @Override
        public void drawSelect(){
            if(canShoot()){
                Drawf.dashCircle(x, y, projectileRange(), chargeColourStart.lerp(chargeColourEnd, chargef()));
            }
            Draw.reset();
        }

        public void drawLaser(PulseBlockc building, Color laserCol) {
            Draw.z(Layer.power);
            if(!(building instanceof Building)) return;
            Building build = (Building) building;
            float angle = angleTo(build.x, build.y) - 90;
            float sourcx = x + Angles.trnsx(angle, 0, laserOffset), sourcy = y + Angles.trnsy(angle, 0, laserOffset);
            float edgex = build.x + Angles.trnsx(angle + 180, 0, building.laserOffset()), edgey = build.y + Angles.trnsy(angle + 180, 0, building.laserOffset());
            Draw.color(laserCol);
            Lines.stroke(1.35f);
            Lines.line(sourcx, sourcy, edgex, edgey);
            Fill.circle(edgex, edgey, 0.85f);
            Fill.circle(sourcx, sourcy, 1.35f);
            Draw.reset();
        }

        public void draw(){
            super.draw();
            if(pulseRegion != Core.atlas.find("error")) {

                Draw.color(chargeColourStart, chargeColourEnd, chargef());

                Draw.alpha(chargef());
                Draw.rect(pulseRegion, x, y, 270);

                Draw.blend(Blending.additive);

                Draw.alpha(alphaDraw);
                Draw.rect(shakeRegion, x + xOffset, y + yOffset, (pulseRegion.width + yOffset)/4, (pulseRegion.height + xOffset)/4, 270);

                Draw.alpha(chargef() * visualBlendingAlphaMulti);
                Draw.rect(pulseRegion, x, y, 270);

                if(Core.settings.getBool("settings.er.pulseglare")){
                    Draw.alpha(chargef() * chargef() * 0.5f);
                    Draw.rect(pulseRegion, x, y, pulseRegion.height * 1.5f/4, pulseRegion.width * 1.5f/4, 270);
                }
                Draw.blend();
            }
            Draw.reset();
        };

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(pulseModule.pulse);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            pulseModule.pulse = read.f();
        }
    }
}
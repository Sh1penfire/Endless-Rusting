package rusting.world.blocks.pulse;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
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

    public float pulseCapacity = 10;
    //decreases pulse energy received
    public float resistance = 0.1f;
    //decreases power over time
    public float drain = 0;
    //minimum power required to work
    public float minRequiredPulsePercent = 0;
    //base efficiency
    public float baseEfficiency = 0.5f;
    //Max overload efficiency. This is additive!
    public float overloadEfficiency = 1;
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
    //What the block can shoot when overloaded. Optional
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

    public boolean hideTest = false;

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
        this.stats.add(Stat.powerCapacity, pulseCapacity);
    }

    @Override
    public void init() {
        super.init();
        setPulseStats();
    }

    public void setPulseStats(){
        pStats.pulseStorage.setValue(pulseCapacity);
        pStats.resistance.setValue(resistance);
        pStats.powerLoss.setValue(drain * 60);
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
        bars.add("pulse", entity -> new Bar(() ->
                Core.bundle.get("bar.pulse"),
                () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                        ((PulseBlockBuild) entity).chargef(false)),
                () -> Mathf.clamp(((PulseBlockBuild) entity).chargef(false))
        ));
        if(canOverload) bars.add("overload", entity -> new Bar(
                () -> Core.bundle.get("bar.pulseoverload") + ": " + Mathf.floor(((PulseBlockBuild) entity).overloadf() * 10000)/100 + "%",
                () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                        ((PulseBlockBuild) entity).overloadf()),
                () -> Mathf.clamp(((PulseBlockBuild) entity).overloadf())
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
        return !Varsr.research.researched(player.team(), this, researchTypes) || hideTest || super.isHidden();
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
        return canOverload && projectile != null;
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


    public class PulseBlockBuild extends Building implements PulseBlockc, Ranged{
        public PulseModule storage = new PulseModule();
        public PulseModule overload = new PulseModule();

        public float xOffset = 0, yOffset = 0, alphaDraw = 0;
        public float shake = 0;

        public float pEfficiency(){
            return baseEfficiency + (1 - baseEfficiency) * chargef() + overloadEfficiency * overloadf();
        }
        public float pDelta(){
            return pEfficiency() * edelta();
        }

        @Override
        public boolean pConsValid() {
            return storage.pulse >= customConsumes.pulse;
        }

        @Override
        public boolean allConsValid() {
            return cons.valid() && pConsValid();
        }

        @Override
        public float range() {
            return projectileRange();
        }

        @Override
        public boolean overloaded() {
            return overloadf() >= 0.2f;
        }

        @Override
        public void update() {
            super.update();
            if(canOverload) overload.pulse -= drain * overloadf() * overloadf();
            normalizePulse();
        }

        @Override
        public boolean canReceivePulse(float pulse, Pulsec source) {
            return chargef(true) < 1;
        }

        @Override
        public boolean connectableTo() {
            return connectable;
        }

        @Override
        public void addPulse() {
            storage.pulse = pulseCapacity;
        }

        @Override
        public float addPulse(float pulse) {
            return addPulse(pulse, this);
        }

        @Override
        public float addPulse(float pulse, Pulsec source) {
            float before = totalPulse();
            overload.pulse += Mathf.clamp((storage.pulse += pulse) - pulseCapacity, 0, overloadCapacity);
            normalizePulse();
            return totalPulse() - before;
        }

        public float totalPulse(){
            return storage.pulse + overload.pulse;
        }

        @Override
        public float removePulse(float pulse) {
            return removePulse(pulse, this);
        }

        @Override
        public float removePulse(float pulse, Pulsec source) {
            float before = totalPulse();
            storage.pulse -= pulse;
            normalizePulse();
            return before - totalPulse();
        }

        @Override
        public void normalizePulse() {
            storage.pulse = Mathf.clamp(storage.pulse, 0, pulseCapacity);
            overload.pulse = Mathf.clamp(overload.pulse, 0, overloadCapacity);
        }

        @Override
        public void normalizeOverload() {
            overload.pulse = Mathf.clamp(overload.pulse, 0, pulseCapacity);
        }

        @Override
        public float chargef() {
            return chargef(false);
        }

        @Override
        public float chargef(boolean overloadaccount) {
            return overloadaccount && canOverload ? (storage.pulse + overload.pulse)/(pulseCapacity + overloadCapacity) : storage.pulse/pulseCapacity;
        }

        @Override
        public float overloadf(){
            return overload.pulse/overloadCapacity;
        }

        @Override
        public PulseModule pulseModule() {
            return storage;
        }

        @Override
        public PulseModule overloadModule() {
            return overload;
        }

        @Override
        public float laserOffset() {
            return laserOffset;
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

        /*
        @Override
        public byte version() {
            return 1;
        }

         */

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(storage.pulse);
            w.f(overload.pulse);
        }

        @Override
        public void read(Reads r, byte revision){
            super.read(r, revision);
            storage.pulse = r.f();
            overload.pulse = r.f();
        }
    }
}
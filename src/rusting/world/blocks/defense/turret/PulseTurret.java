package rusting.world.blocks.defense.turret;

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
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.Ranged;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.ui.Cicon;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.ReloadTurret;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Stat;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.core.holder.CustomConsumerModule;
import rusting.core.holder.CustomStatHolder;
import rusting.ctype.ResearchType;
import rusting.graphics.Drawr;
import rusting.interfaces.*;
import rusting.world.modules.PulseModule;
import rusting.world.modules.ResearchModule;

import static mindustry.Vars.player;
import static mindustry.Vars.state;

public class PulseTurret extends ReloadTurret implements ResearchableBlock {
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
    //base efficiency
    public float baseEfficiency = 0.5f;
    //how long before the charged region draw x and y changes
    public int timeOffset = 1;
    //Bool for whether block can overload. Unused by normal pulse block.
    public boolean canOverload = true;
    //how much the block can store when overloaded
    public float overloadCapacity = 3;
    //Whether the building can be connected to by PulseNodes with power lasers
    public boolean connectable = true;
    //how far away the laser is from the block, is used for drawing to and from block,
    public float laserOffset = 3;
    //custom consumer module used purely to store values
    public CustomConsumerModule customConsumes = new CustomConsumerModule();
    //whether crux has infinite resources. PVP excluded
    public boolean cruxInfiniteConsume = false;
    //regions for charge and shake
    public TextureRegion chargeRegion, shakeRegion;
    //colours for charge
    public Color chargeColourStart, chargeColourEnd;

    public PulseTurret(String name){
        super(name);
        hasPower = false;
        group = BlockGroup.power;
        chargeColourStart = Palr.pulseChargeStart;
        chargeColourEnd = Palr.pulseChargeEnd;
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.powerCapacity, pulseStorage);
    }

    @Override
    public CustomStatHolder customStatHolder() {
        return pStats;
    }

    public void load(){
        super.load();
        chargeRegion = Core.atlas.find(name + "-charged");
        shakeRegion = Core.atlas.find(name + "-shake");
    }

    @Override
    public void setBars(){
        super.setBars();
        bars.add("power", entity -> new Bar(() ->
                Core.bundle.get("bar.pulsebalance"),
                () -> Tmp.c1.set(chargeColourStart).lerp(chargeColourEnd,
                        ((Pulsec) entity).chargef()),
                () -> Mathf.clamp(((Pulsec) entity).chargef())
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
    public boolean canPlaceOn(Tile tile, Team team){
        //must have been researched, but for now checks if research center exists
        if(tile == null || !Varsr.research.researched(player.team(), this, researchTypes)) return false;
        return super.canPlaceOn(tile, team);
    }

    //note: only used for display
    public float projectileRange(){
        return range;
    }

    public boolean canShoot(){
        return true;
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

    public class PulseTurretBuild extends ReloadTurretBuild implements PulseBlockc, Ranged {

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
            return range;
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

        public void removePulse(float pulse){
            removePulse(pulse, null);
        }

        public void removePulse(float pulse, @Nullable Building building){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            pulseModule.pulse -= pulse;
            normalizePulse();
        }

        public void normalizePulse(){
            float storage = pulseStorage + (canOverload ? overloadCapacity : 0);
            pulseModule.pulse = Math.max(Math.min(pulseModule.pulse, storage), 0);
        }

        public void overloadEffect(){
            //placeholder function
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

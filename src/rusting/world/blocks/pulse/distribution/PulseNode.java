package rusting.world.blocks.pulse.distribution;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.*;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.graphics.Drawr;
import rusting.interfaces.PulseBlockc;
import rusting.interfaces.ResearchableBlock;
import rusting.world.blocks.pulse.PulseBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//a block which can connect to other pulse blocks and transmit a pulse
public class PulseNode extends PulseBlock implements ResearchableBlock {
    private static PulseNodeBuild lastNode = null;

    //Reload of the node till it can transmit a pulse to a nearby block
    public float pulseReloadTime = 60;
    //How many bursts the node sends
    public float pulseBursts = 1;
    //Spacing between bursts
    public float pulseBurstSpacing = 0;
    //How much energy is transmitted
    public float energyTransmission = 3;
    //How many nodes you can connect to
    public int connectionsPotential = 1;
    //Range of the node
    public float laserRange = 15;
    //Colour of the laser
    public Color laserColor = Palr.pulseLaser;
    //speed that the node opens and closes
    public float closeSpeed = 0.01f;
    //rely on closed caps or not
    public boolean close = true;

    //used as a placeholder to avoid unnecessary variable creation
    protected static BuildPlan otherReq;

    private static int tmpInteger = 0;
    private static float tmpFloat = 0;

    public TextureRegion closedRegion, shineRegion, topRegion;

    public PulseNode(String name) {
        super(name);
        schematicPriority = -10;
        configurable = true;
        hasPower = false;
        consumesPower = false;
        outputsPower = false;
        canOverdrive = false;
        swapDiagonalPlacement = true;
        drawDisabled = false;


        config(Integer.class, (PulseNodeBuild entity, Integer i) -> {
            Tile t = world.tile(i);
            //must have been added via schem, add to previous connections
            if(t.build == null) {
                entity.previousConnections.add(i);
                return;
            }
            Building other = t.build;
            if(!(other instanceof PulseBlockc)) return;
            if(Varsr.world.onTile(other, entity.connections)){
                //unlink
                tmpInteger = i;
                tmpFloat = world.build(i).dst(entity);

                //find closest integer in the Seq and remove it
                entity.connections.each(integer -> {
                    float dst = tmpFloat;
                    float tmpdst = world.build(integer).dst(other);
                    if(tmpdst < dst){
                        tmpInteger = integer;
                        tmpFloat = tmpdst;
                    }
                });

                //get the index of the int, else the game will think it's removing an integer with the index of the current integer
                entity.connections.remove(entity.connections.indexOf(tmpInteger));
            }

            //connect to the block if posible
            else if(nodeCanConnect(entity, other)){
                entity.connections.add(i);
            }
            //catch any errors with the main blocks of code
            else entity.previousConnections.add(i);
        });

        config(Point2[].class, (PulseNodeBuild entity, Point2[] points) -> {

            for (Point2 point: points){
                if(point != null){
                    entity.configure(Point2.pack(point.x + entity.tile.x, point.y + entity.tile.y));
                }
            }
        });
    }

    @Override
    public void load() {
        super.load();
        super.load();
        closedRegion = Core.atlas.find(name + "-closed", region);
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
    }

    @Override
    public void setStats(){
        super.setStats();
        this.stats.add(Stat.range, laserRange);
        this.stats.add(Stat.reload, pulseReloadTime /60);
    }

    @Override
    public void setPulseStats() {
        super.setPulseStats();
        pStats.connections.setValue(connectionsPotential);
        pStats.pulseReloadTime.setValue(60/pulseReloadTime);
        pStats.energyTransmission.setValue(energyTransmission);
        pStats.pulseBursts.setValue(pulseBursts);
        pStats.pulseBurstSpacing.setValue(pulseBurstSpacing);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);

        if(tile != null && laserRange > 0 && connectionsPotential > 0) {
            Lines.stroke(1f);
            Draw.color(Pal.placing);
            Drawf.circles(x * tilesize + offset, y * tilesize + offset, laserRange * tilesize);
        }
    }

    @Override
    public void drawPlanConfigTop(BuildPlan req, Eachable<BuildPlan> list){
        if(req.config instanceof Point2[]){
            Point2[] ps = (Point2[]) req.config;
            for(Point2 point : ps){
                if(point != null) {
                    int px = req.x + point.x, py = req.y + point.y;
                    otherReq = null;
                    list.each(other -> {
                        if (other.x == px && other.y == py && other.block instanceof PulseBlock) otherReq = other;
                    });

                    if (!(otherReq == null || otherReq.block == null) && req.block instanceof PulseNode)
                        drawLaser(req.drawx(), req.drawy(), otherReq.drawx(), otherReq.drawy(), laserOffset, otherReq.block instanceof PulseBlock ? ((PulseBlock) otherReq.block).laserOffset : otherReq.block.size - 5, 0.5f, chargeColourStart, chargeColourEnd);
                }
            }
            Draw.color();
        }
    }

    public static boolean nodeCanConnect(PulseNodeBuild build, Building target){
        return ((target instanceof PulseBlockc) && ((PulseBlockc) target).connectableTo()) && !build.connections.contains(target.pos()) && build.connections.size < ((PulseNode) (build.block)).connectionsPotential && ((PulseNode) build.block).laserRange * 8 >= Mathf.dst(build.x, build.y, target.x, target.y);
    }

    public void getPotentialLinks(Tile tile, Team team, Cons<PulseNodeBuild> cons){
        Boolf<Building> valid = other -> other != null && other.tile() != tile && other instanceof PulseNodeBuild;
        Groups.build.each(b -> {
            if(valid.get(b)) cons.get((PulseNodeBuild) b);
        });
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

    public class PulseNodeBuild extends PulseBlockBuild{
        public Seq<Integer> connections = new Seq();
        public Seq<Integer> previousConnections = new Seq();
        public float reload = 0;

        public float closed = 0;

        public void dropped(){
            connections.clear();
        }

        @Override
        public void placed(){
            if(Vars.net.client()) return;

            getPotentialLinks(tile, team, other -> {
                if(other.connections.size == 0 && nodeCanConnect(other, this)){
                    other.configureAny(pos());
                }
            });

            super.placed();
        }


        @Override
        public boolean onConfigureBuildTapped(Building other){

            if(this == other){
                deselect();
                return false;
            }

            if(nodeCanConnect(this, other) || Varsr.world.onTile(other, connections)){
                configure(other.pos());

                return false;
            }


            return super.onConfigureBuildTapped(other);
        }

        @Override
        public void updateTile() {
            storage.pulse = Math.max(storage.pulse - drain * (1 - closed), 0);
            if(overloaded()) overloadEffect();
            //if theres more than one connection, lerp to one. Otherwise, lerp to 0. All this extra stuf is nescecary because no devide by 0
            //*sigh* I tried to use bitwise opperators for this but my brain died halfway through
            if(connections.size == 0){
                closed = Math.min(closed + Time.delta * closeSpeed, 1);
            }
            else closed = Math.max(closed - Time.delta * closeSpeed, 0);

            connections.each(l -> {
                Building j = world.build(l);
                if(j == null || j.isNull() || !j.isAdded()) {
                    previousConnections.add(l);
                    connections.remove(l);
                }
            });

            previousConnections.each(l -> {
                Building j = world.build(l);
                if(j != null && j.isAdded() && nodeCanConnect(this, j)) {
                    connections.add(l);
                    previousConnections.remove(l);
                }
            });

            if(connections.size >= connectionsPotential) previousConnections.clear();

            if(closed > 0.15f && close) return;
            if(reload >= pulseReloadTime && chargef(true) >= minRequiredPulsePercent) {
                interactConnected();
                reload = 0;
            }
            else reload += pEfficiency() * Time.delta;
        }

        public void interactConnected(){
            addPulseConnected();
        }

        public void addPulseConnected(){
            final int[] index = {0};
            connections.each(l -> {
                Building j = world.build(l);
                //need to double check, jic, because I've experienced crashes while a generator was pumping out energy
                if(!(j instanceof PulseBlockc)){
                    connections.remove(connections.indexOf(l));
                    return;
                }
                if(chargef() <= 0 || j == null) return;
                if(index[0] > connectionsPotential) connections.remove(l);
                float energyTransmitted = Math.min(storage.pulse, energyTransmission);
                PulseBlockc pBlock = (PulseBlockc) j;
                if(pBlock.canReceivePulse(energyTransmitted, this)) removePulse(pBlock.addPulse(energyTransmitted));
                index[0]++;
            });
        }

        @Override
        public void drawConfigure(){

            float scl = Mathf.absin(Time.time, 4f, 1f);

            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + scl);
            Drawf.circles(x, y, laserRange * tilesize);

            previousConnections.each(l -> {
                Tmp.v1.set(Point2.x(l) * 8, Point2.y(l) * 8);
                Drawf.dashCircle(Tmp.v1.x, Tmp.v1.y, 2f + 1f + scl, Tmp.c1.set(Palr.dustriken).a(scl/8 + 0.5f));
            });

            connections.each(l -> {
                Building link = world.build(l);
                //prevent crashes that can happen when config is being drawn and a block is destroyed which the node is linekd up to
                if(link == null) return;
                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f + scl, Pal.place);
            });

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

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);
            Draw.z(Layer.power + 0.1f);
            connections.each(l -> {
                Building other = world.build(l);
                if(other == null || other.isNull() || !other.isAdded() || !(other instanceof PulseBlockc)) return;
                //draw with less alpha if node is opening
                drawLaser((PulseBlockc) other, Tmp.c1.set(laserColor).a(1 - closed * closed));
            });

            Draw.z(Layer.blockOver - 1);
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
                Draw.color();
            }

            Draw.z(Layer.blockOver);
            Drawr.drawShine(shineRegion, x, y, 0, 0.25f);
            Draw.alpha(closed);
            Draw.rect(topRegion, x, y, 0);
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[connections.size + previousConnections.size];
            for(int i = 0; i < connections.size; i++){
                out[i] = Point2.unpack(connections.get(i)).sub(tile.x, tile.y);
            }
            for(int i = 0; i < previousConnections.size; i++){
                out[i] = Point2.unpack(previousConnections.get(i)).sub(tile.x, tile.y);
            }
            return out;
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(reload);
            w.s(connections.size);
            for(int i = 0;  i < connections.size; i++){
                Log.info(connections.size);
                w.d(connections.get(i));
            }
            w.s(previousConnections.size);
            for (int i = 0; i < previousConnections.size; i++) {
                w.d(previousConnections.get(i));
            }
            w.f(closed);
        }

        @Override
        public void read(Reads r, byte revision){
            super.read(r, revision);
            reload = r.f();
            int n = r.s();
            connections.clear();
            int rpos;
            for(int i = 0; i < n; i++){
                rpos = ((int) r.d());
                connections.add(rpos);
            }
            previousConnections = new Seq<Integer>();
            previousConnections.clear();
            n = r.s();
            for(int i = 0; i < n; i++){
                rpos = ((int) r.d());
                previousConnections.add(rpos);
            }
            closed = r.f();
        }
    }
}
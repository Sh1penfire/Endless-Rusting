package rusting.world.blocks.pulse.utility;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import rusting.content.Fxr;
import rusting.content.RustingAchievements;
import rusting.interfaces.PrimitiveControlBlock;
import rusting.world.blocks.pulse.distribution.PulseCanal.PulseCanalBuild;
import rusting.world.blocks.pulse.distribution.PulseFlowSplitter.PulseFlowSplitterBuild;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class PulseTeleporterController extends PulseTeleporterPart {

    //range of the teleporter
    float teleporterRange = 56;
    //How many nodes you can connect to
    public int connectionsPotential = 1;

    public PulseTeleporterController(String name) {
        super(name);
        configurable = true;
        config(Integer.class, (PulseTeleporterControllerBuild entity, Integer i) -> {
            Building other = world.build(i);
            if(!(other instanceof PulseBlockBuild)) return;
            if(entity.connections.contains(i)){
                //unlink
                entity.connections.remove(i);
            }
            else if(other instanceof PrimitiveControlBlock){
                entity.connections.add(i);
            }
        });
    }

    private PulseTeleporterControllerBuild build;
    private Building tmpBuild, tmpBuild2;
    boolean returnBool = false, cornerFound = false;
    Tile tmpTile = null, tmpTile2 = null;

    private PulseTeleporterControllerBuild asControllerBuild(Building build){
        return (PulseTeleporterControllerBuild) build;
    }

    public static boolean teleCanConnect(PulseTeleporterControllerBuild build, PulseTeleporterControllerBuild target){
        return (target instanceof PulseTeleporterControllerBuild && !build.connections.contains(target.pos()) && build.connections.size < ((PulseTeleporterController) (build.block)).connectionsPotential && ((PulseTeleporterController) build.block).teleporterRange * 8 >= Mathf.dst(build.x, build.y, target.x, target.y));
    }

    //function findMulti(){let build = Vars.world.buildWorld(Core.input.mouseWorld().x, Core.input.mouseWorld().y); return build.block.multiblockFormed(build.tile)}

    public boolean setupMultiblock(Tile tile) {
        returnBool = false;
        if(!(tile.build instanceof PulseTeleporterControllerBuild)) return false;
        build = asControllerBuild(tile.build);
        build.proximity().each(b -> {
            if(returnBool == true) {
                Log.info("breaking out of loop");
                return;
            }
            Log.info("Building is instance of canal: " + (b instanceof PulseCanalBuild));
            Log.info("build is facing: " + (b.rotation * 90) % 360);
            Log.info("angle to block is: " + build.angleTo(b.x, b.y));
            Log.info("build's relative position with ofset is: " + b.tile.x + " " + b.tile.y + " " + (tile.x - b.tile.x) + " " + (tile.y - b.tile.y));
            if(b instanceof PulseCanalBuild && (b.rotation * 90) % 360 == build.angleTo(b.x, b.y)) {
                Fxr.corrodedEffect.at(b.x, b.y);
                returnBool = true;
                tmpBuild2 = b;
            }
        });
        if(returnBool == false) return returnBool;
        Log.info("found canal");
        returnBool = false;
        for (int i = 0; i < 12; i++) {
            if (tmpTile == tile && i > 0) {
                returnBool = true;
                break;
            }
            Log.info("cycle " + (i + 1));
            if(tmpBuild2 instanceof PulseCanalBuild) {
                Fxr.corrodedEffect.at(tmpBuild2.x, tmpBuild2.y);
                Log.info(tmpBuild2 + " instance of PulseCanalBuild");
                tmpTile = rusting.world.blocks.pulse.distribution.PulseCanal.asCanal(tmpBuild2).canalEnding;
                Log.info("ending being at  x:" + tmpTile.x + " y:" + tmpTile.y);
                if(Mathf.dst(tmpBuild2.tile.x, tmpBuild2.tile.y, tmpTile.x, tmpTile.y) < 8) break;
                else if(tmpTile.build != null && tmpTile.build instanceof PulseFlowSplitterBuild){
                    tmpBuild = tmpTile.build;
                    Tmp.v1.trns(tmpBuild.rotation * 90, 8);
                    Log.info("the corner's position is:" + "x: " + tmpTile.x + " y: " + tmpTile.y);
                    tmpBuild2 = world.buildWorld(tmpBuild.x + Tmp.v1.x, tmpBuild.y + Tmp.v1.y);
                    tmpTile2 = world.tileWorld(tmpBuild.x + Tmp.v1.x, tmpBuild.y + Tmp.v1.y);
                    Log.info("the corner's endling position is:" + "x: " + tmpTile2.x + " y: " + tmpTile2.y);

                    Fxr.blackened.at(tmpTile2.x * 8, tmpTile2.y * 8);
                }
            }
        }
        return returnBool;
    }

    public class PulseTeleporterControllerBuild extends PulseBlockBuild {

        /*
        0: idle
        1: teleporting unknown blocks and units in
        2: teleporting blocks and units from  the unknown
         */
        public int state = 0;
        boolean multiblockFormed = false;
        Seq<Integer> connections = new Seq();
        Seq<Building> multiblockParts = new Seq();
        float progress = 0;
        //set whenever a schematic is being teleported in or untis and blocks (If posible) are being teleported
        int toProgress = 100;
        private Point2 point2;
        private float tmpDst = 0;
        private int index = 0;

        @Override
        public Object config() {
            return super.config();
        }

        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
        }

        @Override
        public void drawConfigure(){

            float scl = Mathf.absin(Time.time, 4f, 1f);

            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + scl);
            Drawf.circles(x, y, (float) (teleporterRange * tilesize));

            connections.each(l -> {
                Building link = world.build(l);
                //prevent crashes that can happen when config is being drawn and a block is destroyed which the node is linekd up to
                if(link == null) return;
                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f + scl, Pal.place);
            });

            Draw.reset();
        }

        @Override
        public void updateTile() {
            super.updateTile();
            if(chargef() > 0.9f && !multiblockFormed) multiblockFormed = setupMultiblock(tile);
            if(multiblockFormed && Vars.state.isCampaign()) {
                Core.settings.put("settings.er.teleporterbuilt", true);
                RustingAchievements.pulseTeleporterConstructed.unlock();
            }
        }

        public void teleportUnits(){
            if(connections.size == 0) return;
            tmpDst = range();
            //search for nearest teleporter
            connections.each(i -> {
                Tmp.v1.set(Point2.unpack(i).x, Point2.unpack(i).y);
                if(Tmp.v1.len() < tmpDst) index = connections.indexOf(i);
            });
            point2 = Point2.unpack(connections.get(index));
            tmpBuild = Vars.world.buildWorld(point2.x, point2.y);
            if(tmpBuild instanceof PulseTeleporterControllerBuild){

            }
            else{
                connections.remove(index);
                return;
            }
        }
    }
}

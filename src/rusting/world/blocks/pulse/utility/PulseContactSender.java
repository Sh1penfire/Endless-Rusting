package rusting.world.blocks.pulse.utility;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import rusting.Varsr;
import rusting.interfaces.PrimitiveControlBlock;
import rusting.world.blocks.pulse.distribution.PulseNode;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

//receives information from adjacent blocks, then sends it to connected blocks. Averages out multiple sources of information.
public class PulseContactSender extends PulseControlModule{

    //how many instructions are stored.
    public int dataStorageSize = 10;
    //ticks between reading information
    public float readReloadTime = 1;
    //ticks between writing information
    public float writeReloadTime = 1;
    //Range of the node
    public double laserRange = 15;
    //Colour of the laser
    public Color laserColor = chargeColourStart;
    //how many blocks you can connect to
    public int connectionsPotential = 1;

    public PulseContactSender(String name) {
        super(name);
        configurable = true;
        config(Integer.class, (PulseContactReceiverBuild entity, Integer i) -> {
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

    public static boolean senderCanConnect(PulseContactReceiverBuild build, Building target){
        return target instanceof PrimitiveControlBlock && !build.connections.contains(target.pos()) && build.connections.size < ((PulseContactSender) (build.block)).connectionsPotential && ((PulseNode) build.block).laserRange * 8 >= Mathf.dst(build.x, build.y, target.x, target.y);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);

        if(tile != null && laserRange > 0) {
            Lines.stroke(1f);
            Draw.color(Pal.placing);
            Drawf.circles(x * tilesize + offset, y * tilesize + offset, (float) (laserRange * tilesize));
        }
    }

    public class PulseContactReceiverBuild extends PulseControlModuleBuild{

        float readCooldown = 1, writeCooldown = 1;

        public Seq<Integer> connections = new Seq();

        Seq<PrimitiveControlBlock> adjacent = new Seq();

        Seq<Double>[] informationStore = new Seq[]{new Seq(), new Seq(), new Seq(), new Seq()};

        @Override
        public boolean onConfigureTileTapped(Building other){

            if(this == other){
                deselect();
                return false;
            }

            if(other instanceof PrimitiveControlBlock || connections.contains(other.pos())){
                configure(other.pos());

                return false;
            }


            return super.onConfigureTileTapped(other);
        }

        public void updateProx(){
            adjacent.clear();
            proximity().each(l -> {
                if(l instanceof PrimitiveControlBlock) adjacent.add((PrimitiveControlBlock) l);
            });
        }

        @Override
        public void drawConfigure(){

            Drawf.circles(x, y, tile.block().size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
            Drawf.circles(x, y, (float) (laserRange * tilesize));


            connections.each(l -> {
                Building link = world.build(l);
                Drawf.square(link.x, link.y, link.block.size * tilesize / 2f + 1f, Pal.place);
            });

            Draw.reset();
        }

        @Override
        public void draw() {
            super.draw();
            connections.each(l -> {
                Building other = world.build(l);
                if(other == null || other.isNull() || !other.isAdded()) return;
                drawLaser((PulseBlockBuild) other, laserColor);
            });
        }

        @Override
        public void update() {
            super.update();
            if(readCooldown <= 0) readData();
            else readCooldown -= 1/readReloadTime;
            if(writeCooldown <= 0) writeData();
            else writeCooldown -= 1/writeReloadTime;
        }

        public void readData(){
            readCooldown = 1;
            updateProx();
            adjacent.each(l -> {
                l.exportInformationDefault(this);
            });
        }

        public void writeData(){
            writeCooldown = 1;
            if(informationStore[0].size > 0) {
                connections.each(p -> {
                    Building l = world.build(p);
                    if(l instanceof PrimitiveControlBlock) {
                        PrimitiveControlBlock b = (PrimitiveControlBlock) l;
                        //all Seqs should be the same size as the Seq in informationStore with index 0. If not, this will result in an error
                        double[] translate = {informationStore[0].get(0).doubleValue(), informationStore[1].get(0).doubleValue(), informationStore[2].get(0).doubleValue(), informationStore[3].get(0).doubleValue()};
                        translate = Varsr.formats.getByName("rotational").convert("directional", translate);
                        b.rawControl(translate[0], translate[1], translate[2], translate[3]);
                    }
                });
                for (Seq<Double> doubles : informationStore) {
                    for (int i = 0; i + 1 < doubles.size; i++) {
                        doubles.set(i, doubles.get(i + 1));
                    }
                    doubles.remove(doubles.size - 1);
                }
            }
        }

        @Override
        public void rawControl(double p1, double p2, double p3, double p4) {
            informationStore[0].add(p1);
            informationStore[1].add(p2);
            informationStore[2].add(p3);
            informationStore[3].add(p4);
            for (Seq<Double> doubles : informationStore) {
                if(doubles.size > dataStorageSize){
                    for (int i = 0; i + 1 < doubles.size; i++) {
                        doubles.set(i, doubles.get(i + 1));
                    }
                    doubles.remove(dataStorageSize);
                }
            }
        }

        @Override
        public void write(Writes w){
            super.write(w);
            w.f(readCooldown);
            w.s(connections.size);
            for(int i = 0;  i < connections.size; i++){
                w.d(connections.get(i));
            }
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);
            readCooldown = read.f();
            int n = read.s();
            connections = new Seq<Integer>();
            connections.clear();
            int rpos;
            for(int i = 0;  i < n; i++){
                rpos = ((int) read.d());
                connections.add(rpos);
            }
        }
    }

}

package rusting.world.blocks.pulse.crafting;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import rusting.content.Fxr;
import rusting.graphics.Drawr;

import static mindustry.Vars.tilesize;

//TODO: this class has three methods which could possibly be significantly shortened. Copy off BeamDrill.java
public class PulseForgery extends PulseGenericCrafter{

    public float fanSpeed = 3.5f;

    //The time in ticks that it takes to transition from one state to another. Boostable by overload.
    public float meltTime = 130;
    //The time in ticks that a mixture takes to reach a molten state. Purely visual.
    public float moltenTime = 90;
    //Cooling time affected by how much space the vents have to circulate air. Opens up the top invisible glass cover, and turns the fan on.
    public float coolingTime = 160;

    public int ventRange = 5;
    public float tileBoost = 0.15f;

    public Color meltColorStart = Pal.darkFlame, meltColorEnd = Pal.lightPyraFlame;
    public TextureRegion fanRegion, ventRegion, solidRegion, shineRegion, topRegion, ventlessRegion;

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(name, Core.atlas.find(name + "-base"));
        fanRegion = Core.atlas.find(name + "-fan", region);
        ventRegion = Core.atlas.find(name + "-vent", Core.atlas.find("clear"));
        solidRegion = Core.atlas.find(name + "-solid", Core.atlas.find("clear"));
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
        ventlessRegion = Core.atlas.find(name + "-ventless");
    }

    public PulseForgery(String name) {
        super(name);
        rotate = true;
    }

    @Override
    public void init() {
        super.init();
        craftTime = meltTime + coolingTime;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.productionTime, "(" + meltTime/60 + " + " + coolingTime/60 + ")");
    }

    private static float[] points;

    //to put into perspective how this functions, the forgery itterates from bottom to top, and left to right;

    /*
     x
     [][]
    x[][]x
     x
     */

    //x here being the starting position of all four rotations.
    //rotation starts from 0 at the right side, and goes counterclockwise

    //if the block is not horizontal, the x offset from the bottom left tile is *always* size - 1

    //if it is facing to the left, the offset will be a block behind the tile
    //if it is facing to the right, the offset is the size

    //rotate this counterclockwise to get the y cord pairs

    public int getOffsetX(float rotation){
        return (rotation % 2) == 0 ? (rotation == 0 ? size : -1) : 0;
    }

    public int getOffsetY(float rotation){
        return ((rotation + 1) % 2) == 0 ? (rotation == 1 ? size : -1) : 0;
    }

    //offset from the tile that we place on for both x and y
    public int bottomOffset(){
        return (int) ((size % 2) == 0 ? -size/2 + 1: -size/2 + 0.5f);
    }

    public Point2 tileEdge(int rotation){
        return new Point2(getOffsetX(rotation) + bottomOffset(), getOffsetY(rotation) + bottomOffset());
    }

    public static Point2[] blockOffsets = new Point2[]{
            new Point2(0, -4),
            new Point2(-4, 0),
            new Point2(0, -4),
            new Point2(-4, 0)};

    public static Point2[] tileOffsets = new Point2[]{
            new Point2(-4, -4), new Point2(-4, 4),
            new Point2(-4, -4) ,new Point2(4, -4),
            new Point2(4, -4), new Point2(4, 4),
            new Point2(-4, 4), new Point2(4, 4)
    };

    @Override
    public void drawRequestRegion(BuildPlan req, Eachable<BuildPlan> list) {
        float x = req.getX(), y = req.getY(), rotation = req.rotation;
        Draw.rect(ventlessRegion, x, y);
        Draw.rect(ventRegion, x, y, rotation * 90);
    }

    public float calculateEfficiency(int x, int y, float rotation){
        //itterate through the rows
        int offsetX = 0, offsetY = 0;
        float effTile = 0;

        //check for if facing horazontaly
        boolean horizontal = (rotation % 2) == 0;

        int tileEdgex = getOffsetX(rotation) + bottomOffset();
        int tileEdgey = getOffsetY(rotation) + bottomOffset();
        //itterate through all posible rows/collums
        for (int j = 0; j < size; j++) {
            //itterate through all tiles in a line
            for(int i = 0; i < ventRange + 1; i++){
                if(horizontal){
                    offsetY = j;
                    offsetX = i;
                    if(rotation == 2) offsetX *= -1;
                }
                else {
                    offsetX = j;
                    offsetY = i;
                    if(rotation == 3) offsetY *= -1;
                }

                int tileX = x + tileEdgex + offsetX, tileY = y + tileEdgey + offsetY;

                Tile tile = Vars.world.tiles.get(tileX, tileY);
                if(tile == null || tile.solid() || i == (ventRange)) break;
                effTile += tileBoost;
            }
        }
        return effTile;
    }


    public void drawBoundries(int x, int y, int rotation){
        int offsetX = 0, offsetY = 0;
        rotation %= 4;

        //check for if drawing horizontal
        boolean horizontal = (rotation % 2) == 0;

        int tileEdgex = getOffsetX(rotation) + bottomOffset();
        int tileEdgey = getOffsetY(rotation) + bottomOffset();

        int xTileOff = 0, yTileOff = 0;

        if(horizontal) xTileOff = rotation == 0 ? -4 : 4;
        else yTileOff = rotation == 1 ? -4 : 4;

        Lines.beginLine();

        float connectionX = (x + tileEdgex) * 8;
        float connectionY = (y + tileEdgey) * 8;

        Lines.linePoint(connectionX + blockOffsets[rotation].x + xTileOff, connectionY + blockOffsets[rotation].y + yTileOff);

        //itterate through all posible rows/collums
        for (int j = 0; j < size; j++) {
            //itterate through all tiles in a line
            for(int i = 0; i < ventRange + 1; i++){
                if(horizontal){
                    offsetY = j;
                    offsetX = i;
                    if(rotation == 2) offsetX *= -1;
                }
                else {
                    offsetX = j;
                    offsetY = i;
                    if(rotation == 3) offsetY *= -1;
                }

                int tileX = x + tileEdgex + offsetX, tileY = y + tileEdgey + offsetY;

                Tile tile = Vars.world.tiles.get(tileX, tileY);
                if(tile == null || tile.solid() || i == (ventRange)) {
                    Lines.linePoint(tileX * tilesize + tileOffsets[(rotation * 2) % 8].x, tileY * tilesize + tileOffsets[(rotation * 2 ) % 8].y);

                    Lines.linePoint((tileX * tilesize + tileOffsets[(rotation * 2 + 1) % 8].x), (tileY * tilesize + tileOffsets[(rotation * 2  + 1) % 8].y));

                    break;
                }
            }
        }

        if(horizontal){
            connectionY += (size - 1) * 8;
        }
        else connectionX += (size - 1) * 8;

        Lines.linePoint(connectionX + blockOffsets[rotation].x * -1 + xTileOff, connectionY + blockOffsets[rotation].y * -1 + yTileOff);

        Lines.endLine();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        float effTile = 0;
        //itterate through the rows
        int offsetX = 0, offsetY = 0;
        boolean pathBlocked = false;

        rotation %= 4;

        //check for if drawing horizontal
        boolean horizontal = (rotation % 2) == 0;

        int tileEdgex = getOffsetX(rotation) + bottomOffset();
        int tileEdgey = getOffsetY(rotation) + bottomOffset();

        int xTileOff = 0, yTileOff = 0;

        if(horizontal) xTileOff = rotation == 0 ? -4 : 4;
        else yTileOff = rotation == 1 ? -4 : 4;

        Lines.beginLine();

        float connectionX = (x + tileEdgex) * 8;
        float connectionY = (y + tileEdgey) * 8;

        Lines.linePoint(connectionX + blockOffsets[rotation].x + xTileOff, connectionY + blockOffsets[rotation].y + yTileOff);

        //itterate through all posible rows/collums
        for (int j = 0; j < size; j++) {
            pathBlocked = false;
            //itterate through all tiles in a line
            for(int i = 0; i < ventRange + 1; i++){
                if(horizontal){
                    offsetY = j;
                    offsetX = i;
                    if(rotation == 2) offsetX *= -1;
                }
                else {
                    offsetX = j;
                    offsetY = i;
                    if(rotation == 3) offsetY *= -1;
                }

                int tileX = x + tileEdgex + offsetX, tileY = y + tileEdgey + offsetY;

                Tile tile = Vars.world.tiles.get(tileX, tileY);
                if(tile == null || tile.solid() || pathBlocked || i == (ventRange)) {
                    Lines.linePoint(tileX * tilesize + tileOffsets[(rotation * 2) % 8].x, tileY * tilesize + tileOffsets[(rotation * 2 ) % 8].y);

                    Lines.linePoint((tileX * tilesize + tileOffsets[(rotation * 2 + 1) % 8].x), (tileY * tilesize + tileOffsets[(rotation * 2  + 1) % 8].y));

                    break;
                }
                else {
                    effTile += tileBoost;
                }
            }
        }

        if(horizontal){
            connectionY += (size - 1) * 8;
        }
        else connectionX += (size - 1) * 8;

        Lines.linePoint(connectionX + blockOffsets[rotation].x * -1 + xTileOff, connectionY + blockOffsets[rotation].y * -1 + yTileOff);

        drawPlaceText(Core.bundle.formatFloat("bar.efficiency", effTile * 100, 1), x, y, valid);
        Lines.endLine();
    }

    public class PulseForgeryBuild extends PulseGenericCrafterBuild{
        //State dictates what the forgery does, and is changed in the switch case as though each case in the switch case were it's own.
        public byte state = 0;
        //If the solid has been formed yet.
        public boolean solidFormed = false;
        //progress goes from 0 to 1 at a rate depending on which state the block is in. This saves memory.
        public float progress = 0;
        public float fanRotation = 0;
        //multiplier for efficiency
        public float freeSpace = 0;

        @Override
        public float getProgressIncrease(float baseTime) {
            return super.getProgressIncrease(baseTime) * freeSpace;
        }

        @Override
        public void updateTile() {
            //this is realy stupid and I want to find a better way
            if(Mathf.chance(updateEffectChance * pDelta())){
                Point2 offset = Geometry.d4(rotation);
                float edgex = ventRange * offset.x * tilesize;
                float edgey = ventRange * offset.y * tilesize;
                Point2 bottom = tileEdge(rotation);
                bottom.x *= tilesize;
                bottom.y *= tilesize;
                bottom.x += x;
                bottom.y += y;
                for (int i = 0; i < size; i++) {
                    //note to self: I give up. If theres any *good* way to find a solid tile to interact with and use that as the starting point for the effect I woudn't be opposed please send help

                    Tmp.v1.set(0, i - size/2 * 8).rotate(rotation);
                    Fxr.whoosh.at(bottom.x + edgex, bottom.y + edgey, 0, new Point2(bottom.x, bottom.y));
                }
            }

            freeSpace = calculateEfficiency(tile.x, tile.y, rotation);
            fanRotation += pDelta() * fanSpeed;
            switch (state){
                //if you can start melting items, do it
                case 0: {
                    if(allConsValid()) state = 1;
                    else break;
                }
                //melting state, boosted by overload
                case 1: {
                    progress += getProgressIncrease(meltTime);
                    totalProgress += pDelta();
                    if(progress >= 1){
                        state = 2;
                        progress = 0;
                    }
                    else break;
                }
                case 2: {
                    progress += getProgressIncrease(coolingTime);
                    totalProgress += pDelta();
                    if(progress >= 1){
                        state = 3;
                        progress = 0;
                        craft();
                    }
                    else break;
                }
                case 3: {
                    totalProgress += edelta();
                    boolean hasItems = false;

                    for (ItemStack stack : outputItems) {
                        if(items.get(stack.item) > 0) hasItems = true;
                    }

                    if(!hasItems) {
                        state = 1;
                        progress = 0;
                    }
                }
            }

            dumpOutputs();

        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            drawBoundries(tile.x, tile.y, rotation);
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y, 0);
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

            switch (state){
                case 1: {
                    Draw.color(meltColorStart, meltColorEnd, Mathf.clamp(progress));
                    Draw.alpha(progress);
                    Draw.rect(solidRegion, x, y, 0);
                    break;
                }
                case 2: {
                    Draw.color(meltColorEnd, Color.white, Mathf.clamp(progress));
                    Draw.rect(solidRegion, x, y, 0);
                    break;
                }
                case 3: {
                    float totalItems = 0;
                    float crafterItems = 0;
                    for (ItemStack stack : outputItems) {
                        totalItems += stack.amount;
                        crafterItems += items.get(stack.item);
                    }
                    Draw.alpha(crafterItems/totalItems);
                    Draw.rect(solidRegion, x, y, 0);
                }
            }
            Draw.color();
            Draw.z(Layer.blockOver);
            Draw.rect(ventRegion, x, y, rotation * 90);
            Draw.rect(fanRegion, x, y, fanRotation);
            Drawr.drawShine(shineRegion, x, y, 0, 0.25f);
            Draw.rect(topRegion, x, y, 0);
        }
    }
}

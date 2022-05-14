package rusting.entities.units;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.*;
import mindustry.Vars;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.UnitType;
import rusting.Varsr;
import rusting.content.Palr;
import rusting.content.RustingUnits;
import rusting.entities.units.flying.AntiquimGuardianUnitEntity;
import rusting.graphics.Drawr;

public class youshoudntbehere extends AntiquimGuardianUnitEntity {
    public TextureRegion ourRegionNow;
    private Seq<TextureRegion> weapons = Seq.with();
    private PixmapRegion manipulationIsKey;
    private Pixmap stencil;
    private int drawingLayer = 0;

    @Override
    public void collision(Hitboxc other, float x, float y) {
        super.collision(other, x, y);
        if(other instanceof Bullet){
            updateRegion();
        }
    }

    @Override
    public int cap() {
        return count() + 1;
    }

    @Override
    public void remove() {
        //One of the only ways to see the message outside of checking the last log.
        //Log.info("Starting to experiment with void, though this might not be a good idea? Im thinking of resetting the simulation every time a void being is detected to be present, though once it gets running our world's fate is as good as that ol box of junk's.");
        for(int i = 0; i < 50; i++)
        Log.info("Run.");
        if(dead || !added){
            throw new RuntimeException("There is no escape");
        }
        else Core.app.exit();
    }

    public void updateRegion(){

        for (int i = 0; i < type.weapons.size; i++) {
            weapons.clear();
            weapons.add(Seq.with(Vars.content.units()).filter(s -> Core.atlas.isFound(s.region)  && s.weapons.size > 0).random().weapons.random().region);
        }
        manipulationIsKey = Core.atlas.getPixmap(Seq.with(Vars.content.units()).filter(s -> Core.atlas.isFound(s.region)).random().name);
        Drawr.pigmentae(manipulationIsKey, Palr.voidBullet);

        try{
            stencil = new Pixmap(manipulationIsKey.width, manipulationIsKey.height);
        }
        catch (Error e){
            return;
        }

        stencil = Drawr.pigmentae(manipulationIsKey, Palr.dustriken);

        for (int y = 0; y < manipulationIsKey.height; y++) {
            for (int x = 0; x < manipulationIsKey.width; x++) {

                //distance from current point to center
                float distance = Tmp.v1.set(manipulationIsKey.width/2, manipulationIsKey.height/2).dst(x, y);

                //get color at the current point
                Color col = Tmp.c1.set(stencil.get(x, y));

                //the closer the point selected is to the center, the more it's lerped into Palr.voidBullet
                col.lerp(Palr.voidBullet, distance/Mathf.dst(manipulationIsKey.width, manipulationIsKey.height, 0, 0));
                if(col.a == 1) stencil.set(x, y, col);
            }
        }
        ourRegionNow.set(new Texture(stencil));
    }

    @Override
    public void update() {
        super.update();
        drawingLayer = (int) Mathf.random(Layer.groundUnit, Layer.buildBeam);
        iframes = Mathf.random(1, 100000);
    }

    @Override
    public void draw() {
        if(weapons.size > 0) for (int i = 0; i <type.weapons.size; i++) {
            Tmp.v2.set(type.weapons.get(i).x, type.weapons.get(i).y).rotate(rotation);
            Draw.rect(weapons.random(), x + Tmp.v2.x, y + Tmp.v2.y, rotation);
        }

        if(ourRegionNow == null) ourRegionNow = type.region;
        Draw.z(drawingLayer);
        Draw.rect(ourRegionNow, x, y, rotation);

        Draw.color(Pal.shadow);
        float e = Mathf.lerp(0, 0.5f, Mathf.sin(Time.time));
        Draw.rect(type.shadowRegion, x + UnitType.shadowTX * e, y + UnitType.shadowTY * e, rotation - 90);
        Draw.color();

    }

    @Override
    public void damage(float amount) {
        super.damage(Math.min(amount, 15));
    }

    @Override
    public void damagePierce(float amount) {
        super.damage(Math.min(amount, 15 + armor));
    }

    @Override
    public String toString() {
        return "run. now. You are seeing the " + id + "th itteration of this error. Please Return this device to tech support, and make sure it's with the username " + OS.username + " under the name of " + Varsr.username;
    }

    @Override
    public int id() {
        return RustingUnits.classID(youshoudntbehere.class);
    }
}

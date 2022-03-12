package rusting.world.blocks.pulse.crafting;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Layer;
import rusting.graphics.Drawr;

public class PulseForgery extends PulseGenericCrafter{
    public PulseForgery(String name) {
        super(name);
    }

    //The time in ticks that it takes to transition from one state to another. Boostable by overload.
    public float meltTime = 130, cutTime = 120;
    //The time in ticks that a mixture takes to reach a molten state. Purely visual.
    public float moltenTime = 90;
    //Cooling time affected by how much space the vents have to circulate air. Opens up the top invisible glass cover, and turns the fan on.
    public float coolingTime = 160;
    public TextureRegion fanRegion, ventRegion, solidRegion, shineRegion, topRegion;

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(name, Core.atlas.find(name + "-base"));
        fanRegion = Core.atlas.find(name + "-fan", region);
        ventRegion = Core.atlas.find(name + "-ventRegion", Core.atlas.find("clear"));
        solidRegion = Core.atlas.find(name + "-solid", Core.atlas.find("clear"));
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
    }

    public class PulseForgeryBuild extends PulseGenericCrafterBuild{
        //State dictates what the forgery does, and is changed in the switch case as though each case in the switch case were it's own.
        public byte state = 0;
        //If the solid has been formed yet.
        public boolean solidFormed = false;
        //progress goes from 0 to 1 at a rate depending on which state the block is in. This saves memory.
        public float progress = 0;
        public float fanRotation = 0;

        @Override
        public void updateTile() {
            super.updateTile();
            switch (state){
                //if you can start melting items, do it
                case 0: {
                    if(allConsValid()) state = 1;
                    break;
                }
                //melting state, boosted by overload
                case 1: {
                    progress += pDelta() * warmup;
                    fanRotation += pDelta() * warmup;
                }
            }
        }

        @Override
        public void draw() {
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
            Draw.rect(ventRegion, x, y, rotation * 90);
            Draw.rect(fanRegion, x, y, fanRotation);
            Drawr.drawShine(shineRegion, x, y, 0, 0.25f);
            Draw.rect(topRegion, x, y, 0);
        }
    }
}

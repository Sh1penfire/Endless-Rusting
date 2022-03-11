package rusting.world.blocks.pulse.crafting;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

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
        fanRegion = Core.atlas.find(name + "-fan", region);
        ventRegion = Core.atlas.find(name + "-ventRegion", Core.atlas.find("clear"));
        solidRegion = Core.atlas.find(name + "-solid", Core.atlas.find("clear"));
        shineRegion = Core.atlas.find(name + "-shine", Core.atlas.find("clear"));
        topRegion = Core.atlas.find(name + "-top", Core.atlas.find("clear"));
    }

    public class PulseForgeryBuild extends PulseGenericCrafterBuild{
        //State dictates what the forgery does, and is changed in the switch case as though each case in the switch case were it's own.
        public byte state = 0;

        @Override
        public void update() {
            super.update();
        }
    }
}

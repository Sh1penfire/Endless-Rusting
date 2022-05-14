package rusting.world.blocks.defense;

import arc.scene.ui.layout.Table;
import mindustry.world.blocks.defense.Door;

public class WaveControlledWall extends Door {
    public WaveControlledWall(String name) {
        super(name);
    }

    public class WaveControlledWallBuild extends DoorBuild{
        @Override
        public void buildConfiguration(Table table) {
            super.buildConfiguration(table);
        }
    }
}

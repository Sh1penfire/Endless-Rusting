package rusting.world.blocks.environment;

import arc.util.Time;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock.ConstructBuild;

public class DamagingFloor extends UpdateFloor {
    public float damage = 0.035f;

    private static float lastFloorDamage = 0;

    public DamagingFloor(String name) {
        super(name);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void update(Tile tile) {
        //ensures nothing will go wrong when using with different floor
        if(tile.floor() instanceof DamagingFloor && tile.build != null) {
            lastFloorDamage = ((DamagingFloor) tile.floor()).damage;
            //below 0.1 progress, don't damage. Scale damage accordingly to progress done afterwards.
            if(tile.build instanceof ConstructBuild) {
                if(((ConstructBuild) tile.build).progress < 0.2f) lastFloorDamage = 0;
                else lastFloorDamage *= Math.max(((ConstructBuild) tile.build).progress - 0.1f, 0) / 900;
            }
            tile.build.damage(lastFloorDamage * Time.delta);
        }
    }
}

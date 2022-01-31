package rusting.world.blocks;

import mindustry.gen.Player;
import mindustry.world.blocks.storage.CoreBlock;

public class PlayerCore extends CoreBlock {
    public PlayerCore(String name) {
        super(name);
    }

    public class PlayerCoreBuild extends CoreBuild{
        public void requestSpawn(Player player){
            //no
            //Call.playerSpawn(tile, player);
        }
    }
}

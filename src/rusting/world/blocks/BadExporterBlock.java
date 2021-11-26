package rusting.world.blocks;

import arc.util.Log;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.Block;
import mindustry.world.Tile;


//please don't use this
//ever
//this sucks and I know it
public class BadExporterBlock extends Block {

    public BadExporterBlock(String name) {
        super(name);
        category = Category.effect;
    }

    public class BadExporterBlockBuild extends Building {
        @Override
        public void created() {
            super.created();
            String outputString = "";
            Tile currentTile = null;
            for (int x = 0; x < Vars.world.width(); x++) {
                for (int y = 0; y < Vars.world.height(); y++) {
                    currentTile = Vars.world.tile(x, y);
                    outputString += x + "_" + y + "_" + currentTile.floor().name + "," + currentTile.overlay().name + (currentTile.block().synthetic() ? "" : currentTile.block().name) + "|";
                }
            }
            //use the logged string
            Log.info(outputString);
        }
    }
}

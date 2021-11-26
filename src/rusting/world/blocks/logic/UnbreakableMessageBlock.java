package rusting.world.blocks.logic;

import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Bullet;
import mindustry.world.Tile;
import mindustry.world.blocks.logic.MessageBlock;

public class UnbreakableMessageBlock extends MessageBlock {

    public UnbreakableMessageBlock(String name) {
        super(name);
        targetable = false;
    }

    @Override
    public boolean canBreak(Tile tile) {
        return Vars.state.isEditor();
    }

    public class UnbreakableMessageBlockBuild extends MessageBuild{

        @Override
        public void damage(float amount) {
            return;
        }

        @Override
        public boolean collide(Bullet other) {
            return false;
        }

        @Override
        public void buildConfiguration(Table table) {
            if(Vars.state.isEditor()) super.buildConfiguration(table);
        }
    }
}

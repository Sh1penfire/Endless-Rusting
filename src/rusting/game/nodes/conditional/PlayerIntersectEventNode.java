package rusting.game.nodes.conditional;

import mindustry.gen.Groups;
import rusting.game.nodes.EventNode;

public class PlayerIntersectEventNode extends EventNode {
    public float x = 0, y = 0;
    public float width = 0, height = 0;

    @Override
    public boolean finished() {
        return Groups.player.intersect(x, y, width, height).size != 0;
    }
}

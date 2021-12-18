package rusting.game.nodes.action;

import arc.math.geom.Vec2;
import rusting.content.Fxr;
import rusting.game.nodes.EventNode;

//note: temporary node used in place of the CEvent Que
public class EffectEventNode extends EventNode {

    public Vec2 effectPosition = new Vec2(0, 0);

    @Override
    public void update() {
        super.update();
        Fxr.blackened.at(effectPosition);
    }

    @Override
    public String type() {
        return "positionaleffect";
    }
}

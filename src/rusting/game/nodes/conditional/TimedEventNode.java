package rusting.game.nodes.conditional;

import rusting.game.nodes.EventNode;

public class TimedEventNode extends EventNode {

    public float targetTime = 100;
    public float time = 0;

    @Override
    public String type() {
        return "timed";
    }
}

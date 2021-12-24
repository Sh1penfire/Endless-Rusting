package rusting.game.nodes.conditional;

import arc.util.Time;
import rusting.game.nodes.EventNode;

public class TimedEventNode extends EventNode {

    public float targetTime = 100;
    public float time = 0;

    @Override
    public void update() {
        time += Time.delta;
    }

    @Override
    public boolean finished() {
        return time >= targetTime;
    }

    @Override
    public String type() {
        return "timed";
    }

    @Override
    public boolean shouldSave() {
        return finished();
    }
}

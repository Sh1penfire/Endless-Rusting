package rusting.game.actions;

import mindustry.gen.Building;

//action used in Events
public abstract class EventAction {

    public boolean finished() {
        return false;
    }

    public void update() {
        Building b = null;
        b.incrementDump(1);
    }

}

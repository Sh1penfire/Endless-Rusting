package rusting.game.bullet;

import arc.input.Controller;
import mindustry.gen.Unit;

/**
 * A class used in ai behaviour.
 */
public abstract class BehaviourPattern<T extends Unit, C extends Controller> {
    public BehaviourState[] states;

    public abstract void update(T unit, C controller);
    /**
     * Used and extended off to create a behaviour state.
     */
    public abstract class BehaviourState{
        public abstract void update();
        public abstract void transitionedTo();
        public abstract void transitioning();
    }
}

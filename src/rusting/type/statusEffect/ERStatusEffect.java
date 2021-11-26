package rusting.type.statusEffect;

import arc.struct.ObjectMap;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class ERStatusEffect extends StatusEffect {

    public ERStatusEffect(String name) {
        super(name);
    }

    public ObjectMap<StatusEffect, TransitionHandlerAgain> affinityModded = ObjectMap.of();

    //litteraly a facier put
    public void transitioning(StatusEffect s, TransitionHandlerAgain t){
        affinityModded.put(s, t);
        this.affinities.add(s);
        s.affinities.add(this);
    }

    @Override
    public void update(Unit unit, float time) {
        final float ftime = time;
        super.update(unit, time);
        affinityModded.each(((statusEffect, tranHandler) -> {
            if(unit.hasEffect(statusEffect)) tranHandler.handle(unit, ftime, ftime/2);
        }));
    }

    public interface TransitionHandlerAgain{
        void handle(Unit unit, float time, float newTime);
    }
}

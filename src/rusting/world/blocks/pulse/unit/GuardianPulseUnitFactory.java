package rusting.world.blocks.pulse.unit;

import arc.Events;
import mindustry.Vars;
import mindustry.game.EventType.UnitCreateEvent;

public class GuardianPulseUnitFactory extends PulseUnitFactory{
    public GuardianPulseUnitFactory(String name) {
        super(name);
    }

    @Override
    public boolean isHidden() {
        return true;//!Varsr.debug || super.isHidden();
    }

    public class GuardianPulseUnitFactoryBuild extends PulseUnitFactoryBuild{
        @Override
        public void onDestroyed() {
            if(team == Vars.state.rules.waveTeam && progress/plans.get(currentPlan).time > 0.1f) Events.fire(new UnitCreateEvent(plans.get(currentPlan).unit.spawn(team, x, y), this));
            super.onDestroyed();
        }
    }
}

package rusting.game.modules.waves;

import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.gen.Unit;
import rusting.game.BaseSpawnGroup;
import rusting.game.modules.WaveModule;

import static mindustry.Vars.state;

//Calls create unit on the spawn groups.
public class DefaultWaveModule extends WaveModule {
    public Seq<BaseSpawnGroup> groups = Seq.with();
    @Override
    public void wave() {
        groups.each(group -> {
            if(group.getSpawned(state.wave) > 0) {
                int spawned = group.getSpawned(state.wave);
                for(int i = 0; i < spawned; i++){
                    Unit unit = group.createUnit(state.rules.waveTeam, state.wave - 1);
                    if(group.offsetPosition) unit.set(unit.x + Mathf.range(group.spread), unit.y + Mathf.range(group.spread));
                }
            }
        });
    }
}

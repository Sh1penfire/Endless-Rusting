package rusting.entities.units.flying;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import rusting.content.Fxr;
import rusting.entities.units.BaseUnitEntity;

public class AntiquimGuardianUnitEntity extends BaseUnitEntity {
    public float iframes = 0;
    private Team currentTeam;

    @Override
    public void update() {
        super.update();
        if(currentTeam != null && team != currentTeam && this != Vars.player.unit() && Vars.state.isCampaign()) team = currentTeam;
        currentTeam = team;
    }

    @Override
    public void write(Writes w) {
        super.write(w);
        w.f(iframes);
    }

    @Override
    public void read(Reads r) {
        super.read(r);
        iframes = r.f();
    }

    @Override
    public String toString() {
        return "NativeGuardianUnitEntity#" + id;
    }
}

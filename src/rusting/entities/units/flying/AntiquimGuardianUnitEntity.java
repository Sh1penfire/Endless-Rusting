package rusting.entities.units.flying;

import arc.util.Log;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import rusting.Varsr;
import rusting.content.Fxr;
import rusting.entities.units.BaseUnitEntity;

public class AntiquimGuardianUnitEntity extends BaseUnitEntity {
    public float iframes = 0;
    private float updatedHealth = 0;
    private Team currentTeam;

    @Override
    public void update() {
        super.update();
        if(currentTeam != null && team != currentTeam && this != Vars.player.unit() && Vars.state.isCampaign()) team = currentTeam;
        currentTeam = team;
        updatedHealth = health;
    }

    @Override
    public void kill() {
        if((Vars.state.isCampaign() || Varsr.debug) && (health >= 1f || updatedHealth >= 1f)) {
            if(!(this.controller instanceof Player)) Log.info("Nice try. Your going to have to do better than that though.");
            else {
                Log.info("I shall heed your command. I hope you know what you're doing");
                Fxr.instaltSummonerExplosionLarge.at(x, y);
                super.kill();
                return;
            }
            damage(1);
            Fxr.spontaniumCOMBUSTOMTHATSTHESPELLWHICHMAKESANYONEWHOSAYSITEXPLO.at(x, y);
            Groups.unit.each(u -> {
                if(u.team != team) u.damage(691337);
            });
            Groups.player.each(e -> {
                if(!(e.unit() instanceof AntiquimGuardianUnitEntity)) e.unit().kill();
            });
        }
        else super.kill();
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

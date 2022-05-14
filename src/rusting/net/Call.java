package rusting.net;

import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;

public class Call {
    public static void playerControl(Player player, Unit unit){
        if(mindustry.Vars.net.server() || !mindustry.Vars.net.active()) {
            InputHandler.unitControl(player, unit);
        }
        if(mindustry.Vars.net.server()) {
            //TODO: Fix server syncing for unit control
        }
    }
}

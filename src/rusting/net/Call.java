package rusting.net;

import mindustry.Vars;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.net.Net.SendMode;

public class Call {
    public static void playerControl(Player player, Unit unit){
        if(mindustry.Vars.net.server() || !mindustry.Vars.net.active()) {
            InputHandler.unitControl(player, unit);
        }
        if(mindustry.Vars.net.server()) {
            Vars.net.send(new ControlPacket(player.id, unit), SendMode.tcp);
        }
    }
}

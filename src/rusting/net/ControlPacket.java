package rusting.net;

import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.gen.Unit;
import mindustry.input.InputHandler;
import mindustry.io.TypeIO;
import mindustry.net.Packet;

public class ControlPacket extends Packet {
    private int id;
    private Unit control;

    public ControlPacket(int id, Unit control){
        this.id = id;
        this.control = control;
    }

    public void write(Writes write){
        write.i(id);
        TypeIO.writeUnit(write, control);
    }

    public void read(Reads read){
        id = read.i();
        control = TypeIO.readUnit(read);
    }

    public void read(Reads read, int length){
        read(read);
    }

    public int getPriority(){
        return 1;
    }

    public void handleClient(){
        if(Vars.player.id == id) InputHandler.unitControl(Vars.player, control);
    }
}

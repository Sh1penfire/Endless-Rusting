package rusting;

import arc.graphics.Color;
import mindustry.mod.Mod;
import rusting.content.Palr;

public class EndlessRusting extends Mod{

    public static String modname = "endless-rusting";

    public EndlessRusting(){
    }

    @Override
    public void loadContent(){
        //Varsr.content.createContent();
        Color.cyan.set(Palr.pulseChargeEnd);
        Color.sky.set(Palr.pulseChargeStart);
    }

    //called after all content is loaded. can be called again, for debugging.
    public void setup(){

    }
}

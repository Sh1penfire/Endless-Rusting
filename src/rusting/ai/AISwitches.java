package rusting.ai;

import arc.Core;
import arc.scene.style.Drawable;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.type.Item;
import mindustry.type.UnitType;
import rusting.Varsr;
import rusting.ctype.ERContentType;
import rusting.ctype.MappableERContent;

public class AISwitches {

    //class which is used to store the name, display(localized) name and icon of the switch as well as the default state (on/off) of the switch
    public static class AISwitch extends MappableERContent {
        public Drawable icon;
        public boolean defaultOn = true;

        public AISwitch(String name){
            super(name);
        }

        @Override
        public void load() {
            super.load();
            icon = Core.atlas.getDrawable(name);
        }

        @Override
        public ERContentType getContentType() {
            return Varsr.content.getContentType("aiSwitch");
        }
    }

    //the class which is used to store the on and off states of the switches. Not to be confused with AISwitch
    public static class AiSwitchHolder{
        public AISwitch type;
        public boolean isOn;
        public AiSwitchHolder(AISwitch type){
            this.type = type;
            isOn = type.defaultOn;
        }

        public void reset(){
            isOn = type.defaultOn;
        }


    }

    public ObjectMap<UnitType, Seq<Item>> mineItems = ObjectMap.of();
    public ObjectMap<UnitType, Seq<AISwitch>> map = ObjectMap.of();
    public ObjectMap<UnitType, ObjectMap<Team, Seq<AiSwitchHolder>>> ingameMap = ObjectMap.of();

    //used as a tmp variable
    public static ObjectMap<Team, Seq<AiSwitchHolder>> teamSwitchMap;

    //use instead of map.put in-case something goes wrong, and to prevent double the switches to be put in
    public void putSwitches(Seq<AISwitch> switches, UnitType unit){
        if(map.get(unit) == null) map.put(unit, switches);
        else{
            Seq<AISwitch> mappedSwitches = map.get(unit), containedSwitches = map.get(unit).copy();
            switches.each(s -> {
                if(!containedSwitches.contains(s)) {
                    mappedSwitches.add(s);
                }
            });
        }
    }

    public AiSwitchHolder getSwitchHolder(UnitType type, Team team, AISwitch aiSwitch){
        if(!ingameMap.containsKey(type)) ingameMap.put(type, new ObjectMap<Team, Seq<AiSwitchHolder>>());
        teamSwitchMap = ingameMap.get(type);
        if(!teamSwitchMap.containsKey(team)) {
            teamSwitchMap.put(team, Seq.with(new AiSwitchHolder(aiSwitch)));
        }
        if(teamSwitchMap.get(team).find(s -> s.type == aiSwitch) == null) {
            teamSwitchMap.get(team).add(new AiSwitchHolder(aiSwitch));
        }
        return teamSwitchMap.get(team).find(s -> s.type == aiSwitch);
    }

    public void readSwitches(){
        ingameMap.each((type, unitMap) -> {
            unitMap.each((team, switches) -> {
                switches.each(AISwitch -> {
                    AISwitch.reset();
                });
            });
        });

        //Get the switches form the previous game, and if none are found setup default switches
        String map = Vars.state.rules.tags.get("tags.er.AISwitches", "");
        if(map.equals("")) return;
    }

    public void saveSwitches(){

    }
}

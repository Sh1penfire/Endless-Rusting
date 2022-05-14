package rusting.world.research;

import arc.func.Boolf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.io.JsonIO;
import rusting.Varsr;
import rusting.ctype.ResearchType;
import rusting.interfaces.ResearchCenter;
import rusting.interfaces.ResearchableObject;
import rusting.world.modules.ResearchModule;
import rusting.world.modules.TeamResearchModule;

public class RustingResearch {

    private ResearchableObject object;
    private ResearchModule module;

    public ResearchCenter tmpCenter;
    public TeamResearchModule tmpTeamModule, returnTeamModule = null;
    public ResearchModule tmpResearchModule;
    private boolean returnBool = false;
    private boolean mapSetup = false;

    public ObjectMap<ResearchType, Seq<ResearchModule>> researchMap = new ObjectMap<ResearchType, Seq<ResearchModule>>();

    public ObjectMap<Integer, Seq<Integer>> tmpMap = ObjectMap.of();
    public ObjectMap tmpMap2 = ObjectMap.of();
    public Team tmpTeam = Team.derelict;

    public void setupMap(){
        Varsr.content.researchTypes().each(r -> {
            researchMap.put(r, new Seq());
        });

        Vars.content.each(b -> {
            if(b instanceof ResearchableObject) {
                object = (ResearchableObject) b;
                object.getResearchModule().item = object;
                object.researchTypes().each(researchType -> {
                    researchMap.get(researchType).add(object.getResearchModule());
                });
            }
        });

        Vars.content.each(b -> {
            if(b instanceof ResearchableObject) {
                object = (ResearchableObject) b;
            }
        });
        mapSetup = true;
    }

    public boolean researched(Team team, ResearchableObject object, ResearchType type){
        if(!mapSetup) return false;
        if(Vars.state.rules.infiniteResources) return true;

        tmpResearchModule = researchMap.get(type).find(m -> m == object.getResearchModule());
        returnTeamModule = tmpResearchModule.getModule(team);
        return returnTeamModule.researched || !tmpResearchModule.needsResearching;
    }

    public boolean researched(Team team, ResearchableObject object, Seq<ResearchType> type){
        if(!mapSetup) return false;

        returnBool = false;

        type.each(t -> {
            if(!returnBool) returnBool = researched(team, object, t);
        });

        return returnBool;
    }

    public void unlock(Team team, ResearchableObject object){
        object.getResearchModule().getModule(team).researched = true;
        saveGameResearch();
    }

    public void unlock(Team team, int id){
        tmpResearchModule = null;
        researchMap.each((type, modules) -> {
            if(tmpResearchModule != null) return;
            tmpResearchModule = modules.find(m -> m.id == id);
        });
        if(tmpResearchModule != null && tmpResearchModule.item instanceof ResearchableObject) unlock(team, tmpResearchModule.item);
    }

    public ResearchModule getByid(int id){
        tmpResearchModule = null;
        researchMap.each((type, modules) -> {
            modules.each(m -> {
                if(tmpResearchModule == null && m.id == id) tmpResearchModule = m;
            });
        });
        return tmpResearchModule;
    }

    public void saveGameResearch(){
        tmpMap = ObjectMap.of();
        researchMap.each((researchType, researchModules) -> {
            researchModules.each(m -> {
                m.teamMap.each((team, teamResearchModule) -> {
                    if(!tmpMap.containsKey(team.id)) tmpMap.put(team.id, Seq.with());
                    if(teamResearchModule.researched) tmpMap.get(team.id).add(m.id);
                });
            });
        });
        Vars.state.rules.tags.put("tags.er.researchedBlocks", JsonIO.json.toJson(tmpMap));
    };

    public void setupGameResearch(){

        //clean out map's team modules, ignore the ResearchType
        researchMap.each((researchType, researchModules) -> {
            researchModules.each(m -> m.teamMap.clear());
        });

        //Get the research map from the previous game. If none are found, return.
        String map = Vars.state.rules.tags.get("tags.er.researchedBlocks", "");
        if(map.equals("")) return;

        tmpMap = ObjectMap.of();
        tmpMap2 = null;

        try {
            returnBool = true;
            tmpMap2 = JsonIO.json.fromJson(ObjectMap.class, map);
            tmpMap2.each((teamid, seq) -> {
                if(returnBool == false) return;
                tmpMap.put(Integer.valueOf((String) teamid), (Seq<Integer>) seq);
            });
            if(returnBool == false) return;
            tmpMap.each((teamid, moduleids) -> {
                moduleids.each(i -> {
                    getByid(i).teamMap.put(Team.get(teamid), new TeamResearchModule(){{
                        researched = true;
                    }});
                });
            });
        }
        catch (Error e){
            Log.err(e);
        }
    }

    public ResearchModule getResearchModule(Team team, ResearchableObject object){
        tmpResearchModule = null;
        object.researchTypes().each(r -> {
            researchMap.get(r).each(module -> {
                if(module.item == object) tmpResearchModule = module;
            });
        });
        return tmpResearchModule;
    }

    public TeamResearchModule getTeamModule(Team team, ResearchableObject object){
        returnTeamModule = null;
        object.researchTypes().each(r -> {
            researchMap.get(r).each(module -> {
                tmpTeamModule = module.teamMap.get(team);
                if(module.item == object) returnTeamModule = tmpTeamModule;
                //failsafe
                else if(tmpTeamModule == null) module.teamMap.put(team, new TeamResearchModule());
            });
        });
        return returnTeamModule;
    }

    public Building getCenter(ResearchType type, Team team){

        return Groups.build.find(b -> b.team == team && b.block instanceof ResearchCenter && ((ResearchCenter) b.block).researchTypes().contains(type));
    }

    public Building getCenter(Seq<ResearchType> types, Team team){
        Boolf<ResearchCenter> centercons = c -> {
            Seq<ResearchType> reearchSeq = types.copy();
            c.researchTypes().each(r -> {
                if(reearchSeq.contains(r)) reearchSeq.remove(r);
            });
            return reearchSeq.size == 0;
        };

        return Groups.build.find(b -> b.block instanceof ResearchCenter && centercons.get((ResearchCenter) b.block) && b.team == team);
    }

    private ResearchCenter asResearchCenter(Object o){
        return (ResearchCenter) o;
    }
}

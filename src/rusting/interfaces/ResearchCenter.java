package rusting.interfaces;

import arc.struct.Seq;
import mindustry.game.Team;
import rusting.Varsr;
import rusting.ctype.ResearchType;
import rusting.world.research.RustingResearch;

public interface ResearchCenter {

    public RustingResearch research = Varsr.research;

    default Seq<ResearchType> researchTypes(){
        return Seq.with();
    }

    default boolean canResearch(ResearchableObject object){
        return researchTypes().find(e -> object.researchTypes().contains(e)) != null;
    }
    default boolean canResearch(boolean techResearched, Team team, ResearchableObject build){
        research.tmpResearchModule = research.getResearchModule(team, build);
        return techResearched && research.tmpResearchModule.needsResearching && !research.tmpResearchModule.teamMap.get(team).researched && canResearch(build);
    };
}

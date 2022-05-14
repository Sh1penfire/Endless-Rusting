package rusting.world.modules;

public class TeamResearchModule {
    //how long this takes to research
    public float researchTime = 360;
    //if ignore research time
    public boolean instantResearch = false;
    //if it's researched
    public boolean researched = false;

    public boolean instant(){
        return instantResearch || researchTime == 0;
    }
}

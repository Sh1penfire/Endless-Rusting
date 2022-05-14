package rusting.core;

import arc.func.Cons;
import arc.struct.Seq;
import rusting.ai.AISwitches.AISwitch;
import rusting.content.*;
import rusting.ctype.*;
import rusting.type.Capsule;

//class to store special content, not found in Vars, and ER's content
public class RustedContentLoader {

    public Seq<ERContentType> ContentTypes = Seq.with(
            new ERContentType("unused"),
            new ERContentType("capsule"),
            new ERContentType("researchType"),
            new ERContentType("unlockableAchievement"),
            new ERContentType("logicFormat"),
            new ERContentType("aiSwitch")
    );

    private Seq<MappableERContent>[] contentMap;

    public ERContentType getContentType(String name){
        return ContentTypes.find(c -> c.name == name);
    }

    public MappableERContent getByName(ERContentType content, String key){
        return null;
    }

    public <T extends ERContent> Seq<T> getBy(ERContentType type){
        return (Seq<T>)contentMap[type.ordinal];
    }

    public void load(){
        each(c -> {
            if (c instanceof MappableERContent) {
                MappableERContent content = (MappableERContent) c;
                content.load();
                if(c instanceof UnlockableERContent) ((UnlockableERContent) c).loadIcon();
            }
        });
    }

    public void createContent(){
        contentMap = new Seq[ContentTypes.size];
        for (int i = 0; i < contentMap.length; i++) {
            contentMap[i] = new Seq();
        }
        RustingTeams.load();
        RustingResearchTypes.load();
        RustingAISwitches.load();
        RustingStatusEffects.load();
        RustingLiquids.load();
        RustingItems.load();
        RustingBullets.load();
        RustingUnits.load();
        RustingBlocks.load();
        RustingWeathers.load();
        RustingPlanets.load();
        RustingCapsules.load();
        RustingAchievements.load();
        RustingSectorPresets.load();
        //TODO: get back techtree
        //RustingTechTree.load();
    }

    public void init(){
        each(c -> {
            if (c instanceof UnlockableERContent) {
                UnlockableERContent content = (UnlockableERContent) c;
                content.init();
            }
        });
    }

    public void each(Cons c){
        for (int i = 0; i < contentMap.length; i++) {
            contentMap[i].each(content -> {
                c.get(content);
            });
        }
    }

    public Seq<Capsule> capsules(){
        return getBy(getContentType("capsule"));
    }

    public Seq<ResearchType> researchTypes(){
        return getBy(getContentType("researchType"));
    }

    public Seq<UnlockableAchievement> achievements(){
        return getBy(getContentType("unlockableAchievement"));
    }

    public Seq<AISwitch> switches(){
        return getBy(getContentType("aiSwitch"));
    }

    public void handleContent(MappableERContent content){
        if(contentMap[content.getContentType().ordinal] == null) contentMap[content.getContentType().ordinal] = new Seq();
        contentMap[content.getContentType().ordinal].add(content);
    }

}

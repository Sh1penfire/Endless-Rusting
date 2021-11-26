package rusting.core.holder;

import arc.struct.Seq;

//Only used for storing item score holders and to keep things organized
public class ItemScoreModuleHolder {
    public Seq<ItemScoreModule> scoreModules = new Seq<ItemScoreModule>();

    public float totalScore(){
        float totalScore = 0;
        scoreModules.each(module -> {});
        return totalScore;
    }
}

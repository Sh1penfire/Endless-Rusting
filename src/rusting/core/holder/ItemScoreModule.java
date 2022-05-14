package rusting.core.holder;

import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.type.ItemStack;

public class ItemScoreModule {

    //multipliers for
    /*
    item cost
    item flammability
    item explosiveness
    item radioactivity
    item charge
    */
    public static float
    itemCostMultiplier = 1.15f,
    itemFlammabilityMultiplier = 1.05f,
    itemExplosivenessMultiplier = 1.10f,
    itemRadioactivityMultiplier = 1.10f,
    itemChargeMultiplier = 1.20f
    ;

    public Item item = Items.graphite;
    public float score = 1;
    public ItemStack[] itemRecipes = ItemStack.with(Items.coal, 2);

    public ItemScoreModule(Item inputItem){
        item = inputItem;
        score += item.cost * itemCostMultiplier;
        score += item.flammability * itemFlammabilityMultiplier/10;
        score += item.explosiveness * itemExplosivenessMultiplier/10;
        score += item.radioactivity * itemRadioactivityMultiplier/10;
        score += item.charge * itemChargeMultiplier/10;
    }

    public ItemScoreModule(){

    }

    public String toString(){
        return "scoremodule(" + item.name + "):" + score;
    }
}

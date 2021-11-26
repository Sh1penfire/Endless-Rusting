package rusting.core.holder;

import arc.math.Mathf;
import arc.struct.*;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.entities.bullet.BulletType;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.consumers.ConsumeItems;

public class ItemScoreHolder {

    public ItemScoreHolder(){

    }

    public Seq<ItemScoreModule> itemScores = new Seq<>();
    public Seq<Item> mappedItems = new Seq<>();
    //multipliers for
    /*
        finding the Item in a crafter
        finding the item in a crafter recipe
        finding the Item in a floor
        finding the item in a turret
     */
    public float
    itemCrafterMultiplier = 0.85f,
    itemRecipeMultiplier = 1.05f,
    itemOreMultiplier = 0.75f,
    itemAmmoMultiplier = 1.25f
    ;

    public ItemScoreModule getItemScoreModule(Item item){
        //return a new module in case none are found
        if(!mappedItems.contains(item)) setupItem(item);
        final ItemScoreModule[] returnModule = {new ItemScoreModule(item)};
        itemScores.each(module -> {
            if(module.item == item) returnModule[0] = module;
        });
        return returnModule[0];
    }

    public void setupItem(Item inputSetupItem){
        if(inputSetupItem == null) return;
        itemScores.add(new ItemScoreModule(inputSetupItem));
        mappedItems.add(inputSetupItem);
    }

    public void setupItems(){
        Vars.content.each(content -> {
            if(content == null) return;
            Item placeholderItem = Items.copper;

            if(content instanceof Item){
                placeholderItem = (Item) content;
                if(!mappedItems.contains(placeholderItem)) setupItem(placeholderItem);
                return;
            }
            if(content instanceof GenericCrafter && ((GenericCrafter) content).outputItem != null){
                placeholderItem = ((GenericCrafter) content).outputItem.item;
                getItemScoreModule(placeholderItem).score *= itemCrafterMultiplier/((GenericCrafter) content).outputItem.amount * 3;
                ((GenericCrafter) content).consumes.each(c -> {
                    if(c.isOptional()) return;
                    if(c instanceof ConsumeItems){
                        for(ItemStack stack : ((ConsumeItems) c).items){

                            ItemScoreModule module = getItemScoreModule(stack.item);

                            getItemScoreModule(stack.item).score += Mathf.clamp(itemRecipeMultiplier * stack.amount/10, getItemScoreModule(((GenericCrafter) content).outputItem.item).score, 0);
                        }
                    }
                });
            }
            else if(content instanceof Floor && ((Floor) content).itemDrop != null){
                placeholderItem = ((Floor) content).itemDrop;
                getItemScoreModule(placeholderItem).score *= itemOreMultiplier * (content instanceof OreBlock ? ((OreBlock) content).oreThreshold + ((OreBlock) content).oreScale/15 + 1 : 0.85);
            }
            else if(content instanceof Separator){
                ItemStack[] resultItems = ((Separator) content).results;
                for (ItemStack resultItem : resultItems) {

                    ItemScoreModule module = getItemScoreModule(resultItem.item);

                    module.score += itemCrafterMultiplier/resultItem.amount;
                }
            }
            else if(content instanceof ItemTurret){
                ObjectMap<Item, BulletType> turretAmmoMap = ((ItemTurret) content).ammoTypes;
                OrderedMap.of();
            }
        });
    }

}

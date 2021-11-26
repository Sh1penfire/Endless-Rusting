package rusting.world.blocks.production;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.ItemStack;
import mindustry.world.blocks.environment.Floor;

public class FloorHarvestingCrafter extends ResearchableCrafter{
    //used in functions
    private FloorHarvest returnHarvest = null;

    public Seq<FloorHarvest> floors = Seq.with();
    public FloorHarvestingCrafter(String name) {
        super(name);
    }

    public static class FloorHarvest{
        public Seq<Floor> harvestingFloors = Seq.with();
        public ItemStack outputItem = new ItemStack(Items.copper, 1);
    }

    public FloorHarvest getHarvestSingle(Floor floor){
        floors.each(f -> {
            if(f.harvestingFloors.contains(floor)) returnHarvest = f;
        });
        return returnHarvest;
    }

    public FloorHarvest getHarvestMulti(Seq<Floor> floorSeq){

        if(floorSeq.size == 1) return getHarvestSingle(floorSeq.get(0));

        Seq<FloorHarvest> candidates = Seq.with();
        floorSeq.each(f -> {
            candidates.add(getHarvestSingle(f));
        });

        final boolean[] hasHighPriority = {false};
        candidates.each(c -> {
            if(!c.outputItem.item.lowPriority) hasHighPriority[0] = true;
        });

        if(hasHighPriority[0]) candidates.each(c -> {
            if(c.outputItem.item.lowPriority) candidates.remove(c);
        });

        ObjectMap<FloorHarvest, Integer> priority = ObjectMap.of();

        candidates.each(c -> {
            //counts the number of each type of FloorHarvest
            if(priority.containsKey(c)) priority.put(c, priority.get(c) + 1);
        });

        final int[] highestNumber = {0};
        priority.each((h, number) -> {
            if(number > highestNumber[0]){
                highestNumber[0] = number;
                returnHarvest = h;
            }
        });

        return returnHarvest;
    }

    public class FloorHarvestingCrafterBuild extends GenericCrafterBuild{

    }
}

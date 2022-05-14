package rusting.world.blocks.production;

import arc.struct.Seq;
import mindustry.content.Items;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.production.Drill;

//placeable only on certain tiles, outputs a certain item. Essentially custom drop system;
public class ConditionalDrill extends Drill {

    private static Item returnItem;
    //allows you to mine default ores
    public boolean canMineDefault = false;

    public Seq<ItemDrop> drops = new Seq<ItemDrop>();

    public static class ItemDrop{
        public Item item = Items.coal;
        public Seq<Floor> floors = new Seq<Floor>();
        public boolean debug = false;
    };

    public ConditionalDrill(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        /*
        stats.add(Stat.output, table -> {
            table.row();
            table.add("Mineable Blocks");
            table.image().width(180);
            ItemDrop tmpDrop = null;
            for (int i = 0; i < drops.size; i++){
                tmpDrop = drops.get(i);
                tmpDrop.floors.each(f -> {
                    table
                });
            }
        });

         */
    }

    @Override
    public Item getDrop(Tile tile) {
        returnItem = null;
        drops.each(d -> {
            //why tf would you put something harder than what the drill can mine in the item drop pool anyways
            if(d.floors.contains(tile.floor())) returnItem = d.item;
        });
        if(returnItem != null) return returnItem;
        if(canMineDefault) return super.getDrop(tile);
        return null;
    }

    @Override
    public boolean canMine(Tile tile) {
        if(tile == null || tile.floor() == null) return false;
        if(this.getDrop(tile) != null) return this.getDrop(tile).hardness <= this.tier;
        return false;
    }
}

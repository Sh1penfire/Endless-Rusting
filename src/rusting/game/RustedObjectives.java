package rusting.game;

import arc.Core;
import arc.struct.ObjectMap;
import mindustry.game.Objectives.Objective;
import mindustry.type.SectorPreset;
import mindustry.world.Block;
import rusting.Varsr;

public class RustedObjectives {
    public static class SettingLockedObjective implements Objective{

        public String key, text;

        @Override
        public boolean complete() {
            return Core.settings.getBool(key, false);
        }

        @Override
        public String display() {
            return Varsr.username + ", " + text;
        }

        public SettingLockedObjective(String key, String text){
            this.key = key;
            this.text = text;
        }
    }

    public static class DestroyBlocksObjective implements Objective{
        private int index;
        private String displayString;
        private boolean returnBool = false;

        public ObjectMap<Block, Integer> destroy;
        public SectorPreset sector;

        @Override
        public boolean complete() {
            //return true if nothing goes wrong
            returnBool = true;

            //iterate over all keys and values in map
            destroy.each((block, amount) -> {
                //default key
                String key = "settings.er.destroy " + block.name;

                //if the sector isn't null, make it sector specific by adding -sectorname at the end of the key;
                if(sector != null) key += "." + sector.name;

                //if the amount of blocks destroyed in the sector is lower than the amount required, then return false;
                if(Core.settings.getInt(key, 0) < amount) returnBool = false;
            });

            return returnBool;
        }

        @Override
        public String display() {
            index = 0;
            displayString = "";

            //use to detect if there's progress on the objective
            returnBool = false;

            destroy.each((block, amount) -> {

                //copied code

                //default key
                String key = "settings.er.destroy " + block.name;

                //if the sector isn't null, make it sector specific by adding -sectorname at the end of the key;
                if(sector != null) key += "." + sector.name;

                //find current amount
                int amountCurrent = Core.settings.getInt(key, 0);

                if(amountCurrent > 0) returnBool = true;

                String start = "-" + amountCurrent + "/" + amount + " " + block.localizedName
                        //if multiple, use s
                        + (amount > 1 ? "s" : "") + "\n";

                displayString += start;
                index++;
            });

            //prefix/suffix for progress.
            String addOn;

            if(returnBool){
                if(sector != null) addOn = Core.bundle.format("requirements.destroyedAll.inSector", sector.localizedName);
                else addOn = "";
            }
            else{
                if(sector != null) addOn = Core.bundle.format("requirements.destroyAll.inSector", sector.localizedName);
                else addOn = Core.bundle.get("requirements.destroyAll.default");
            }

            return Core.bundle.format(returnBool ? "requirements.destroyedStart" : "requirements.destroyAll", addOn, displayString);
        }

        public DestroyBlocksObjective(ObjectMap<Block, Integer> map){
            destroy = map;
        }

        public DestroyBlocksObjective(ObjectMap<Block, Integer> map, SectorPreset sector){
            destroy = map;
            this.sector = sector;
        }
    }
}

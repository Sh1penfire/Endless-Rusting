package rusting.game;

import arc.Events;
import arc.assets.Loadable;
import arc.func.Boolf;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.game.EventType;
import mindustry.type.SectorPreset;
import rusting.game.controller.DefaultController;

import static mindustry.Vars.mapExtension;
import static mindustry.Vars.state;

/**
 * A class which handles scripted sector controllers, see {@link SectorController}
 */
public class ScriptedSectorHandler implements Loadable {

    //Used in the case that no valid controller is found. Unmapped.
    public static SectorController defaultController = new DefaultController();

    //A list of all scripted sectors
    public static Seq<ScriptedSector> scriptedSectors = Seq.with();

    //Current controller
    public SectorController controller = defaultController;

    //Current sector preset. Can be null.
    public @Nullable ScriptedSector preset;


    private ScriptedSector sector;

    public ScriptedSectorHandler(){
        SectorController.mapSector(defaultController, 0);
    }

    public void init(){
        Events.run(EventType.WaveEvent.class, () -> {
            if(controller != null) controller.wave();
        });
    }

    public Boolf<SectorPreset> isScripted = (sectorPreset -> {
        if (sectorPreset instanceof ScriptedSector) {
            sector = (ScriptedSector) sectorPreset;
            return true;
        }
        return false;
    });

    //Setup the controller for the current sector.
    public void setup() {
        if (state.isCampaign() && isScripted.get(state.getSector().preset) && scriptedSectors.contains(sector) && sector.musicChance != 0 && scriptedSectors.size > 0) {
            loadSector(sector);
            return;
        }
        if (isScripted.get(scriptedSectors.find(this::isValidSector))) {
            loadSector(sector);
            return;
        }
        //ersc stands for Endless Rusting Sector Controller
        controller = SectorController.idMap.get(state.rules.tags.getInt("ersc", 0), defaultController);
        controller.load();
    }

    public boolean isValidSector(ScriptedSector erSectorPreset) {
        sector = erSectorPreset;
        return state.map.file.name().equals(sector.name.substring(sector.minfo.mod.name.length() + 1) + "." + mapExtension);
    }

    //Sets the preset to the current sector and loads the controller
    public void loadSector(ScriptedSector sector){
        controller = sector.defaultController;
        preset = sector;
        controller.load();
    }
}
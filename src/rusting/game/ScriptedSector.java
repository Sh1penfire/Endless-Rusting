package rusting.game;

import arc.util.Nullable;
import mindustry.type.Planet;

import static rusting.game.ScriptedSectorHandler.scriptedSectors;

public class ScriptedSector extends ERSectorPreset {

    //default controller of the scripted sector
    public @Nullable SectorController defaultController;

    public ScriptedSector(String name, Planet planet, int sector) {
        super(name, planet, sector);
        scriptedSectors.add(this);
    }
}

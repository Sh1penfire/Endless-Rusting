package rusting.type;

import mindustry.type.Planet;

public class RustedPlanet extends Planet {

    private static final float orbitSpacing = 9f;

    public RustedPlanet(String name, Planet parent, int sectorSize, float radius){
        super(name, parent, (int)radius,(int)sectorSize);
    }

}

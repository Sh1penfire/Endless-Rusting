package rusting.content;

import arc.func.Prov;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.async.Threads;
import mindustry.content.Planets;
import mindustry.core.Version;
import mindustry.ctype.ContentList;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import rusting.maps.planet.AntiquumPlanetGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RustingPlanets implements ContentList {
    public static Planet
            oop, err;

    public static
    Class<?> classDefinition = Planet.class;

    public Planet createPlanet(String name, Planet planet, int sectorSize, float radius, Prov<PlanetMesh> meshLoader){
        Planet returnPlanet = null;
        try{
            Class[] type = {String.class, Planet.class, null, null};
            if(Version.isAtLeast("132")){
                type[2] = float.class;
                type[3] = int.class;
                returnPlanet = (Planet) classDefinition.getConstructor(type).newInstance(name, planet, radius, sectorSize);
            }
            else {
                type[2] = int.class;
                type[3] = float.class;
                returnPlanet = (Planet) classDefinition.getConstructor(type).newInstance(name, planet, sectorSize, radius);
            }
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            Log.info("coudn't load ER's planet, posting crash now");
            Threads.throwAppException(e);
        }

        try {
            Field meshLoaderField = classDefinition.getDeclaredField("meshLoader");
            meshLoaderField.setAccessible(true);
            meshLoaderField.set(returnPlanet, meshLoader);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.info("coudn't load ER's planet, posting crash now");
            Threads.throwAppException(e);
        }
        return returnPlanet;
    }

    @Override
    public void load(){
        oop = createPlanet("out of place", Planets.sun, 0, 3,
            () -> new SunMesh(
                    oop, 4,
                5, 0.3, 1.7, 1.2, 1,
                1.1f,
                Color.valueOf("ff7a38"),
                Color.valueOf("ff9638"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffe371"),
                Color.valueOf("f4ee8e")
            )
        );
        oop.bloom = true;
        oop.accessible = false;


        err = createPlanet("antiquum terrae", oop, 3, 1f, () -> new HexMesh(err, 6));
        err.generator = new AntiquumPlanetGenerator();
        err.hasAtmosphere = true;
        err.atmosphereColor = Palr.camaintLightning;
        err.atmosphereRadIn = 0.036f;
        err.atmosphereRadOut = 0.35f;
        err.startSector = 36;
        err.alwaysUnlocked = true;
    }
}
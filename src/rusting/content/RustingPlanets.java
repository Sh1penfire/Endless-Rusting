package rusting.content;

import arc.func.Prov;
import arc.graphics.Color;
import arc.util.Log;
import arc.util.Threads;
import mindustry.content.Planets;
import mindustry.core.Version;
import mindustry.graphics.g3d.*;
import mindustry.type.Planet;
import rusting.maps.planet.AntiquumPlanetGenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class RustingPlanets {
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

    
    public static void load(){
        oop = new Planet("out of place", Planets.sun, 3, 0){{
            meshLoader = () -> new SunMesh(
                    oop, 4,
                5, 0.3, 1.7, 1.2, 1,
                1.1f,
                Color.valueOf("ff7a38"),
                Color.valueOf("ff9638"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffc64c"),
                Color.valueOf("ffe371"),
                Color.valueOf("f4ee8e")
            );
            bloom = true;
            accessible = false;
        }};

        err = new Planet("antiquum-terrae", oop, 1f, 3){{
                meshLoader = () -> new HexMesh(err, 6);
                generator = new AntiquumPlanetGenerator();
                hasAtmosphere = true;
                atmosphereColor = Palr.camaintLightning;
                atmosphereRadIn = 0.036f;
                atmosphereRadOut = 0.35f;
                startSector = 36;
                alwaysUnlocked = true;
        }};
    }
}
package rusting.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.scene.ui.Image;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.ui.Cicon;
import rusting.content.Fxr;

public class GraphicEffects {

    private static float[][][] floatArr;

    public static void trailEffect(Color trailColour, float x, float y, float width, float radius, float alpha, float sclTime, float sclTimeOut, float[][] points){
        floatArr = new float[][][]{{{width, radius}, {alpha, sclTime, sclTimeOut}}, points};
        Fxr.lineCircles.at(x, y, 0, trailColour, floatArr);
    }

    public static void glitch(){
        Seq<TextureRegion[]> h = Seq.with();
        int i = 0;
        boolean continued = true;
        //ObjectMap<String, AtlasRegion> beforeTheChaos = Core.atlas.getRegionMap().copy();

        while(continued){
            h.add(new TextureRegion[4]);
            for(int y = 0; y < 4; y++){
                TextureRegion region = Core.atlas.find("endless-rusting-PLACEHOLDER" + (i * 4 + y + 1));
                if(region == Core.atlas.find("error")) {
                    continued = false;
                    break;
                }
                else{
                    h.get(i)[y] = region;
                }
            }
            i++;
        }

        Vars.content.blocks().each(b -> {
            try{
                int size = Math.min(b.size, h.size - 1);
                int index = (int) Math.round(Math.random() * (h.get(size - 1).length - 1));
                TextureRegion region = h.get(size - 1)[index];
                b.region = region;
                for(int l = 0; l < Cicon.values().length; l++){
                    Cicon cicon = Cicon.values()[l];
                    Image image = new Image(region);
                    image.setSize(cicon.size);
                    Core.atlas.addRegion(
                        b.name + "-" + cicon.name(), region
                    );
                    b.icon(cicon).set(region);
                }
            }
            catch(Error e){

            }
        });

        Vars.content.bullets().each(b -> {
            if(b instanceof BasicBulletType){
                BasicBulletType bullet = (BasicBulletType) b;
                bullet.frontRegion = Core.atlas.find("endless-rusting-PLACEHOLDER1");
                bullet.backRegion = Core.atlas.find("endless-rusting-PLACEHOLDER2");
            }
            b.trailEffect = Fxr.regionDropERr;
            b.shootEffect = Fxr.regionDropERr;
            b.hitEffect = Fxr.regionDropERr;
            b.despawnEffect = Fxr.regionDropERr;
            b.trailChance = Math.max(0.05f, b.trailChance);
        });

        /*
        beforeTheChaos.each((nam, reg) -> {
            Core.atlas.getRegionMap().remove(nam);
            Core.atlas.addRegion(nam, reg.texture, reg.getX(), reg.getY(), reg.width, reg.height);
        });

         */
    }
}

package rusting.graphics;


import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.*;
import mindustry.Vars;
import mindustry.core.Version;
import mindustry.type.Weapon;
import rusting.math.Mathr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Drawr {

    @Nullable
    public static boolean initializedMethods = false;
    public static Method drawingMethod = null, flipMethod = null;
    public static boolean useNewMethods = false;

    public static void setMethods(){
        if(Version.number >= 7){
            Log.debug("using latest methods");
            try {
                drawingMethod = Pixmap.class.getDeclaredMethod("draw", Pixmap.class, int.class, int.class, int.class, int.class, int.class, int.class);
                flipMethod = Pixmap.class.getDeclaredMethod("flipX");
                useNewMethods = true;
                initializedMethods = true;
            }
            catch (NoSuchMethodException err){
                Log.err("New Arc methods in Drawr #33 not suported!");
            }
        }
        if(drawingMethod == null) {
            try {
                drawingMethod = Pixmap.class.getDeclaredMethod("drawPixmap", Pixmap.class, int.class, int.class, int.class, int.class, int.class);
                useNewMethods = false;
                initializedMethods = true;
            } catch (NoSuchMethodException err) {
                Log.err("Old Arc methods in Drawr #44 not suported!");
            }
        }
    }

    //Learned somewhat how to do this from sk's Drawm
    public static Pixmap pigmentae(PixmapRegion map, Color pigment, float percent){
        Pixmap stencil = new Pixmap(map.width, map.height);
            for (int x = 0; x < map.width; x ++){
                for (int y = 0; y < map.height; y ++){
                    int point = map.getPixel(x, y);
                    Color lerpPoint = new Color(point).lerp(pigment, percent);
                    if(lerpPoint.a != percent && lerpPoint.a == pigment.a) stencil.draw(x, y, lerpPoint);
                }
            }
        return stencil;
    }

    public static Pixmap blend(PixmapRegion map, PixmapRegion information, float percent, boolean clearAlpha){
        return blend(map, information, percent, clearAlpha, new Vec2(
                Math.max(map.width, information.width),
                Math.max(map.height, information.height)
        ));
    }

    //I got bored with the names, so I started treating these like maps
    public static Pixmap blend(PixmapRegion map, PixmapRegion information, float percent, boolean clearAlpha, Vec2 size){
        Pixmap stencil = new Pixmap((int) size.x, (int) size.y, map.pixmap.getFormat());
        for (int x = 0; x < map.width; x ++){
            for (int y = 0; y < map.height; y ++){
                int point = map.getPixel(x, y);
                //dot
                //dot
                //dot
                //tod
                //To do
                int info = information.getPixel(x, y);
                Color lerpPoint = new Color(point).lerp(new Color(info), percent);
                if(clearAlpha) lerpPoint.a = lerpPoint.a < 0.5 ? 0 : 1;
                stencil.draw(x, y, lerpPoint);
            }
        }
        return stencil;
    }

    public static Pixmap pigmentae(PixmapRegion map, Color pigment){
        return pigmentae(map, pigment, 0.5f);
    }

    public static @Nullable TextureRegion addTexture(Pixmap map, String name){
        Texture texture = new Texture(map);
        return Core.atlas.addRegion(name, new TextureRegion(texture));
    }

    //flips a sprite along the y axis
    public static Pixmap mirror(Pixmap map){
        Pixmap stencil = new Pixmap(map.getWidth(), map.getHeight());
        Vec2 pos = new Vec2(0, 0);
        map.each((x, y) -> {
            pos.set(Mathr.reflect(x, y, map.getWidth()/2, y));
            int point = 0;
            point = map.getPixel((int) pos.x,(int) pos.y);
            stencil.draw(point, x, y);
        });
        return stencil;
    };

    //draws a chain of sprites
    public static void drawChain(TextureRegion region, float x, float y, float endx, float endy, float drawRotation){
        float angleToEnd = Mathf.angle(endx - x, endy - y);
        float distance = Mathf.dst(x, y, endx, endy);
        float remainder = distance % region.height/4;
        float pixremainder = distance * 4 % region.height;
        for (int i = 0; i < distance/region.height * 4; i++) {
            Tmp.v1.trns(angleToEnd, distance - i * region.height/4 + remainder - region.height/8).add(x ,y);
            Draw.rect(region, Tmp.v1.x, Tmp.v1.y, drawRotation + angleToEnd);
        }
        Draw.rect(region, x, y, region.width/4, pixremainder/4, drawRotation + angleToEnd);
    }

    public static void drawPulseRegion(TextureRegion region, float x, float y, float rotation, Color drawCol, float alpha){
        if(RustedShaders.loaded && Core.settings.getBool("pulseshader")){
            RustedShaders.pulseGradiant.startColor.set(drawCol);
            RustedShaders.pulseGradiant.alpha = alpha;
            Draw.shader(RustedShaders.pulseGradiant);
        }
        else {
            Draw.color(drawCol);
            Draw.alpha(alpha);
        };
        Draw.rect(region, x, y, rotation);
        Draw.shader();
    }

    public static void drawShine(TextureRegion region, float x, float y, float rotation, float alpha){
        Draw.alpha((1 - Vars.state.rules.ambientLight.a) * alpha);
        Draw.rect(region, x, y, rotation);
    }

    public static void drawPixmapWeapons(Pixmap stencil, Weapon w, boolean top, boolean pastMainRegion){

        Pixmap outin;

        boolean done = false;

        int progress = 0;

        while (!done){

            String regionName = top && progress == 0 || pastMainRegion ? w.name : w.name + "-outline";

            outin = Core.atlas.getPixmap(regionName).crop();

            if(validateRegion(w.name)) for(int i = 0; i < 2; i++){
                if(!w.mirror && i > 1) break;
                int reverse = 1 - i * 2;
                drawPixmapUnitMount(stencil, outin, (int) w.x, (int) w.y, reverse);
            }
            if(progress < 1 && (top || pastMainRegion)) progress++;
            else done = true;
        }
    }

    public static void drawPixmapUnitMount(Pixmap stencil, Pixmap map, int x, int y, int reverse){
            try {
                try {
                    if(flipMethod != null) stencil = (Pixmap) flipMethod.invoke(stencil);
                    if(drawingMethod != null) drawingMethod.invoke(
                            stencil,
                            map,
                            stencil.getWidth() / 2 - map.getWidth() / 2 + x * 4 * reverse,
                            stencil.getHeight() / 2 - map.getHeight() / 2 - y * 4,
                            0,
                            0,
                            map.getWidth(),
                            map.getHeight());
                }
                catch (InvocationTargetException err) {
                    Log.err("Invoking Methods in Drawr #275 not suported!");
                }
            }
            catch (IllegalAccessException erro){
                Log.err("Unable to acces Methods Drawr #29!");
            }
    }

    public static boolean validateRegion(String weapon){
        return Core.atlas.isFound(Core.atlas.find(weapon)) && validateRegion(Core.atlas.getPixmap(weapon));
    }

    public static boolean validateRegion(PixmapRegion weapon){
        return weapon != Core.atlas.getPixmap("none");
    }

    public static void polyLight(float x, float y, float[] vertices, Color center, Color edge){
        float centerf = center.toFloatBits(), edgef = edge.toFloatBits();
        int size = vertices.length;

        for (int i = 0; i < vertices.length; i += 2) {
            float x1 = vertices[i % size],
                    y1 = vertices[(i % size) + 1],
                    x2 = vertices[(i + 2) % size],
                    y2 = vertices[((i + 2) % size) + 1],
                    x3 = vertices[(i + 4) % size],
                    y3 = vertices[((i + 4) % size) + 1];

            Fill.quad(x, y, centerf, x + x1, y + y1, edgef, x + x2, y + y2, edgef, x + x3, y + y3, edgef);
        }
    }
}

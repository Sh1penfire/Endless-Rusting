package rusting.graphics;

import arc.Core;
import arc.Graphics;
import arc.Graphics.Cursor.SystemCursor;
import arc.backend.sdl.SdlGraphics;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.struct.ObjectMap;
import arc.util.OS;

import static mindustry.ui.Fonts.cursorScale;
import static rusting.EndlessRusting.modname;
import static rusting.graphics.Graphicsr.ReplacementCursor.*;

public class Graphicsr {

    //whether a cursor is replaced or not
    public static ObjectMap<Boolean, SystemCursor> replaced = ObjectMap.of();

    //clones of the original cursors
    public static ObjectMap<SystemCursor, SdlGraphics.SdlCursor> clones = ObjectMap.of();

    public static void loadReplacementCursors(){
        if(Core.app.isAndroid()) return;
        //setup clones so that the cursors that are set can be returned to normal
        arrowClone.set(SystemCursor.arrow);
        ibeamClone.set(SystemCursor.ibeam);
        corsshairClone.set(SystemCursor.crosshair);
        handClone.set(SystemCursor.hand);
        horizontalResizeClone.set(SystemCursor.horizontalResize);
        verticalResizeClone.set(SystemCursor.verticalResize);

        //load modded cursors
        corsair.set(newCursor("corsair", 1));

        clones.putAll(
            ObjectMap.of(
                SystemCursor.arrow,arrowClone,
                SystemCursor.arrow, ibeamClone,
                SystemCursor.arrow, corsshairClone,
                SystemCursor.arrow, handClone,
                SystemCursor.arrow, horizontalResizeClone,
                SystemCursor.arrow, verticalResizeClone
            )
        );
    }


    public enum ReplacementCursor{
        arrowClone,
        ibeamClone,
        corsshairClone,
        handClone,
        horizontalResizeClone,
        verticalResizeClone,
        corsair
        ;

        protected Graphics.Cursor cursor;

        private ReplacementCursor() {

        }

        public void set(Graphics.Cursor cursor) {
            this.cursor = cursor;
        }

        public void dispose() {
            if (this.cursor != null && !(this.cursor instanceof SdlGraphics.SdlCursor)) {
                this.cursor.dispose();
                this.cursor = null;
            }
        }
    }

    //Sets arrow to a corsair.
    public static void corsairCursor(){
        SystemCursor.arrow.set(corsair.cursor);
        Core.graphics.cursor(corsair.cursor);
    }

    //Reset all cursors which have been replaced with mod's cursor to normal.
    public static void resetCursors(){
        replaced.each((replaced, cursor) -> {
            if(replaced) cursor.set(clones.get(cursor));
        });
    }

    //Instead of searching the vanilla directory, search the modded one. Scale handled automatically.
    public static SdlGraphics.SdlCursor newCursor(String filename, int scaleOffset){
        int scale = cursorScale() * scaleOffset;
        Pixmap base = Core.atlas.getPixmap(modname + "-" + filename).crop();
        if(cursorScale() != 1 && !OS.isAndroid && !OS.isIos) {
            Pixmap result = Pixmaps.scale(base, base.getWidth() * scale, base.getHeight() * scale, true);
            base.dispose();
            return (SdlGraphics.SdlCursor) Core.graphics.newCursor(result, result.getWidth() / 2, result.getHeight() / 2);
        }
        else return (SdlGraphics.SdlCursor) Core.graphics.newCursor(base, base.getWidth()/2, base.getHeight()/2);
    }
}
